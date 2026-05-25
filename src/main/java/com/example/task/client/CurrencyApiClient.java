package com.example.task.client;

import com.example.task.response.CurrencyResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;


@Component
public class CurrencyApiClient {

    private final RestTemplate restTemplate;

    public CurrencyApiClient() {
        this.restTemplate = new RestTemplate();
    }

    public CurrencyResponse getCurrency(String currency) {
        return restTemplate.getForObject(
                "https://cdn.jsdelivr.net/npm/@fawazahmed0/currency-api@latest/v1/currencies/" + currency.toLowerCase() + ".json",
                CurrencyResponse.class
        );

    }
}
