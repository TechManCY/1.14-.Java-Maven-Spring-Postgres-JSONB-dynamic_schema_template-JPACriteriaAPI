package com.example.postgres_jsonb_dynamic_schema.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "keycloak")
public class KeycloakConfig {
    private String serverUrl;
    private String realm;
    private String clientId;
    private String username;
    private String password;

}
