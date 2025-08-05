package com.example.postgres_jsonb_dynamic_schema.repository;

import com.example.postgres_jsonb_dynamic_schema.model.EmployeeMetadata;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmployeeMetadataRepo extends JpaRepository<EmployeeMetadata, Long> {
}
