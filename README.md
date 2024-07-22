<!-- TOC -->
* [Spring Jwt Auth](#spring-jwt-auth)
* [Documentation](#documentation)
  * [Generating Jwt Token](#generating-jwt-token)
  * [Getting authenticated user details](#getting-authenticated-user-details)
  * [Example](#example)
<!-- TOC -->

# Spring Jwt Auth

An opensource library to quickly implement JWT authentication in a spring boot application.


# Documentation

Add the dependency to your project:

```xml
<dependency>
    <groupId>dev.ilyas</groupId>
    <artifactId>spring-jwt-auth</artifactId>
    <version>1.1.0</version>
</dependency>
```

Add the following properties to your `application.properties` file:

```properties
ilyasdotdev:
  spring:
    auth:
      jwt:
        secret: 792F413F4428472B4B6250655368566D597133743677397A244326452948404D635166546A576E5A7234753778214125442A472D4A614E645267556B58703273
        expiry-unit: minutes
        expiry: 30
```

Create a class that represents jwt token payload:

```java
@Data
class JwtToken implements Token {

    private String username;
    private String email;
    private List<String> roles;

    @Override
    public Collection<String> getRoles() {
        return roles;
    }
}
```

Register `JWTAuthenticationFilter` in spring security filter chain:

```java
@Bean
@Order(2)
public SecurityFilterChain securityFilterChain(HttpSecurity http, JwtHelper jwtHelper) throws Exception {
    JWTAuthenticationFilter jwtAuthenticationFilter = new JWTAuthenticationFilter(jwtHelper, JwtToken.class);
    return http
            .securityMatcher("/api/**")
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
            .csrf(AbstractHttpConfigurer::disable)
            .cors(AbstractHttpConfigurer::disable)
            .formLogin(AbstractHttpConfigurer::disable)
            .httpBasic(AbstractHttpConfigurer::disable)
            .build();
}
```

**Note:**
- Change secret with your secret key string
- Expiry unit could be any java ChronoUnit, see [ChronoUnit](https://docs.oracle.com/javase/8/docs/api/java/time/temporal/ChronoUnit.html)

**That's all, We have successfully secured our spring boot application by jwt authentication**

## Generating Jwt Token
To create an endpoint which returns jwt token, We can create simple rest controller.

```java
@RestController
@RequiredArgsConstructor
class AuthenticationController {

    private final JwtHelper jwtHelper;

    @GetMapping("/api/auth")
    public String auth() {

        JwtToken jwtToken = new JwtToken();
        jwtToken.setUsername("test user");
        jwtToken.setEmail("email@domain.com");
        jwtToken.setRoles(List.of("USER", "ADMIN"));

        return jwtHelper.serializeJwtToken(jwtToken);
    }
}
```

## Getting authenticated user details

Once user is authenticated, we can get user details from jwt token.

```java
JwtToken token = (JwtToken) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
```

## Example

[See running code example using this library](https://github.com/ilyasdotdev/spring-jwt-auth-example)
