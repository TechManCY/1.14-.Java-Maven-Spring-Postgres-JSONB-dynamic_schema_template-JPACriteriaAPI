package com.example.postgres_jsonb_dynamic_schema.controller;

import com.example.postgres_jsonb_dynamic_schema.model.EmployeeMetadata;
import com.example.postgres_jsonb_dynamic_schema.service.EmployeeMetadataService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/employeeMetadata")
@RequiredArgsConstructor
public class EmployeeMetadataController {

    private final EmployeeMetadataService employeeMetadataService;

    @PostMapping("/create")
    @ResponseStatus(HttpStatus.CREATED)
    public EmployeeMetadata createEmployeeMetadata(@Valid @RequestBody EmployeeMetadata employeeMetadata){
        return employeeMetadataService.createEmployeeMetadata(employeeMetadata);

    }

    @GetMapping
    public ResponseEntity<List<EmployeeMetadata>> getAllEmployeeMetadata(){
        return ResponseEntity.ok(employeeMetadataService.getAllEmployeeMetadata());

    }

}
