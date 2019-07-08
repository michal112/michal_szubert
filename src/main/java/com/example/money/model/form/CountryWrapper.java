package com.example.money.model.form;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class CountryWrapper {

    private List<Country> countries;
}
