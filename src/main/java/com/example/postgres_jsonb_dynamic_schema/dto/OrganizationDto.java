package com.example.postgres_jsonb_dynamic_schema.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrganizationDto {

    private String name;
    private String alias;
    private String domain;
    private String id;

}
