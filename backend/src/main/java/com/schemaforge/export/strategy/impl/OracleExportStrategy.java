package com.schemaforge.export.strategy.impl;

import com.schemaforge.export.entity.ExportDialect;
import com.schemaforge.export.strategy.SqlBuilderSupport;
import com.schemaforge.schema.entity.Schema;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class OracleExportStrategy extends SqlBuilderSupport {

    @Override
    public ExportDialect getDialect() {
        return ExportDialect.ORACLE;
    }

    @Override
    protected String quoteIdentifier(String name) {
        return "\"" + name.toUpperCase() + "\"";
    }

    @Override
    protected String mapColumnType(String aiType) {
        if (aiType == null) return "VARCHAR2(255)";
        return switch (aiType.toUpperCase().trim()) {
            case "UUID"                              -> "VARCHAR2(36)";
            case "INT", "INTEGER", "SMALLINT"        -> "NUMBER(10)";
            case "BIGINT"                            -> "NUMBER(19)";
            case "DECIMAL", "NUMERIC"                -> "NUMBER(19,4)";
            case "FLOAT", "DOUBLE", "REAL"           -> "FLOAT";
            case "BOOLEAN", "BOOL"                   -> "NUMBER(1)";
            case "TEXT"                              -> "CLOB";
            case "DATE"                              -> "DATE";
            case "TIME"                              -> "TIMESTAMP";
            case "TIMESTAMP", "DATETIME", "TIMESTAMPTZ" -> "TIMESTAMP WITH TIME ZONE";
            case "JSONB", "JSON"                     -> "CLOB";
            case "SERIAL"                            -> "NUMBER(10) GENERATED ALWAYS AS IDENTITY";
            case "BIGSERIAL"                         -> "NUMBER(19) GENERATED ALWAYS AS IDENTITY";
            default -> aiType.toUpperCase().startsWith("VARCHAR")
                    ? aiType.toUpperCase().replace("VARCHAR", "VARCHAR2")
                    : "VARCHAR2(255)";
        };
    }

    @Override
    protected String autoIncrementClause() {
        return "GENERATED ALWAYS AS IDENTITY";
    }

    @Override
    protected String adaptIndex(String rawIndex) {
        // Oracle uses uppercase double-quoted identifiers; append /
        return rawIndex.toUpperCase() + ";";
    }

    

    @Override
    protected void appendFooter(StringBuilder sb) {
        sb.append(NEWLINE);
        sb.append("-- Run COMMIT after DDL execution in Oracle SQL*Plus").append(NEWLINE);
        sb.append("COMMIT;").append(NEWLINE);
    }
}