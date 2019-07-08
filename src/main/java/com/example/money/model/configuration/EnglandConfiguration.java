package com.example.money.model.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@Data
@ConfigurationProperties(prefix = "england")
public class EnglandConfiguration implements CountryConfiguration{

    private String label;

    private Double tax;

    private Double cost;

    private String name;
}
