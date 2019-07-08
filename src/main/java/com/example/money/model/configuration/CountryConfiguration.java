package com.example.money.model.configuration;

public interface CountryConfiguration {

    Double getTax();

    Double getCost();

    String getLabel();

    String getName();

    default String getConfiguration() {
        return "Configuration{" +
                "name='" + getName() + '\'' +
                ", label='" + getLabel() + '\'' +
                ", tax=" + getTax() +
                ", cost=" + getCost() +
                '}';
    }
}
