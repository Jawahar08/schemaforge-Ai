package com.schemaforge.export.strategy.impl;

import com.schemaforge.export.entity.ExportDialect;
import com.schemaforge.export.strategy.SqlBuilderSupport;
import org.springframework.stereotype.Component;

@Component
public class PostgreSqlExportStrategy extends SqlBuilderSupport {

    @Override
    public ExportDialect getDialect() {
        return ExportDialect.POSTGRESQL;
    }

    @Override
    protected String quoteIdentifier(String name) {
        return "\"" + name + "\"";
    }

    @Override
    protected String mapColumnType(String aiType) {
        if (aiType == null) return "VARCHAR(255)";
        return switch (aiType.toUpperCase().trim()) {
            case "UUID"                              -> "UUID";
            case "INT", "INTEGER"                    -> "INTEGER";
            case "BIGINT"                            -> "BIGINT";
            case "SMALLINT"                          -> "SMALLINT";
            case "DECIMAL", "NUMERIC"                -> "NUMERIC(19,4)";
            case "FLOAT", "DOUBLE", "REAL"           -> "DOUBLE PRECISION";
            case "BOOLEAN", "BOOL"                   -> "BOOLEAN";
            case "TEXT"                              -> "TEXT";
            case "DATE"                              -> "DATE";
            case "TIME"                              -> "TIME";
            case "TIMESTAMP", "DATETIME"             -> "TIMESTAMPTZ";
            case "JSONB"                             -> "JSONB";
            case "JSON"                              -> "JSONB";
            case "SERIAL"                            -> "SERIAL";
            case "BIGSERIAL"                         -> "BIGSERIAL";
            default -> aiType.toUpperCase().startsWith("VARCHAR") ? aiType.toUpperCase() : "VARCHAR(255)";
        };
    }

    @Override
    protected String autoIncrementClause() {
        return "SERIAL";
    }

    @Override
    protected String adaptIndex(String rawIndex) {
        // PostgreSQL uses double-quoted identifiers — return as-is since
        // AI-generated indexes already target PostgreSQL syntax.
        return rawIndex + ";";
    }
}