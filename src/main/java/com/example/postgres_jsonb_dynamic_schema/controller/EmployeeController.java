package com.example.postgres_jsonb_dynamic_schema.controller;

import com.example.postgres_jsonb_dynamic_schema.model.Employee;
import com.example.postgres_jsonb_dynamic_schema.service.EmployeeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/employee")
@RequiredArgsConstructor
public class EmployeeController {

    private final EmployeeService employeeService;

    @PostMapping("/create")
    @ResponseStatus(HttpStatus.CREATED)
    public Employee createEmployee(@Valid @RequestBody Employee employee){
        return employeeService.createEmployee(employee);

    }
}
