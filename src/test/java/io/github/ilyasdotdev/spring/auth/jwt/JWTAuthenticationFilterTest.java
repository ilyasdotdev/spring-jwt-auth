package io.github.ilyasdotdev.spring.auth.jwt;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Data;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ContextConfiguration;

import java.util.ArrayList;
import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

@SpringBootTest
@EnableConfigurationProperties
@ContextConfiguration(classes = {JwtHelper.class, JWTConfig.class, ObjectMapper.class})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class JWTAuthenticationFilterTest {

    @Autowired
    ObjectMapper mapper;

    @Autowired
    JwtHelper jwtHelper;

    @Mock
    FilterChain filterChain;

    private JwtToken token;

    @BeforeAll
    void setup() {
        token = createToken();
        //ignore unknown properties
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    @Test
    void doFilterInternalWithValidJWTToken() throws Exception {

        JWTAuthenticationFilter jwtAuthenticationFilter = new JWTAuthenticationFilter(
                jwtHelper,
                token.getClass()
        );

        String validJwt = "Bearer " + jwtHelper.serializeJwtToken(token);

        // Setting expectations
        MockHttpServletRequest req = new MockHttpServletRequest();

        req.addHeader("Authorization", validJwt);
        HttpServletResponse resp = new MockHttpServletResponse();

        jwtAuthenticationFilter.doFilterInternal(req, resp, filterChain);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        assertThat(authentication).isNotNull();
        JwtToken jwtToken = (JwtToken) authentication.getPrincipal();

        assertThat(jwtToken.getId()).isEqualTo(token.id);
        assertThat(jwtToken.getUsername()).isEqualTo(token.username);

        assertThat(jwtToken.getRoles()).contains("USER");

        verify(filterChain).doFilter(req, resp);
    }

    @Data
    private static class JwtToken implements Token {

        private String id;
        private String username;

        @Override
        public Collection<String> getRoles() {
            ArrayList<String> roles = new ArrayList<>();
            roles.add("USER");
            return roles;
        }
    }

    private JwtToken createToken() {
        JwtToken token = new JwtToken();
        token.setId("101");
        token.setUsername("Test User");
        return token;
    }
}