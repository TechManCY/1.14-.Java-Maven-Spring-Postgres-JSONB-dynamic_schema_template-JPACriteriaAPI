package com.example.postgres_jsonb_dynamic_schema.repository;

import com.example.postgres_jsonb_dynamic_schema.model.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    @Query(value = "SELECT * FROM employee WHERE (metadata->>:key)::double precision = :filterValue", nativeQuery = true)
    List<Employee> findByMetadataKeyEqual(
            @Param("key") String key,
            @Param("filterValue") Double filterValue
    );

    @Query(value = "SELECT * FROM employee WHERE (metadata->>:key)::double precision <= :filterValue", nativeQuery = true)
    List<Employee> findByMetadataKeyLessOrEqual(
            @Param("key") String key,
            @Param("filterValue") Double filterValue
    );

    @Query(value = "SELECT * FROM employee WHERE (metadata->>:key)::double precision >= :filterValue", nativeQuery = true)
    List<Employee> findByMetadataKeyGreaterOrEqual(
            @Param("key") String key,
            @Param("filterValue") Double filterValue
    );

    @Query(value = "SELECT * FROM employee WHERE (metadata->>:key)::double precision < :filterValue", nativeQuery = true)
    List<Employee> findByMetadataKeyLess(
            @Param("key") String key,
            @Param("filterValue") Double filterValue
    );

    @Query(value = "SELECT * FROM employee WHERE (metadata->>:key)::double precision > :filterValue", nativeQuery = true)
    List<Employee> findByMetadataKeyGreater(
            @Param("key") String key,
            @Param("filterValue") Double filterValue
    );
}
