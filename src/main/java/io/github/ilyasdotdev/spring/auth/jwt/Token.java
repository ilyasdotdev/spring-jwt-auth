package io.github.ilyasdotdev.spring.auth.jwt;

import java.util.Collection;

/**
 * Represents a token that can be used for authentication.
 * @author Muhammad Ilyas (m.ilyas@live.com)
 */
public interface Token {

    /**
     * Retrieves the roles associated with the token.
     *
     * @return A collection of strings representing the roles assigned to the token.
     */
    Collection<String> getRoles();
}
