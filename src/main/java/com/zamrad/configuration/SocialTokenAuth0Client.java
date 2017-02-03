package com.zamrad.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.zamrad.dto.AccessToken;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class SocialTokenAuth0Client {
    private final String CLIENT_ID = "sAvJYAESyNLOwjLo9LihTBn5ZY4vR58g";
    private final String CLIENT_SECRET = "GWVeyewjcFUj8zmA5e28ODC0DGW71RoZR9ixlT2EsAKzJH4XSZx7grg1ofqk45qZ";
    private final String BASE_URL = "https://zamrad.eu.auth0.com/api/v2/";
    private final String CONTENT_TYPE = "application/json";

    private final Logger LOGGER = LoggerFactory.getLogger(SocialTokenAuth0Client.class);

    public String getAccessToken(String userId) {
        String token;
        try {
            token = getAuth0ManagementApiToken().getAccessToken();
        } catch (UnirestException e) {
            throw new RuntimeException("Failed to retrieve auth0 management token: ", e);
        }

        Unirest.setDefaultHeader("Authorization", "Bearer " + token);
        Unirest.setDefaultHeader("content-type", CONTENT_TYPE);

        HttpResponse<String> response;
        try {
            response = Unirest.get("https://zamrad.eu.auth0.com/api/v2/users/facebook|" + userId).asString();
        } catch (UnirestException e) {
            throw new RuntimeException("Failed to retrieve auth0 user profile.");
        }

        LOGGER.info("Response from auth0 management api: {}", response.getBody());

        JSONObject jsonResponse = new JSONObject(response.getBody());
        final JSONArray identities = jsonResponse.getJSONArray("identities");
        final JSONObject identity = new JSONObject(identities.get(0).toString());

        return identity.get("access_token").toString();
    }

    private AccessToken getAuth0ManagementApiToken() throws UnirestException {
        final String body = "{ \"client_id\": \"" + CLIENT_ID + "\", \"client_secret\": \"" + CLIENT_SECRET + "\", \"audience\": \"" + BASE_URL + "\", \"grant_type\": \"client_credentials\" }";

        HttpResponse<String> response = Unirest.post("https://zamrad.eu.auth0.com/oauth/token")
                .header("content-type", CONTENT_TYPE)
                .body(body)
                .asString();

        LOGGER.info("Auth0 response for fb token was: {}", response.getBody());

        try {
            return new ObjectMapper().readValue(response.getBody(), AccessToken.class);
        } catch (IOException e) {
            throw new RuntimeException("Failed to deserialize response to access token.");
        }
    }
}
