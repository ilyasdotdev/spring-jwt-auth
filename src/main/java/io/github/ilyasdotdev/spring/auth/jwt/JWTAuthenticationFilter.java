package io.github.ilyasdotdev.spring.auth.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.util.Optional;

/**
 * This class is responsible for filtering and processing JWT authentication in each request.
 */
@Slf4j
public class JWTAuthenticationFilter extends OncePerRequestFilter {

    private final JwtHelper jwtHelper;
    private final Class<? extends Token> token;

    /**
     * Initializes a new instance of the JWTAuthenticationFilter class.
     *
     * @param jwtHelper The JwtHelper instance used for working with JSON Web Tokens.
     * @param token     The Token instance representing the authentication token.
     */
    public JWTAuthenticationFilter(JwtHelper jwtHelper, Class<? extends Token> token) {
        this.jwtHelper = jwtHelper;
        this.token = token;
    }

    private static final String AUTH_HEADER_VALUE_PREFIX = "Bearer ";

    @Override
    @SneakyThrows
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) {
        log.debug("filtering jwt token");
        //get Authorization header value
        String bearerToken = Optional.ofNullable(request.getHeader(HttpHeaders.AUTHORIZATION))
                .filter(authHeaderValue -> authHeaderValue.startsWith(AUTH_HEADER_VALUE_PREFIX))
                .map(authHeaderValue -> authHeaderValue.substring(AUTH_HEADER_VALUE_PREFIX.length()))
                .orElse(null);

        // if bearerToken is null do nothing
        if (bearerToken == null) {
            log.debug("No token in request header");
            filterChain.doFilter(request, response);
            return;
        }

        //convert token to Token object
        Token parsedJwtToken = jwtHelper.deserializeJwtToken(bearerToken, token);

        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                new UsernamePasswordAuthenticationToken(parsedJwtToken,
                        null,
                        parsedJwtToken.getRoles().stream().map(role -> new SimpleGrantedAuthority("ROLE_" + role)).toList()
                );

        SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);

        filterChain.doFilter(request, response);
    }
}
