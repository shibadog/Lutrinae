package com.shibadog.lutrinae.domain.service;

import java.net.URI;
import java.time.LocalDate;
import java.util.Optional;

import com.shibadog.lutrinae.domain.entity.Rate;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class MizuhoRateService {
    @Value("${app.mizuho.daily}")
    private URI dailyData;

    @Value("${app.mizuho.oldDaily}")
    private URI dailyOldData;

    @Value("${app.mizuho.oldBorder}")
    private LocalDate border;

    public Optional<Rate> findRate(String date) {
        return Optional.empty();
    }

}