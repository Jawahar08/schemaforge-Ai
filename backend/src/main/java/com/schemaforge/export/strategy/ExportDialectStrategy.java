package com.schemaforge.export.strategy;

import com.schemaforge.export.entity.ExportDialect;
import com.schemaforge.schema.entity.Schema;

public interface ExportDialectStrategy {

    /**
     * Returns the dialect this strategy handles.
     * Used by {@link ExportStrategyFactory} to route requests.
     */
    ExportDialect getDialect();

    /**
     * Generates a complete, executable DDL script from the provided schema.
     *
     * @param schema the schema entity whose tables_json and relationships_json are read
     * @return the full SQL script as a String
     */
    String generateSql(Schema schema);
}