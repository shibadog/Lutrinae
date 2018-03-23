package com.shibadog.lutrinae.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Rate {
    private String provider;
    private String date;
}