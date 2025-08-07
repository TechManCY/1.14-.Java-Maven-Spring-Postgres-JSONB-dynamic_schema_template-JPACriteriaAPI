package com.example.postgres_jsonb_dynamic_schema.service;

import com.example.postgres_jsonb_dynamic_schema.model.Employee;
import com.example.postgres_jsonb_dynamic_schema.model.EmployeeMetadata;
import com.example.postgres_jsonb_dynamic_schema.model.EmployeePage;
import com.example.postgres_jsonb_dynamic_schema.model.EmployeeSearchCriteria;
import com.example.postgres_jsonb_dynamic_schema.repository.EmployeeCriteriaRepo;
import com.example.postgres_jsonb_dynamic_schema.repository.EmployeeMetadataRepo;
import com.example.postgres_jsonb_dynamic_schema.repository.EmployeeRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
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
    private final EmployeeCriteriaRepo employeeCriteriaRepo;
    private final EmployeeMetadataRepo employeeMetadataRepo;

    public Employee createEmployee(@Valid Employee employee) {
        List<EmployeeMetadata> metadataList = employeeMetadataService.getAllEmployeeMetadata();
        if (!metadataList.isEmpty()) {
            if (employee.getMetadata() != null && !employee.getMetadata().isEmpty()) {
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

    public Page<Employee> getEmployeesByCriteria(EmployeePage employeePage,
                                                 EmployeeSearchCriteria employeeSearchCriteria){
        List<EmployeeMetadata> employeeMetadataList = employeeMetadataRepo.findAll();
        return employeeCriteriaRepo.findAllWithFilters(employeePage, employeeSearchCriteria, employeeMetadataList);
    }

    public List<Employee> findAllEmployee() {
        return employeeRepository.findAll();
    }
}
