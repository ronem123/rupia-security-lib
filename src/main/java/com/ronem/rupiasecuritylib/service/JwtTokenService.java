/**
 * Author: Ram Mandal
 * Created on @System: Apple M1 Pro
 * User:rammandal
 * Date:20/02/2026
 * Time:13:17
 */


package com.ronem.rupiasecuritylib.service;

import com.ronem.rupiasecuritylib.properties.JwtProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;


@Service
@RequiredArgsConstructor
public class JwtTokenService {

    private final JwtProperties jwtProperties;
    private static final String CLAIM_TOKEN_TYPE = "token_type";
    private static final String CLAIM_ROLE = "role";
    private static final String CLAIM_MOBILE_NUMBER = "mobileNumber";
    private static final String CLAIM_EMAIL = "email";
    private static final String ACCESS = "ACCESS";
    private static final String REFRESH = "REFRESH";

    /**
     *
     * @return AccessToken Secret Key
     */
    public SecretKey getAccessTokenSecretKey() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtProperties.getAccessSecret());
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     *
     * @return RefreshToken Secret Key
     */
    public SecretKey getRefreshTokenSecretKey() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtProperties.getRefreshSecret());
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public boolean validateAccessToken(String token) {
        return validateToken(token, getAccessTokenSecretKey(), ACCESS);
    }

    public boolean validateRefreshToken(String token) {
        return validateToken(token, getRefreshTokenSecretKey(), REFRESH);
    }

    /**
     * Single method to validate the tokens like access-token and refresh-token
     *
     * @param token
     * @param secretKey
     * @param expectedType
     * @return
     */
    private boolean validateToken(String token, SecretKey secretKey, String expectedType) {
        try {
            Claims claims = getClaims(token, secretKey);

            String tokenType = claims.get(CLAIM_TOKEN_TYPE, String.class);
            return tokenType.equals(expectedType);
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    /**
     * Return claim from the given token
     *
     * @param token
     * @param secretKey
     * @return
     */
    private Claims getClaims(String token, SecretKey secretKey) {
        return Jwts.parser().verifyWith(secretKey).build()
                .parseSignedClaims(token)
                .getPayload();

    }


    /**
     * Return subject from the provided token. generally the userId with type Long
     *
     * @param token
     * @param secretKey
     * @return
     */
    public Long getSubject(String token, SecretKey secretKey) {
        return Long.parseLong(getClaims(token, secretKey).getSubject());
    }

    /**
     * Return Current user role from the token provided.
     * @param token
     * @param secretKey
     * @return
     */
    public String getRole(String token, SecretKey secretKey) {
        return getClaims(token, secretKey).get(CLAIM_ROLE, String.class);
    }
}