package com.example.task.utils;

import com.example.task.response.CurrencyResponse;

import java.util.Map;

public final class CurrencyConverter {

    private CurrencyConverter(){}

    public static double convertDeposit(CurrencyResponse response, String fromCurrency, String accountCurrency, double amount) {
        return convert(response, fromCurrency, accountCurrency, amount);
    }

    public static double convertWithdraw(CurrencyResponse response, String accountCurrency, String toCurrency, double amount) {
        return convert(response, accountCurrency, toCurrency, amount);
    }

    //nga usd ne eur ose nga eur ne usd varet si i vendos
    private static double convert(CurrencyResponse response, String from, String to, double amount){
        System.out.println("Rates keys: " + response.getRates().keySet());
        System.out.println("From: " + from + ", To: " + to);
        Map<String, Double> rates = response.getRates().get(from.toLowerCase());
        if (rates == null) {
            throw new IllegalArgumentException("No rates found for currency: " + from);
        }

        Double rate = rates.get(to.toLowerCase());
        if (rate == null) {
            throw new IllegalArgumentException("No rate found for target currency: " + to);
        }

        return amount * rate;
    }

}
