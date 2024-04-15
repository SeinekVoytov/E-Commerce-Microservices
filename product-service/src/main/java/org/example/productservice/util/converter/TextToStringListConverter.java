package org.example.productservice.util.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Converter
public class TextToStringListConverter implements AttributeConverter<List<String>, String> {

    private static final String DELIMITER = ";";

    @Override
    public String convertToDatabaseColumn(List<String> stringList) {
        return stringList != null ? String.join(DELIMITER, stringList) : "";
    }

    @Override
    public List<String> convertToEntityAttribute(String string) {
        return string != null ? new ArrayList<>(Arrays.asList(string.split(DELIMITER))) : new ArrayList<>();
    }
}
