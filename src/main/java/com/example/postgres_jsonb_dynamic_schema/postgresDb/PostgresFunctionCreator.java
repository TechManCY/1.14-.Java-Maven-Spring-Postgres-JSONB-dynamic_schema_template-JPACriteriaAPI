package com.example.postgres_jsonb_dynamic_schema.postgresDb;

import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class PostgresFunctionCreator {

    @PersistenceContext
    private EntityManager entityManager;

    // Or use JdbcTemplate if you prefer JDBC directly

    //run the function-creation SQL once per database during the db creation
    @PostConstruct
    @Transactional
    public void createFunctionIfNotExists() {
        String sql = """
            CREATE OR REPLACE FUNCTION jsonb_extract_path_double(jsonb, text) RETURNS double precision AS $$
            SELECT (jsonb_extract_path_text($1, $2))::double precision;
            $$ LANGUAGE SQL IMMUTABLE STRICT;
            """;

        entityManager.createNativeQuery(sql).executeUpdate();
    }
}
