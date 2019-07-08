package com.example.money.service.converter;

import com.example.money.model.form.Country;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class MoneyConverter {

    public List<Country> generateNetPrice(List<Country> countries) {
        return countries.stream()
                .map(this::generateNetPrice)
                .collect(Collectors.toList());
    }

    private Country generateNetPrice(Country country) {
        var salary = country.getGrossPrice().multiply(BigDecimal.valueOf(22)).subtract(BigDecimal.valueOf(country.getCost()));
        var tax = salary.multiply(BigDecimal.valueOf(country.getTax() / 100));

        country.setNetPrice(salary.subtract(tax));
        return country;
    }
}
