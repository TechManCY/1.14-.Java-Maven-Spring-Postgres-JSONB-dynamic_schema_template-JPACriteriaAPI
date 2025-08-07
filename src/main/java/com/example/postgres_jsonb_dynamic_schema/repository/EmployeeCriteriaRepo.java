package com.example.postgres_jsonb_dynamic_schema.repository;

import com.example.postgres_jsonb_dynamic_schema.model.Employee;
import com.example.postgres_jsonb_dynamic_schema.model.EmployeeMetadata;
import com.example.postgres_jsonb_dynamic_schema.model.EmployeePage;
import com.example.postgres_jsonb_dynamic_schema.model.EmployeeSearchCriteria;
import com.example.postgres_jsonb_dynamic_schema.model.MetadataFilter;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Repository
public class EmployeeCriteriaRepo {

    private final EntityManager entityManager;
    private final CriteriaBuilder criteriaBuilder;

    public EmployeeCriteriaRepo(EntityManager entityManager){
        this.entityManager = entityManager;
        this.criteriaBuilder = entityManager.getCriteriaBuilder();
    }

    public Page<Employee> findAllWithFilters (EmployeePage employeePage,
                                             EmployeeSearchCriteria employeeSearchCriteria,
                                              List<EmployeeMetadata> employeeMetadataList){
        CriteriaQuery<Employee> criteriaQuery = criteriaBuilder.createQuery(Employee.class);
        Root<Employee> employeeRoot = criteriaQuery.from(Employee.class);
        Predicate predicate = getPredicate(employeeSearchCriteria, employeeMetadataList, employeeRoot);
        criteriaQuery.where(predicate);
        setOrder(employeePage,criteriaQuery,employeeRoot);

        TypedQuery<Employee> typedQuery = entityManager.createQuery(criteriaQuery);
        typedQuery.setFirstResult(employeePage.getPageNumber()*employeePage.getPageSize());
        typedQuery.setMaxResults(employeePage.getPageSize());

        Pageable pageable = getPageable(employeePage);

        long employeeCount = getEmployeeCount(employeeSearchCriteria, employeeMetadataList);

        return new PageImpl<>(typedQuery.getResultList(),pageable, employeeCount);
    }

    private long getEmployeeCount(EmployeeSearchCriteria employeeSearchCriteria, List<EmployeeMetadata> employeeMetadataList) {
        CriteriaQuery<Long> countQuery = criteriaBuilder.createQuery(Long.class);
        Root<Employee> countRoot = countQuery.from(Employee.class);

        Predicate predicate = getPredicate(employeeSearchCriteria, employeeMetadataList, countRoot);

        countQuery.select(criteriaBuilder.count(countRoot));
        if (predicate != null) {
            countQuery.where(predicate);
        }
        return entityManager.createQuery(countQuery).getSingleResult();
    }

    private Pageable getPageable(EmployeePage employeePage) {
        Sort sort = Sort.by(employeePage.getSortDirection(), employeePage.getSortBy());
        return PageRequest.of(employeePage.getPageNumber(), employeePage.getPageSize(), sort);
    }

    private void setOrder(EmployeePage employeePage,
                          CriteriaQuery<Employee> criteriaQuery,
                          Root<Employee> employeeRoot) {
        if (employeePage.getSortDirection().equals(Sort.Direction.ASC)){
            criteriaQuery.orderBy(criteriaBuilder.asc(employeeRoot.get(employeePage.getSortBy())));
        } else {
            criteriaQuery.orderBy(criteriaBuilder.desc(employeeRoot.get(employeePage.getSortBy())));
        }
    }

    private Predicate getPredicate(EmployeeSearchCriteria employeeSearchCriteria,
                                   List<EmployeeMetadata> employeeMetadataList,
                                   Root<Employee> employeeRoot) {
        List<Predicate> predicates = new ArrayList<>();
        if (Objects.nonNull(employeeSearchCriteria.getEmployeeId())){
            predicates.add(
                    criteriaBuilder.like(employeeRoot.get("employeeId"),"%"+employeeSearchCriteria.getEmployeeId() + "%")
            );
        }
        if (Objects.nonNull(employeeSearchCriteria.getFirstName())){
            predicates.add(
                    criteriaBuilder.like(employeeRoot.get("firstName"),"%"+employeeSearchCriteria.getFirstName() + "%")
            );
        }
        if (Objects.nonNull(employeeSearchCriteria.getLastName())){
            predicates.add(
                    criteriaBuilder.like(employeeRoot.get("lastName"),"%"+employeeSearchCriteria.getLastName() + "%")
            );
        }

        if (employeeSearchCriteria.getMetadataFilters() != null) {
//            for (Map.Entry<String, Object> filter : employeeSearchCriteria.getMetadataFilters().entrySet()) {
//            String key = filter.getKey();
//            Object filterValue = filter.getValue();
            for (MetadataFilter filter : employeeSearchCriteria.getMetadataFilters()) {
                String key = filter.getKey();
                MetadataFilter.FilterOperation operation = filter.getOperation();
                Object filterValue = filter.getValue();

                EmployeeMetadata attribute = employeeMetadataList
                        .stream()
                        .filter(attr -> attr.getKey().equals(key))
                        .findFirst()
                        .orElse(null);

                if (attribute == null) {
                    // Either skip or throw error if filter key isn't recognized in template
                    continue;
                }

                String dataType = String.valueOf(attribute.getDataType());

                switch (operation) {
                    // Exact match
                    case EQUAL:
                        {
                        switch (dataType.toLowerCase()) {
                            case "integer":
                            case "long":
                            case "float":
                            case "double":
                            case "bigdecimal": {
                                Expression<Double> jsonValueAsDouble = criteriaBuilder.function(
                                        "jsonb_extract_path_double",
                                        Double.class,
                                        employeeRoot.get("metadata"),
                                        criteriaBuilder.literal(key)
                                );
                                Double numericValue = null;
                                if (filterValue instanceof Number) {
                                    numericValue = ((Number)filterValue).doubleValue();
                                } else if (filterValue instanceof String) {
                                    numericValue = Double.valueOf((String) filterValue);
                                }

                                if (numericValue != null) {
                                    predicates.add(criteriaBuilder.equal(jsonValueAsDouble, numericValue));
                                }
                                break;

                                //predicates.add(criteriaBuilder.equal(jsonValueAsDouble, Double.valueOf((String) filterValue)));
                            }
                            case "boolean": {
                                Expression<String> jsonValue = criteriaBuilder.function(
                                        "jsonb_extract_path_text",
                                        String.class,
                                        employeeRoot.get("metadata"),
                                        criteriaBuilder.literal(key)
                                );
                                Boolean boolFilterValue = (filterValue instanceof Boolean) ? (Boolean) filterValue : Boolean.valueOf(filterValue.toString());
                                predicates.add(criteriaBuilder.equal(criteriaBuilder.lower(jsonValue), boolFilterValue.toString().toLowerCase()));

                                break;
                            }

                            case "string":
                            default: {
                                Expression<String> jsonValue = criteriaBuilder.function(
                                        "jsonb_extract_path_text",
                                        String.class,
                                        employeeRoot.get("metadata"),
                                        criteriaBuilder.literal(key)
                                );
                                predicates.add(criteriaBuilder.equal(criteriaBuilder.lower(jsonValue), filterValue.toString().toLowerCase()));
                                break;
                            }

                        }
                        break;
                        }


                    case LIKE: {
                        Expression<String> jsonValue = criteriaBuilder.function(
                                "jsonb_extract_path_text",
                                String.class,
                                employeeRoot.get("metadata"),
                                criteriaBuilder.literal(key)
                        );
                        switch (dataType.toLowerCase()){
                            case "string":
                            default: {
                                String pattern = "%" + filterValue.toString() + "%";
                                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(jsonValue), pattern.toLowerCase()));
                                break;
                        }}
                        break;
                    }

                    case GREATER_THAN: {
                        switch (dataType.toLowerCase()) {
                        case "integer":
                        case "long":
                        case "float":
                        case "double":
                        case "bigdecimal": {
                            Expression<Double> jsonValueAsDouble = criteriaBuilder.function(
                                    "jsonb_extract_path_double",
                                    Double.class,
                                    employeeRoot.get("metadata"),
                                    criteriaBuilder.literal(key)
                            );

                            Double numericValue = null;
                            if (filterValue instanceof Number) {
                                numericValue = ((Number)filterValue).doubleValue();
                            } else if (filterValue instanceof String) {
                                numericValue = Double.valueOf((String) filterValue);
                            }

                            if (numericValue != null) {
                                predicates.add(criteriaBuilder.greaterThan(jsonValueAsDouble, numericValue));
                            }
                            break;

                            //predicates.add(criteriaBuilder.greaterThan(jsonValueAsDouble, Double.valueOf((String) filterValue)));
                        }
                    }
                    break;
                }

                    case GREATER_OR_EQUAL: {
                        switch (dataType.toLowerCase()) {
                            case "integer":
                            case "long":
                            case "float":
                            case "double":
                            case "bigdecimal": {
                                Expression<Double> jsonValueAsDouble = criteriaBuilder.function(
                                        "jsonb_extract_path_double",
                                        Double.class,
                                        employeeRoot.get("metadata"),
                                        criteriaBuilder.literal(key)
                                );

                                Double numericValue = null;
                                if (filterValue instanceof Number) {
                                    numericValue = ((Number)filterValue).doubleValue();
                                } else if (filterValue instanceof String) {
                                    numericValue = Double.valueOf((String) filterValue);
                                }

                                if (numericValue != null) {
                                    predicates.add(criteriaBuilder.greaterThanOrEqualTo(jsonValueAsDouble, numericValue));
                                }
                                break;
                                //predicates.add(criteriaBuilder.greaterThanOrEqualTo(jsonValueAsDouble, Double.valueOf((String) filterValue)));
                            }
                        }
                        break;
                    }

                    case LESS_THAN: {
                        switch (dataType.toLowerCase()) {
                            case "integer":
                            case "long":
                            case "float":
                            case "double":
                            case "bigdecimal": {
                                Expression<Double> jsonValueAsDouble = criteriaBuilder.function(
                                        "jsonb_extract_path_double",
                                        Double.class,
                                        employeeRoot.get("metadata"),
                                        criteriaBuilder.literal(key)
                                );

                                Double numericValue = null;
                                if (filterValue instanceof Number) {
                                    numericValue = ((Number)filterValue).doubleValue();
                                } else if (filterValue instanceof String) {
                                    numericValue = Double.valueOf((String) filterValue);
                                }

                                if (numericValue != null) {
                                    predicates.add(criteriaBuilder.lessThan(jsonValueAsDouble, numericValue));
                                }
                                break;

                                //predicates.add(criteriaBuilder.lessThan(jsonValueAsDouble, Double.valueOf((String) filterValue)));
                            }
                        }
                        break;
                    }

                    case LESS_OR_EQUAL: {
                        switch (dataType.toLowerCase()) {
                            case "integer":
                            case "long":
                            case "float":
                            case "double":
                            case "bigdecimal": {
                                Expression<Double> jsonValueAsDouble = criteriaBuilder.function(
                                        "jsonb_extract_path_double",
                                        Double.class,
                                        employeeRoot.get("metadata"),
                                        criteriaBuilder.literal(key)
                                );
                                Double numericValue = null;
                                if (filterValue instanceof Number) {
                                    numericValue = ((Number)filterValue).doubleValue();
                                } else if (filterValue instanceof String) {
                                    numericValue = Double.valueOf((String) filterValue);
                                }

                                if (numericValue != null) {
                                    predicates.add(criteriaBuilder.lessThanOrEqualTo(jsonValueAsDouble, numericValue));
                                }
                                break;
                                //predicates.add(criteriaBuilder.lessThanOrEqualTo(jsonValueAsDouble, Double.valueOf((String) filterValue)));
                            }
                        }
                        break;
                    }


                }

                }

            }


        return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
    }
}


