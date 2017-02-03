package com.zamrad.service.user;

import com.auth0.spring.security.api.Auth0JWTToken;
import com.auth0.spring.security.api.Auth0UserDetails;
import com.zamrad.configuration.Auth0Client;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class Auth0TokenService {

    private static final Logger LOGGER = LoggerFactory.getLogger(Auth0TokenService.class);

    private final Auth0Client auth0Client;

    @Autowired
    public Auth0TokenService(Auth0Client auth0Client) {
        this.auth0Client = auth0Client;
    }

    public String getSocialUserId() {
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        final Auth0UserDetails principal = (Auth0UserDetails) authentication.getPrincipal();

        LOGGER.info("Current user accessed ARTIST secured resource: " + principal.getUsername());

        return auth0Client.getUserId((Auth0JWTToken) authentication);
    }

    public String getUserRoles() {
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        final Auth0UserDetails principal = (Auth0UserDetails) authentication.getPrincipal();

        LOGGER.info("Current user accessed ARTIST secured resource: " + principal.getUsername());

        return auth0Client.getUserRole((Auth0JWTToken) authentication);
    }

    public String getUserEmail() {
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return auth0Client.getUserEmail((Auth0JWTToken) authentication);
    }
}
