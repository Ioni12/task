package com.example.task.response;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class CurrencyResponse {
    private String date;
    private Map<String, Map<String, Double>> rates = new HashMap<>();

    @JsonAnySetter
    public void setRates(String key, Map<String, Double> value) {
        rates.put(key, value);
    }
}
