package com.example.money.model.form;

import com.example.money.model.configuration.CountryConfiguration;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
public class Country {

    private String name;

    private String label;

    private Double tax;

    private Double cost;

    private BigDecimal netPrice;

    private BigDecimal grossPrice;

    public Country(CountryConfiguration configuration) {
        this.label = configuration.getLabel();
        this.cost = configuration.getCost();
        this.tax = configuration.getTax();
        this.name = configuration.getName();
    }

    public String getConfiguration() {
        return "Configuration{" +
                "name='" + name + '\'' +
                ", label='" + label + '\'' +
                ", tax=" + tax +
                ", cost=" + cost +
                '}';
    }
}
