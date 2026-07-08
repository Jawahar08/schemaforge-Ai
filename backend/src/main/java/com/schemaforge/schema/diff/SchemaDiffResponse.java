package com.schemaforge.schema.diff;

import java.util.List;
import java.util.UUID;

public record SchemaDiffResponse(
        UUID schemaId,
        int fromVersion,
        int toVersion,
        List<String> tablesAdded,
        List<String> tablesRemoved,
        List<TableDiff> tablesModified,
        List<String> relationshipsAdded,
        List<String> relationshipsRemoved,
        int totalChanges
) {
}