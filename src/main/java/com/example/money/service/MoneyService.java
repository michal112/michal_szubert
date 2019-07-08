package com.example.money.service;

import com.example.money.model.configuration.DeutschlandConfiguration;
import com.example.money.model.configuration.EnglandConfiguration;
import com.example.money.model.configuration.PolandConfiguration;
import com.example.money.model.form.Country;
import com.example.money.model.form.CountryWrapper;
import com.example.money.service.converter.MoneyConverter;
import com.example.money.service.network.NBPModule;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class MoneyService {

    private final DeutschlandConfiguration deutschlandConfiguration;

    private final EnglandConfiguration englandConfiguration;

    private final PolandConfiguration polandConfiguration;

    private final MoneyConverter moneyConverter;

    private final NBPModule nbpModule;

    public MoneyService(DeutschlandConfiguration deutschlandConfiguration,
                        EnglandConfiguration englandConfiguration,
                        PolandConfiguration polandConfiguration,
                        MoneyConverter moneyConverter,
                        NBPModule nbpModule) {
        this.deutschlandConfiguration = deutschlandConfiguration;
        this.englandConfiguration = englandConfiguration;
        this.polandConfiguration = polandConfiguration;
        this.moneyConverter = moneyConverter;
        this.nbpModule = nbpModule;
    }

    public CountryWrapper generateCountries() {
        List<Country> countries = new ArrayList<>();
        countries.add(new Country(deutschlandConfiguration));
        countries.add(new Country(englandConfiguration));
        countries.add(new Country(polandConfiguration));
        return new CountryWrapper(countries);
    }

    public CountryWrapper generateCountriesWithResult(CountryWrapper countryWrapper) {
        moneyConverter.generateNetPrice(countryWrapper.getCountries());

        countryWrapper.getCountries().stream()
                .filter(country -> !country.getLabel().equals(polandConfiguration.getLabel()))
                .forEach(country -> country.setNetPrice(country.getNetPrice().multiply(
                            BigDecimal.valueOf(nbpModule.getCurrencyRate(country))
                    ))
        );

        return countryWrapper;
    }
}
