package com.shibadog.lutrinae.domain.controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import com.shibadog.lutrinae.domain.entity.Rate;
import com.shibadog.lutrinae.domain.mizuho.MizuhoRateService;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/v1/rate")
@RequiredArgsConstructor
public class RateRequestController {
    private final MizuhoRateService service;

    @RequestMapping("/{provider}/")
    public Rate getRate(final @PathVariable String provider) throws Exception {
        return getRate(provider, LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
    }

    @RequestMapping("/{provider}/{date}")
    public Rate getRate(final @PathVariable String provider, final @PathVariable String date) throws Exception {
        final LocalDate dateConv = LocalDate.parse(date);
        Optional<Rate> rate = Optional.empty();
        switch(provider) {
            case "mizuho":
                rate = service.findRate(dateConv);
                break;
            default:
                throw new IllegalAccessError("未実装のプロバイダが指定されました。{" + provider + "}");
        }

        return rate.orElseThrow(() -> new Exception("アプリケーション実行エラーです。"));
    }

    @RequestMapping("/{provider}/{date}/{code}")
    public Rate getRate(final @PathVariable String provider, final @PathVariable String date, final @PathVariable String code) throws Exception {
        Rate rate = getRate(provider, date);
        Map<String, Optional<Double>> ratePickMap = new HashMap<>();
        ratePickMap.put(code, rate.getRate().getOrDefault(code, Optional.empty()));

        return Rate.builder()
            .date(date)
            .rate(ratePickMap)
            .build();
    }
}