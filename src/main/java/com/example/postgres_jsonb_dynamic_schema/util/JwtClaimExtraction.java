package com.example.postgres_jsonb_dynamic_schema.util;

import com.example.postgres_jsonb_dynamic_schema.dto.OrganizationDto;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import java.util.List;
import java.util.Map;

public class JwtClaimExtraction {
    public static String getAttributeValueByName() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth instanceof JwtAuthenticationToken jwtAuth) {
            return (String) jwtAuth.getToken().getClaims().get("dummyName");
        }
        return null;
    }

    public static OrganizationDto getOrganizationDetails() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth instanceof JwtAuthenticationToken jwtAuth) {
            Map<String, Object> claims = jwtAuth.getToken().getClaims();

            if (claims.containsKey("organization")) {
                Map<String, Object> organizationClaim = (Map<String, Object>) claims.get("organization");

                // Assuming there's only one entry in the organization map
                if (!organizationClaim.isEmpty()) {
                    Map.Entry<String, Object> entry = organizationClaim.entrySet().iterator().next();
                    OrganizationDto organizationDTO = new OrganizationDto();
                    organizationDTO.setAlias(entry.getKey());

                    Map<String, Object> organizationDetails = (Map<String, Object>) entry.getValue();

                    if (organizationDetails.containsKey("id")) {

                        organizationDTO.setId((String) organizationDetails.get("id"));
                        return organizationDTO;
                    }
                }
            }
        }
        return null; // Or throw an exception if organization ID is expected to always be present

    }

    public static String getUsernameFromJwt() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth instanceof JwtAuthenticationToken jwtAuth) {
            Map<String, Object> claims = jwtAuth.getToken().getClaims();
            if (claims.containsKey("preferred_username")) {
                return (String) claims.get("preferred_username");
            }
        }
        return null; // or throw an exception if username is mandatory
    }

    public static String getKeycloakIdFromJwt() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth instanceof JwtAuthenticationToken jwtAuth) {
            Map<String, Object> claims = jwtAuth.getToken().getClaims();
            if (claims.containsKey("sub")) {
                return (String) claims.get("sub");
            }
        }
        return null;
    }

    public static String extractUserRole() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth instanceof JwtAuthenticationToken jwtAuth) {
            Map<String, Object> claims = jwtAuth.getToken().getClaims();

            Object resourceAccessObj = claims.get("resource_access");
            if (resourceAccessObj instanceof Map<?, ?> resourceAccess) {
                Object clientAccessObj = resourceAccess.get("crm-oauth2-pkce-client");
                if (clientAccessObj instanceof Map<?, ?> clientAccess) {
                    Object rolesObj = clientAccess.get("roles");
                    if (rolesObj instanceof List<?> roles) {
                        if (roles.contains("manager")) return "manager";
                        if (roles.contains("user")) return "user";
                    }
                }
            }
        }
        return "user";
    }

}

