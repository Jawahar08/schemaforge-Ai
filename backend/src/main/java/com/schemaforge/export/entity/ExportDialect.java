package com.schemaforge.export.entity;

public enum ExportDialect {

    POSTGRESQL("postgresql"),
    MYSQL("mysql"),
    SQLSERVER("sqlserver"),
    ORACLE("oracle");

    private final String value;

    ExportDialect(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static ExportDialect fromValue(String value) {
        if (value == null) {
            return null;
        }
        for (ExportDialect dialect : values()) {
            if (dialect.value.equalsIgnoreCase(value)) {
                return dialect;
            }
        }
        throw new IllegalArgumentException("Unknown export dialect: " + value);
    }
}