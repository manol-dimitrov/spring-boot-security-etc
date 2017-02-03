package com.zamrad.service.user;

import com.zamrad.configuration.SocialTokenAuth0Client;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SocialTokenService {
    private final SocialTokenAuth0Client socialTokenAuth0Client;

    @Autowired
    public SocialTokenService(SocialTokenAuth0Client socialTokenAuth0Client) {
        this.socialTokenAuth0Client = socialTokenAuth0Client;
    }

    public String getFacebookToken(String userId) {
        return socialTokenAuth0Client.getAccessToken(userId);
    }
}
