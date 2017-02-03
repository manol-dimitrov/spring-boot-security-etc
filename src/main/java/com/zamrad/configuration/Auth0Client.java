package com.zamrad.configuration;

import com.auth0.Auth0;
import com.auth0.authentication.AuthenticationAPIClient;
import com.auth0.authentication.result.UserProfile;
import com.auth0.request.Request;
import com.auth0.spring.security.api.Auth0JWTToken;
import org.springframework.stereotype.Component;

/**
 * Auth0 client component to interact with the authorization and authentication Auth0 API.
 *
 * @return Auth0Client instance
 */
@Component
public class Auth0Client {

    private final String clientid;
    private final String domain;

    private final Auth0 auth0;
    private final AuthenticationAPIClient client;

    public Auth0Client(String clientid, String domain) {
        this.clientid = clientid;
        this.domain = domain;
        this.auth0 = new Auth0(clientid, domain);
        this.client = this.auth0.newAuthenticationAPIClient();
    }

    public String getUserEmail(Auth0JWTToken token) {
        final Request<UserProfile> request = client.tokenInfo(token.getJwt());
        final UserProfile profile = request.execute();
        return profile.getEmail();
    }

    public String getUserRole(Auth0JWTToken token) {
        final Request<UserProfile> request = client.tokenInfo(token.getJwt());
        final UserProfile profile = request.execute();
        return profile.getAppMetadata().get("roles").toString();
    }

    public String getUserId(Auth0JWTToken token) {
        final Request<UserProfile> request = client.tokenInfo(token.getJwt());
        final UserProfile profile = request.execute();
        return profile.getId();
    }

    public String getClientid() {
        return clientid;
    }

    public String getDomain() {
        return domain;
    }
}

