package com.zamrad.configuration;

import com.auth0.spring.security.api.Auth0AuthenticationFilter;
import com.auth0.spring.security.api.Auth0SecurityConfig;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.context.SecurityContextPersistenceFilter;

@Configuration
@EnableWebSecurity(debug = true)
@EnableGlobalMethodSecurity(prePostEnabled = true)
@Order(SecurityProperties.ACCESS_OVERRIDE_ORDER)
public class SecurityConfig extends Auth0SecurityConfig {

    private static final String PROFILES_PATH = "/profiles/v1/**";
    private static final String EVENTS_PATH = "/events/v1/**";
    private static final String PHOTOS_PATH = "/photos/v1/**";
    private static final String TOKENS_PATH = "/tokens/v1/**";
    private static final String REVIEWS_PATH = "/reviews/v1/**";

    @Bean
    public Auth0Client auth0Client() {
        return new Auth0Client(clientId, issuer);
    }

    @Bean
    @Primary
    public CORSFilter corsFilter() {
        return new CORSFilter();
    }

    @Override
    protected void configure(final HttpSecurity http) throws Exception {
        http.csrf().disable();
        http.addFilterAfter(auth0AuthenticationFilter(auth0AuthenticationEntryPoint()), SecurityContextPersistenceFilter.class)
                .addFilterBefore(corsFilter(), Auth0AuthenticationFilter.class);
        authorizeRequests(http);
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
    }

    @Override
    protected void authorizeRequests(final HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers(PROFILES_PATH).authenticated()
                .antMatchers(PHOTOS_PATH).authenticated()
                .antMatchers(REVIEWS_PATH).authenticated()
                .antMatchers(TOKENS_PATH).authenticated()
                .antMatchers(EVENTS_PATH).authenticated();
    }
}