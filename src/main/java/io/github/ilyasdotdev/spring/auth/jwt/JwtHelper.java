package io.github.ilyasdotdev.spring.auth.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.*;
import lombok.Data;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Date;
import java.util.Map;

/**
 * A helper class for working with JSON Web Tokens (JWT).
 *
 * @author <b>Muhammad Ilyas</b> (m.ilyas@live.com)
 */
@Data
@Component
public class JwtHelper {

    private final ObjectMapper objectMapper;
    private final JWTConfig jwtConfig;

    /**
     * Constructs a JwtHelper object with the given ObjectMapper and JWTConfig.
     *
     * @param objectMapper the ObjectMapper used for JSON processing
     * @param jwtConfig     the JWTConfig object containing JWT settings
     */
    public JwtHelper(ObjectMapper objectMapper, JWTConfig jwtConfig) {
        this.objectMapper = objectMapper;
        this.jwtConfig = jwtConfig;
    }

    /**
     * Converts a JWT token string to the specified object type.
     *
     * @param token the JWT token to convert
     * @param type  the class type to convert the token to
     * @param <T>   the type parameter for the object to convert to
     * @return the converted object of the specified type
     * @throws ExpiredJwtException      if the specified JWT token has expired
     * @throws UnsupportedJwtException  if the specified JWT token is not supported
     * @throws MalformedJwtException    if the specified JWT token is not valid
     * @throws IllegalArgumentException if the specified JWT token is null, empty or whitespace
     */
    public <T> T deserializeJwtToken(String token, Class<T> type) {
        Claims claims = loadAllClaimsFromToken(token, jwtConfig.getSecret());
        return objectMapper.convertValue(claims, type);
    }

    /**
     * Converts the specified token to a JWT string.
     *
     * @param token the token to convert
     * @param <T>   the type parameter for the token
     * @return the generated JWT string
     */
    @SuppressWarnings("unchecked")
    public <T> String serializeJwtToken(T token) {
        return Jwts.builder()
                .setClaims(objectMapper.convertValue(token, Map.class))
                .setExpiration(Date.from(Instant.now().plus(jwtConfig.getExpiry(), jwtConfig.getExpiryUnit())))
                .signWith(SignatureAlgorithm.HS512, jwtConfig.getSecret())
                .compact();
    }

    /**
     * Loads all claims from a JWT token using the specified secret key.
     *
     * @param token  the JWT token to load claims from
     * @param secret the secret key to verify the token signature
     * @return the claims extracted from the token
     * @throws ExpiredJwtException      if the token has expired
     * @throws UnsupportedJwtException  if the token is not supported
     * @throws MalformedJwtException    if the token is malformed or invalid
     * @throws IllegalArgumentException if the token is null, empty, or contains only whitespace
     * @throws SignatureException       if the token signature is invalid
     */
    private static Claims loadAllClaimsFromToken(String token, String secret)
            throws
            ExpiredJwtException,
            UnsupportedJwtException,
            MalformedJwtException,
            IllegalArgumentException,
            SignatureException {

        JwtParser jwtParser = Jwts.parser().setSigningKey(secret);
        return jwtParser.parseClaimsJws(token).getBody();
    }
}
