package com.shibadog.lutrinae.domain.mizuho;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.AbstractMap.SimpleEntry;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.shibadog.lutrinae.domain.entity.Rate;
import com.shibadog.lutrinae.domain.exception.ApplicationException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import lombok.val;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class MizuhoRateService {
    @Value("${app.mizuho.daily}")
    private URL dailyData;

    @Value("${app.mizuho.allDaily}")
    private URL dailyAllData;

    @Value("${app.mizuho.border}")
    private LocalDate border;

    private LocalDate minimumDate = LocalDate.of(2002, 4, 1);

    public boolean beforeBorder(LocalDate date) {
        return date.compareTo(border) > 0;
    }

    @Cacheable("mizuho.rate")
    public Optional<Rate> findRate(LocalDate date) throws ApplicationException {
        if (date.compareTo(minimumDate) < 0) throw new ApplicationException("対応していない日付です。");
        final URL accessUrl = beforeBorder(date) ? this.dailyData : this.dailyAllData;
        log.debug("accessUrl: " + accessUrl.toString());

        Optional<Rate> rate = Optional.empty();

        try {
            final URLConnection con = accessUrl.openConnection();

            try (final BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()))) {
                // 3行分はヘッダ
                br.readLine();
                br.readLine();
                final val header2 = Optional.ofNullable(br.readLine());

                if (header2.isPresent()) {
                    final List<String> headerList = Arrays.asList(header2.get().split(",", -1));
                    final int endIndex = headerList.lastIndexOf("");

                    rate = br.lines()
                        .map(line -> parseRate(headerList, endIndex, line))
                        .filter(line -> line.getDate().equals(date.format(DateTimeFormatter.ofPattern("yyyy/M/d"))))
                        .findFirst();
                }
            }
        } catch (IOException ex) {
            throw new ApplicationException("リクエストに失敗しました。", ex);
        }

        log.debug("rate: " + rate.toString());
        return rate;
    }

    private Rate parseRate(final List<String> headerList, final int endIndex, final String line) {
        final List<String> lineColumns = Arrays.asList(line.split(",", -1));
        final Iterator<String> lineIte = lineColumns.iterator();

        return Rate.builder()
            .date(lineIte.next())
            .rate(parseRate2(headerList.stream().limit(endIndex).skip(1), lineIte)).build();
    }

    private Map<String, Optional<Double>> parseRate2(final Stream<String> headers, final Iterator<String> lineIte) {
        return Collections.unmodifiableMap(headers.map((head) -> new SimpleEntry<>(head, lineIte.next()))
            .collect(Collectors.toMap(e -> e.getKey(), this::parseDouble)));
    }

    private Optional<Double> parseDouble(SimpleEntry<String, String> e) {
        return isDouble(e.getValue()) ? Optional.of(Double.valueOf(e.getValue())) : Optional.empty();
    }

    private static boolean isDouble(String input) {
        try {
            Double.parseDouble(input);
            return true;
        } catch (NumberFormatException e) {
            //You can send the message which you want to show to your users here.
            return false;
        }
    }
}