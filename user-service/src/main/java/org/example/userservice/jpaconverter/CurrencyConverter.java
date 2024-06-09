package org.example.userservice.jpaconverter;

import jakarta.persistence.AttributeConverter;

import java.util.Currency;

public class CurrencyConverter implements AttributeConverter<Currency, String> {

    @Override
    public String convertToDatabaseColumn(Currency currency) {
        return currency.getCurrencyCode();
    }

    @Override
    public Currency convertToEntityAttribute(String s) {
        return Currency.getInstance(s);
    }
}
