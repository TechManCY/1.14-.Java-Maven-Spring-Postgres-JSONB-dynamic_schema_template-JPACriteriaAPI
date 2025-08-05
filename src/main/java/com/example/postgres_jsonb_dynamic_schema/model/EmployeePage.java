package com.example.postgres_jsonb_dynamic_schema.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Sort;

@Data @AllArgsConstructor @NoArgsConstructor
public class EmployeePage {
    private int pageNumber = 0 ;
    private int pageSize = 10 ;
    private Sort.Direction sortDirection = Sort.Direction.ASC;
    private String sortBy = "lastName";

}

