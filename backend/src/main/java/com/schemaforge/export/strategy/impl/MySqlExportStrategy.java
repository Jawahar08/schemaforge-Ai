package com.schemaforge.export.strategy.impl;

import com.schemaforge.export.entity.ExportDialect;
import com.schemaforge.export.strategy.SqlBuilderSupport;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class MySqlExportStrategy extends SqlBuilderSupport {

    @Override
    public ExportDialect getDialect() {
        return ExportDialect.MYSQL;
    }

    @Override
    protected String quoteIdentifier(String name) {
        return "`" + name + "`";
    }

    @Override
    protected String mapColumnType(String aiType) {
        if (aiType == null) return "VARCHAR(255)";
        return switch (aiType.toUpperCase().trim()) {
            case "UUID"                              -> "CHAR(36)";
            case "INT", "INTEGER"                    -> "INT";
            case "BIGINT"                            -> "BIGINT";
            case "SMALLINT"                          -> "SMALLINT";
            case "DECIMAL", "NUMERIC"                -> "DECIMAL(19,4)";
            case "FLOAT", "DOUBLE", "REAL"           -> "DOUBLE";
            case "BOOLEAN", "BOOL"                   -> "TINYINT(1)";
            case "TEXT"                              -> "TEXT";
            case "DATE"                              -> "DATE";
            case "TIME"                              -> "TIME";
            case "TIMESTAMP", "DATETIME", "TIMESTAMPTZ" -> "DATETIME";
            case "JSONB", "JSON"                     -> "JSON";
            case "SERIAL"                            -> "INT AUTO_INCREMENT";
            case "BIGSERIAL"                         -> "BIGINT AUTO_INCREMENT";
            default -> aiType.toUpperCase().startsWith("VARCHAR") ? aiType.toUpperCase() : "VARCHAR(255)";
        };
    }

    @Override
    protected String autoIncrementClause() {
        return "AUTO_INCREMENT";
    }

    @Override
    protected String adaptIndex(String rawIndex) {
        // Replace double-quoted identifiers with backtick-quoted ones.
        return rawIndex.replace("\"", "`") + ";";
    }

    @Override
    protected void appendCreateTable(StringBuilder sb, java.util.Map<String, Object> table) {
        // Delegate to base, then append ENGINE=InnoDB DEFAULT CHARSET=utf8mb4
        // We do this by capturing the output and replacing the closing semicolon.
        StringBuilder inner = new StringBuilder();
        super.appendCreateTable(inner, table);
        String base = inner.toString();
        // Replace terminal ");" with ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;"
        sb.append(base.replace(");", ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;"));
    }
}