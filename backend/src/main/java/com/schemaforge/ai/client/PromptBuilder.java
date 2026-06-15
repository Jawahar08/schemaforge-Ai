package com.schemaforge.ai.client;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class PromptBuilder {

    public String buildSchemaGenerationPrompt(String systemDescription, String normalizationTarget) {
        return """
                You are a senior database architect. Analyze the following system description and generate a complete, production-ready database schema.

                System description: "%s"

                Requirements:
                - Normalize to %s
                - Use UUID for primary keys (type: "UUID")
                - Add created_at and updated_at TIMESTAMP columns to every table
                - Include recommended indexes where appropriate

                Respond with ONLY a JSON object (no markdown, no code fences, no extra commentary) in this exact format:
                {
                  "systemName": "brief system name",
                  "description": "one sentence about the system",
                  "tables": [
                    {
                      "name": "table_name",
                      "description": "what this table stores",
                      "fields": [
                        {
                          "name": "field_name",
                          "type": "DATA_TYPE",
                          "constraints": ["PRIMARY KEY", "NOT NULL", "UNIQUE"],
                          "description": "what this field stores",
                          "references": "other_table.field (only if foreign key)"
                        }
                      ],
                      "indexes": ["CREATE INDEX idx_... ON table_name(column)"]
                    }
                  ],
                  "relationships": [
                    {"from": "table1", "to": "table2", "type": "one-to-many", "description": "how they relate"}
                  ],
                  "normalizationNotes": [
                    {"level": "1NF", "status": "satisfied", "notes": "brief explanation"},
                    {"level": "2NF", "status": "satisfied", "notes": "brief explanation"},
                    {"level": "3NF", "status": "satisfied", "notes": "brief explanation"},
                    {"level": "BCNF", "status": "satisfied", "notes": "brief explanation"}
                  ],
                  "analysisItems": [
                    {"type": "success", "title": "...", "description": "..."}
                  ]
                }
                """.formatted(systemDescription, normalizationTarget.toUpperCase());
    }

    public String buildNormalizationPrompt(List<Map<String, Object>> tables, String normalizationTarget) {
        return """
                You are a database normalization expert. Analyze the following tables and produce a normalization
                report up to %s.

                Tables (JSON): %s

                Respond with ONLY a JSON array (no markdown, no code fences) in this exact format:
                [
                  {"level": "1NF", "status": "satisfied", "notes": "brief explanation"},
                  {"level": "2NF", "status": "satisfied", "notes": "brief explanation"},
                  {"level": "3NF", "status": "satisfied", "notes": "brief explanation"},
                  {"level": "BCNF", "status": "satisfied", "notes": "brief explanation"}
                ]

                "status" must be one of: "satisfied", "violation", "partial".
                """.formatted(normalizationTarget.toUpperCase(), tables);
    }

    public String buildAnalysisPrompt(List<Map<String, Object>> tables, List<Map<String, Object>> relationships) {
        return """
                You are a senior database reviewer. Analyze the following schema for missing relationships,
                redundant tables, poor normalization, and naming issues.

                Tables (JSON): %s

                Relationships (JSON): %s

                Respond with ONLY a JSON array (no markdown, no code fences) in this exact format:
                [
                  {"type": "success", "title": "...", "description": "..."},
                  {"type": "warning", "title": "...", "description": "..."},
                  {"type": "info", "title": "...", "description": "..."}
                ]

                "type" must be one of: "success", "warning", "info", "error".
                """.formatted(tables, relationships);
    }
}