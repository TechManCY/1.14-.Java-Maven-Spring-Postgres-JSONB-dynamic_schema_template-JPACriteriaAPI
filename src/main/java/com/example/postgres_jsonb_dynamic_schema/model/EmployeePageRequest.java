package com.example.postgres_jsonb_dynamic_schema.model;

import lombok.Data;

@Data
public class EmployeePageRequest {

    private EmployeePage employeePage;
    private EmployeeSearchCriteria employeeSearchCriteria;
}
