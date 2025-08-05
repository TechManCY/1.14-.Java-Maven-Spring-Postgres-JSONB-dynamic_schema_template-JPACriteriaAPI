package com.example.postgres_jsonb_dynamic_schema.service;

import com.example.postgres_jsonb_dynamic_schema.model.EmployeeMetadata;
import com.example.postgres_jsonb_dynamic_schema.repository.EmployeeMetadataRepo;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EmployeeMetadataService {

    private final EmployeeMetadataRepo employeeMetadataRepo;

    public EmployeeMetadata createEmployeeMetadata(@Valid EmployeeMetadata employeeMetadata) {
        return employeeMetadataRepo.save(employeeMetadata);
    }

    public List<EmployeeMetadata> getAllEmployeeMetadata() {
        return employeeMetadataRepo.findAll();
    }
}
