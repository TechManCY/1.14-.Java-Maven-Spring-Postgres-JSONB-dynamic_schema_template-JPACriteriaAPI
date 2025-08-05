package com.example.postgres_jsonb_dynamic_schema.model;


import lombok.Data;

import java.util.List;

@Data
public class EmployeeSearchCriteria {
    private String employeeId;
    private String firstName;
    private String lastName;
    //private Map<String, Object> metadataFilters;
    private List<MetadataFilter> metadataFilters;

}
