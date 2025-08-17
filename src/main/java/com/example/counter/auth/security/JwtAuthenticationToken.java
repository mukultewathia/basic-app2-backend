package com.example.counter.auth.security;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import java.util.Collection;

public class JwtAuthenticationToken extends AbstractAuthenticationToken {
    private final Object principal;
    private final UserContext userContext;

    public JwtAuthenticationToken(Object principal, UserContext userContext, 
                                Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.principal = principal;
        this.userContext = userContext;
        setAuthenticated(true);
    }

    @Override
    public Object getCredentials() {
        return null; // JWT tokens don't have credentials
    }

    @Override
    public Object getPrincipal() {
        return principal;
    }

    public UserContext getUserContext() {
        return userContext;
    }

    public Long getUserId() {
        return userContext != null ? userContext.getUserId() : null;
    }

    public String getUsername() {
        return userContext != null ? userContext.getUsername() : null;
    }
}
