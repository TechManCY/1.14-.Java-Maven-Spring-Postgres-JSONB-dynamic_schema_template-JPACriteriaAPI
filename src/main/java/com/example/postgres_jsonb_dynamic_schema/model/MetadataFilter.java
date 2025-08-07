package com.example.postgres_jsonb_dynamic_schema.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Data;

@Data
public class MetadataFilter {
    private String key;                  // JSONB key
    @Enumerated(EnumType.STRING)
    @JsonProperty("FilterOperation")
    private FilterOperation operation;  // enum: EQUAL, LIKE, GT, LT, GTE, LTE
    private Object value;               // Filter value (string, number, etc.)

    public enum FilterOperation {
        EQUAL,
        LIKE,
        GREATER_THAN,
        LESS_THAN,
        GREATER_OR_EQUAL,
        LESS_OR_EQUAL
    }
}
