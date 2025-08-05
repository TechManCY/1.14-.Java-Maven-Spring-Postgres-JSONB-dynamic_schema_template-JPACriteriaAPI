package com.example.postgres_jsonb_dynamic_schema.model;

import jakarta.persistence.*;
import lombok.*;
import jakarta.persistence.Id;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeMetadata {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;
    @NonNull
    private String key;
    private String description;
    @NonNull
    @Enumerated(EnumType.STRING)
    private DataType dataType; // e.g., "string", "number", "boolean", etc.
    @NonNull
    private boolean required;

    public enum DataType {
        STRING, INTEGER, DOUBLE, LONG, BOOLEAN
    }

}
