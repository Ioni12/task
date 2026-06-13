package com.example.task.utils;

import java.math.BigInteger;
import java.util.Random;

public final class IbanGenerator {

    private static final String COUNTRY_CODE = "AL";
    private static final String BANK_CODE = "212";
    private static final String BRANCH_CODE = "1100";
    private static final Random RANDOM = new Random();

    private IbanGenerator(){}

    public static String generate(String accountNumber) {
//        String accountNumber = generateAccountNumber();
        String bban = BANK_CODE + BRANCH_CODE + accountNumber;
        String checkDigits = calculateCheckDigits(COUNTRY_CODE, bban);
        return COUNTRY_CODE + checkDigits + bban;
    }

    //al 47 1010 109199
    public static boolean validate(String iban) {
        if (iban == null || iban.length() != 28) return false;
        String rearranged = iban.substring(4) + iban.substring(0, 4);
        String numeric = toNumericString(rearranged);
        return new BigInteger(numeric).mod(BigInteger.valueOf(97)).intValue() == 1;
    }

    // make it sequential increment it by one 
//    private static String generateAccountNumber() {
//        long number = RANDOM.nextLong(1_000_000_000_000_000L, 9_999_999_999_999_999L);
//        return String.valueOf(number);
//    }

    private static String calculateCheckDigits(String countryCode, String bban) {
        String rearranged = bban + countryCode + "00";
        String numeric = toNumericString(rearranged);
        int remainder = new BigInteger(numeric).mod(BigInteger.valueOf(97)).intValue();
        return String.format("%02d", 98 - remainder);
    }

    private static String toNumericString(String input) {
        StringBuilder sb = new StringBuilder();
        for (char c: input.toCharArray()) {
            if (Character.isLetter(c)) {
                sb.append(Character.toUpperCase(c) - 'A' + 10);
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }
}
