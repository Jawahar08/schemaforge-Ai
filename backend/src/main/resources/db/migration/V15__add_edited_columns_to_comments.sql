-- ============================================================
-- V15__add_edited_columns_to_comments.sql
-- Add support for editing tracking in comments table
-- ============================================================

ALTER TABLE comments ADD COLUMN edited BOOLEAN NOT NULL DEFAULT false;
ALTER TABLE comments ADD COLUMN edited_at TIMESTAMPTZ;
