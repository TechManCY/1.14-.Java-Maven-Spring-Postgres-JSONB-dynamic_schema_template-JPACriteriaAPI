package com.example.postgres_jsonb_dynamic_schema.controller;

import com.example.postgres_jsonb_dynamic_schema.model.Employee;
import com.example.postgres_jsonb_dynamic_schema.model.EmployeePage;
import com.example.postgres_jsonb_dynamic_schema.model.EmployeePageRequest;
import com.example.postgres_jsonb_dynamic_schema.model.EmployeeSearchCriteria;
import com.example.postgres_jsonb_dynamic_schema.service.EmployeeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/employee")
@RequiredArgsConstructor
public class EmployeeController {

    private final EmployeeService employeeService;

    @GetMapping("/get")
    @ResponseStatus(HttpStatus.OK)
    public List<Employee> findAllEmployee(){
        return employeeService.findAllEmployee();

    }

    @PostMapping("/create")
    @ResponseStatus(HttpStatus.CREATED)
    public Employee createEmployee(@Valid @RequestBody Employee employee){
        return employeeService.createEmployee(employee);

    }

    @PostMapping("/search")
    public ResponseEntity<Page<Employee>> getEmployeesByCriteria(@RequestBody EmployeePageRequest employeePageRequest) {
        EmployeePage employeePage = employeePageRequest.getEmployeePage();
        EmployeeSearchCriteria employeeSearchCriteria = employeePageRequest.getEmployeeSearchCriteria();
        return new ResponseEntity<>(employeeService.getEmployeesByCriteria(employeePage, employeeSearchCriteria), HttpStatus.OK);
    }

}
