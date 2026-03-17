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
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.crypto.SecretKey;


@Service
@RequiredArgsConstructor
public class JwtUtil {

    private final JwtProperties jwtProperties;
    public static final String CLAIM_TOKEN_TYPE = "token_type";
    public static final String CLAIM_ROLE = "role";
    public static final String CLAIM_MOBILE_NUMBER = "mobileNumber";
    public static final String CLAIM_EMAIL = "email";
    public static final String ACCESS = "ACCESS";
    public static final String REFRESH = "REFRESH";

    /**
     *
     * @return AccessToken Secret Key
     */
    public SecretKey getAccessTokenSecretKey() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtProperties.getAccessSecret());
        return Keys.hmacShaKeyFor(keyBytes);
    }


    public boolean validateAccessToken(String token) {
        try {
            Claims claims = getClaims(token);

            String tokenType = claims.get(CLAIM_TOKEN_TYPE, String.class);
            return tokenType.equals(ACCESS);
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }


    /**
     * Extract token
     */
    public String extractToken(ServerHttpRequest request) {
        String bearerToken = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    /**
     * Return claim from the given token
     *
     * @param token
     * @return
     */
    private Claims getClaims(String token) {
        return Jwts.parser().verifyWith(getAccessTokenSecretKey()).build()
                .parseSignedClaims(token)
                .getPayload();

    }


    /**
     * Return subject from the provided token. generally the userId with type Long
     *
     * @param token
     * @return
     */
    public Long getSubject(String token) {
        return Long.parseLong(getClaims(token).getSubject());
    }

    /**
     * Return Current user role from the token provided.
     *
     * @param token
     * @return
     */
    public String getRole(String token) {
        return getClaims(token).get(CLAIM_ROLE, String.class);
    }

    /**
     * Return Current user email from the token provided.
     *
     * @param token
     * @return
     */
    public String getClaimEmail(String token) {
        return getClaims(token).get(CLAIM_EMAIL, String.class);
    }

    /**
     * Return Current user mobile number from the token provided.
     *
     * @param token
     * @return
     */
    public String getClaimMobileNumber(String token) {
        return getClaims(token).get(CLAIM_MOBILE_NUMBER, String.class);
    }
}