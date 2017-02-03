package com.zamrad.resources;

import com.zamrad.dto.AccessToken;
import com.zamrad.service.user.SocialTokenService;
import com.zamrad.service.user.Auth0TokenService;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import java.security.Principal;
import java.util.Arrays;
import java.util.Objects;

@RestController
@RequestMapping("/tokens/v1")
@CrossOrigin
@Api(value = "/tokens", description = "Retrieve social access tokens.")
public class SocialTokenResource {
    @Autowired
    private SocialTokenService socialTokenService;

    @Autowired
    private Auth0TokenService auth0TokenService;

    @Autowired
    private Environment environment;

    @ApiOperation(value = "Get my facebook access token.", response = String.class, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Access token retrieved successfully."),
            @ApiResponse(code = 403, message = "The operation cannot be fulfilled with the provided credentials.")
    })
    @ApiImplicitParam(name = "Authorization", value = "Bearer token", dataType = "string", paramType = "header")
    @RequestMapping(value = "/facebook", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AccessToken> getFacebookToken(@ApiIgnore final Principal principal) {
        Objects.requireNonNull(principal, "Principal cannot be null.");

        Long facebookId = getUserId();
        final String facebookToken = socialTokenService.getFacebookToken(String.valueOf(facebookId));

        AccessToken accessToken = new AccessToken();
        accessToken.setAccessToken(facebookToken);

        return new ResponseEntity<>(accessToken, HttpStatus.OK);
    }

    private Long getUserId() {
        if (Arrays.stream(environment.getActiveProfiles()).anyMatch(profile -> Objects.equals(profile, "dev"))) {
            return 1392995950728274L;
        }
        return Long.valueOf(auth0TokenService.getSocialUserId().substring(9));
    }
}
