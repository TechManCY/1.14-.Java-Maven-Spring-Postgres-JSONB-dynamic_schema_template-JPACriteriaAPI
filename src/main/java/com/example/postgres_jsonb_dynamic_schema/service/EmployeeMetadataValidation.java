package com.example.postgres_jsonb_dynamic_schema.service;

import com.example.postgres_jsonb_dynamic_schema.model.EmployeeMetadata;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class EmployeeMetadataValidation {

    public static void validateEmployeeMetadata(Map<String, Object> metadata, List<EmployeeMetadata> template) {
        for(EmployeeMetadata attribute : template) {
            Object value = metadata.get(attribute.getKey());
            if(attribute.isRequired() && (value == null)) {
                throw new IllegalArgumentException("Missing required metadata key: " + attribute.getKey());
            }
            if(value != null && !isValueCompatibleWithType(value, attribute.getDataType())) {
                throw new IllegalArgumentException("Metadata key " + attribute.getKey() +
                        " expects type " + attribute.getDataType() +
                        " but got " + value.getClass() + " with value: " + value);

            }
        }

        Set<String> validKeys = template.stream()
                .map(EmployeeMetadata::getKey)
                .collect(Collectors.toSet());
        for (String key : metadata.keySet()) {
            if (!validKeys.contains(key)) {
                throw new IllegalArgumentException("Unexpected metadata key: " + key);
            }
        }

    }

    private static boolean isValueCompatibleWithType(Object value, EmployeeMetadata.DataType dataType) {
        switch(dataType) {
            case STRING: return value instanceof String;
            case INTEGER: return value instanceof Integer || value instanceof Long;
            case LONG: return value instanceof Long || value instanceof Integer;
            case DOUBLE: return value instanceof Double || value instanceof Float;
            case BOOLEAN: return value instanceof Boolean;
            default: return false;
        }
    }
}


