package com.schemaforge.export.entity;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class ExportDialectConverter implements AttributeConverter<ExportDialect, String> {

    @Override
    public String convertToDatabaseColumn(ExportDialect attribute) {
        if (attribute == null) {
            return null;
        }
        return attribute.getValue();
    }

    @Override
    public ExportDialect convertToEntityAttribute(String dbData) {
        return ExportDialect.fromValue(dbData);
    }
}