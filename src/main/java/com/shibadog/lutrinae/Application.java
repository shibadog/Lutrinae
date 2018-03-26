package com.shibadog.lutrinae;

import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.Set;
import java.util.TimeZone;

import com.ulisesbocchio.jasyptspringboot.annotation.EnableEncryptableProperties;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.core.convert.ConversionService;
import org.springframework.format.FormatterRegistrar;
import org.springframework.format.datetime.standard.DateTimeFormatterFactoryBean;
import org.springframework.format.datetime.standard.DateTimeFormatterRegistrar;
import org.springframework.format.support.FormattingConversionServiceFactoryBean;

@SpringBootApplication
@EnableEncryptableProperties
@EnableCaching
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    public ConversionService conversionService() {
        Set<FormatterRegistrar> registrars = new HashSet<>();
        registrars.add(dateTimeFormatterRegistrar());
      
        FormattingConversionServiceFactoryBean factory = new FormattingConversionServiceFactoryBean();
        factory.setFormatterRegistrars(registrars);
        factory.afterPropertiesSet();
        return factory.getObject();
    }

    private FormatterRegistrar dateTimeFormatterRegistrar() {
        DateTimeFormatterRegistrar registrar = new DateTimeFormatterRegistrar();
        registrar.setDateTimeFormatter(dateTimeFormatterFactory());
        registrar.setUseIsoFormat(true);
        return registrar;
    }

    private DateTimeFormatter dateTimeFormatterFactory() {
        DateTimeFormatterFactoryBean factory = new DateTimeFormatterFactoryBean();
        factory.setPattern("yyyy-MM-dd HH:mm:ss");
        factory.setTimeZone(TimeZone.getDefault());
        factory.afterPropertiesSet();
        return factory.getObject();
    }
}