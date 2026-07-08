package com.schemaforge.schema.diff;

import java.util.List;

public record TableDiff(
        String tableName,
        List<ColumnDiff> columnsAdded,
        List<ColumnDiff> columnsRemoved,
        List<ColumnDiff> columnsModified
) {
}