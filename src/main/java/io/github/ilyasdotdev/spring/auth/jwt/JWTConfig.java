package io.github.ilyasdotdev.spring.auth.jwt;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

/**
 * Configuration class for JSON Web Token (JWT) settings.
 */
@Component
@ConfigurationProperties(prefix = "ilyasdotdev.spring.auth.jwt")
@Data
public class JWTConfig {

    private String secret;
    private ChronoUnit expiryUnit;
    private Integer expiry;

    /**
     * Configuration class for JSON Web Token (JWT) settings.
     */
    public JWTConfig() {

    }

    /**
     * Retrieves the expiration time of a JWT token in seconds.
     *
     * @return The expiration time of a JWT token in seconds.
     */
    public Long getExpiryInSeconds() {
        return Duration.of(expiry, expiryUnit).getSeconds();
    }
}
