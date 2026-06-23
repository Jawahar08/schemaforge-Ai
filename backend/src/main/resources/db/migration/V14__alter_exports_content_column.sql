-- ============================================================
-- V13__alter_exports_content_column.sql
-- The exports.file_url column was originally VARCHAR(1024), sized
-- for a URL to an external file store. SchemaForge AI stores the
-- full generated SQL script directly in this column, which can
-- easily exceed 1024 characters for any non-trivial schema.
-- This migration widens it to TEXT.
-- ============================================================

ALTER TABLE exports ALTER COLUMN file_url TYPE TEXT;

COMMENT ON COLUMN exports.file_url IS
    'Stores the full generated SQL script text for SQL exports. '
    'Repurposed from a file URL column; kept as file_url to avoid '
    'breaking the V8 migration history.';