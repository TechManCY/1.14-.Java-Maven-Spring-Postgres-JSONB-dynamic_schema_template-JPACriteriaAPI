package com.example.postgres_jsonb_dynamic_schema.service;

import com.example.postgres_jsonb_dynamic_schema.model.Employee;
import com.example.postgres_jsonb_dynamic_schema.model.EmployeeMetadata;
import com.example.postgres_jsonb_dynamic_schema.repository.EmployeeMetadataRepo;
import com.example.postgres_jsonb_dynamic_schema.repository.EmployeeRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final EmployeeMetadataService employeeMetadataService;

    public Employee createEmployee(@Valid Employee employee) {
        List<EmployeeMetadata> metadataList = employeeMetadataService.getAllEmployeeMetadata();
        if (employee.getMetadata() != null && !employee.getMetadata().isEmpty()) {
            if (!metadataList.isEmpty()) {
                EmployeeMetadataValidation.validateEmployeeMetadata(employee.getMetadata(), metadataList);
            } else {
                throw new IllegalArgumentException("Metadata key is not required");
            }
        }

        return employeeRepository.save(employee);
    }

    public List<Employee> findByMetadataKeyEqual(String key, Double filterValue) {
        return employeeRepository.findByMetadataKeyEqual(key, filterValue);
    }

    public List<Employee> findByMetadataKeyLessOrEqual(String key, Double filterValue) {
        return employeeRepository.findByMetadataKeyLessOrEqual(key, filterValue);
    }

    public List<Employee> findByMetadataKeyGreaterOrEqual(String key, Double filterValue) {
        return employeeRepository.findByMetadataKeyGreaterOrEqual(key, filterValue);
    }

    public List<Employee> findByMetadataKeyLess(String key, Double filterValue) {
        return employeeRepository.findByMetadataKeyLess(key, filterValue);
    }

    public List<Employee> findByMetadataKeyGreater(String key, Double filterValue) {
        return employeeRepository.findByMetadataKeyGreater(key, filterValue);
    }

}
