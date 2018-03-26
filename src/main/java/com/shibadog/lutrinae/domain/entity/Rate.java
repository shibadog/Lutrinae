package com.shibadog.lutrinae.domain.entity;

import java.util.Map;
import java.util.Optional;

import lombok.Builder;
import lombok.Value;

@Value(staticConstructor="of")
@Builder
public class Rate {
    String date;
    Map<String, Optional<Double>> rate;
}