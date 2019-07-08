package com.example.money.controller;

import com.example.money.model.form.CountryWrapper;
import com.example.money.service.MoneyService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/app")
public class MoneyController {

    private final MoneyService moneyService;

    public MoneyController(MoneyService moneyService) {
        this.moneyService = moneyService;
    }

    @GetMapping("/money")
    public String getFormView(Model model) {
        model.addAttribute("wrapper", moneyService.generateCountries());
        return "countries";
    }

    @PostMapping("/money/result")
    public String geResultView(Model model, @ModelAttribute CountryWrapper countryWrapper) {
        model.addAttribute("wrapper", moneyService.generateCountriesWithResult(countryWrapper));
        return "countriesWithResult";
    }
}
