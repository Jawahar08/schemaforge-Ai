package com.schemaforge.schema.diff;

public record ColumnDiff(
        String name,
        String oldType,
        String newType,
        boolean constraintsChanged,
        String oldConstraints,
        String newConstraints
) {
}