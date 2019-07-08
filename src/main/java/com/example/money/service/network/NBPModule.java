package com.example.money.service.network;

import com.example.money.model.form.Country;
import com.example.money.model.network.NBPPayload;
import com.example.money.model.network.NBPRate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Objects;

@Component
public class NBPModule {

    private final static Double DEFAULT_RATE = 1.0D;

    @Value("${nbp.url}")
    private String url;

    private RestTemplate restTemplate;

    public NBPModule(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder.build();
    }

    public Double getCurrencyRate(Country country) {
        var payload = restTemplate.getForObject(String.format(url, country.getLabel()), NBPPayload.class);
        if (Objects.isNull(payload) || Objects.isNull(payload.getRates())) {
            return DEFAULT_RATE;
        }

        return payload.getRates().stream()
                .findAny().map(NBPRate::getRate).orElse(DEFAULT_RATE);
    }
}
