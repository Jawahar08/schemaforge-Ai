package com.schemaforge.schema.diff;

import com.schemaforge.user.entity.User;

import java.util.UUID;

public interface SchemaDiffService {

    /**
     * Compares two version snapshots of the same schema.
     * Caller must own the parent project.
     *
     * @param currentUser authenticated user — ownership validated
     * @param schemaId    the schema to compare versions of
     * @param fromVersion older version number
     * @param toVersion   newer version number
     */
    SchemaDiffResponse diffVersions(
            User currentUser, UUID schemaId, int fromVersion, int toVersion);
}