package com.shibadog.lutrinae.domain.controller;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import com.shibadog.lutrinae.domain.entity.Rate;
import com.shibadog.lutrinae.domain.service.MizuhoRateService;

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
        Optional<Rate> rate = Optional.empty();
        switch(provider) {
            case "mizuho":
                rate = service.findRate(date);
                break;
            default:
                throw new IllegalAccessError("未実装のプロバイダが指定されました。{" + provider + "}");
        }

        return rate.orElseThrow(() -> new Exception("アプリケーション実行エラーです。"));
    }
}