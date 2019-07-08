package com.example.money.data;

import com.example.money.model.network.NBPPayload;
import com.example.money.model.network.NBPRate;

import java.util.Collections;

public class NBPPayloadFactory {

    public final static Double DEFAULT_RATE = 2.0;

    public static NBPPayload createNBPPayload() {
        var nbpPayload = new NBPPayload();
        var nbpRate = new NBPRate();
        nbpRate.setRate(DEFAULT_RATE);

        nbpPayload.setRates(Collections.singletonList(nbpRate));
        return nbpPayload;
    }
}
