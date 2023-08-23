package com.yondu.knowledgebase.config;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;

import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

@Configuration
public class JacksonConfig {
    private static final String dateFormat = "yyyy-MM-dd";
    private static final String dateTimeFormat = "yyyy-MM-dd HH:mm:ss";

    @Bean
    Jackson2ObjectMapperBuilder jacksonBuilder() {
        Jackson2ObjectMapperBuilder builder = new Jackson2ObjectMapperBuilder();
        ZoneId zoneId = ZoneId.systemDefault();

        ZoneOffset systemDefaultOffset = zoneId.getRules().getOffset(java.time.Instant.now());

        builder.propertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);
        builder.featuresToDisable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        builder.serializers(new LocalDateSerializer(
                DateTimeFormatter.ofPattern(dateFormat)));
        builder.serializers(new LocalDateTimeSerializer(
                DateTimeFormatter.ofPattern(String.format("%s '%s'", dateTimeFormat, systemDefaultOffset.getId()))));
        return builder;
    }
}