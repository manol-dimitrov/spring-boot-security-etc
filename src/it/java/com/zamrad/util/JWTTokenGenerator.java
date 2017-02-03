package com.zamrad.util;

import com.auth0.jwt.JWTSigner;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.JWTVerifyException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.apache.log4j.Logger;
import org.junit.Test;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Map;

import static com.auth0.jwt.Algorithm.HS256;
import static org.assertj.core.api.Assertions.assertThat;

public class JWTTokenGenerator {

    private final static Logger LOGGER = Logger.getLogger(JWTTokenGenerator.class);
    private final static String SECRET = "WvX2FwWhnI1FmWRawQkPSXPZwHO_gzDfcpzZjrLGcfQHz62n2WruF_BsSXG7sF1i";
    private static final String AUDIENCE = "CK33UrY612ano10onCbbxJm0fPxGIA76";
    private static final String ISSUER = "https://localhost:9000/";

    public static String generateToken() throws UnsupportedEncodingException {
        final JWTSigner signer = new JWTSigner(SECRET.getBytes());
        Claims claims = createClaims();
        final String token = signer.sign(claims, new JWTSigner.Options().setAlgorithm(HS256));

        LOGGER.info("Token is " + token);
        return token;
    }

    private static Claims createClaims() {
        final Claims claims = Jwts.claims();

        claims.put("name", "Manol Dimitrov");
        claims.put("email", "manoldimitrov@yahoo.com");
        claims.put("email_verified", true);
        claims.put("roles", new String[]{"ROLE_ARTIST"});
        claims.setIssuer(ISSUER);
        claims.setSubject("facebook|1392995950728274");
        claims.setAudience(AUDIENCE);
        claims.setIssuedAt(Date.from(Instant.now()));
        claims.setExpiration(Date.from(Instant.now().plus(6L, ChronoUnit.HOURS)));

        return claims;
    }

    @Test
    public void verifyToken() throws IOException, SignatureException, NoSuchAlgorithmException, JWTVerifyException, InvalidKeyException {
        final String token = generateToken();
        JWTVerifier jwtVerifier = new JWTVerifier(SECRET, AUDIENCE, ISSUER);
        final Map<String, Object> decoded = jwtVerifier.verify(token);
        assertThat(decoded).isNotNull();
    }


}
