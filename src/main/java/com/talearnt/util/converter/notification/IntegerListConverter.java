package com.talearnt.util.converter.notification;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.List;

@Converter
public class IntegerListConverter implements AttributeConverter<List<Integer>, String> {

    @Override
    public String convertToDatabaseColumn(List<Integer> attribute) {
        return ListStringConverter.integerListToString(attribute);
    }

    @Override
    public List<Integer> convertToEntityAttribute(String dbData) {
        return ListStringConverter.stringToIntegerList(dbData);
    }
}
