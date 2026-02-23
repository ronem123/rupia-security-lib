/**
 * Author: Ram Mandal
 * Created on @System: Apple M1 Pro
 * User:rammandal
 * Date:20/02/2026
 * Time:13:17
 */


package com.ronem.rupiasecuritylib.service;

import com.ronem.rupiasecuritylib.enums.UserRole;
import com.ronem.rupiasecuritylib.properties.JwtProperties;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;


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


    /**
     * Generate Access-Token for customer (user based on Mobile number)
     *
     * @param userId
     * @param mobileNumber
     * @return
     */
    public String createAccessTokenForCustomer(String userId, String mobileNumber) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtProperties.getAccessTokenExpiryTime());
        return Jwts.builder()
                .subject(userId)
                .claim(CLAIM_MOBILE_NUMBER, mobileNumber)
                .claim(CLAIM_ROLE, UserRole.CUSTOMER.name())
                .claim(CLAIM_TOKEN_TYPE, ACCESS)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(getAccessTokenSecretKey())
                .compact();
    }

    /**
     * Generate Access-Token for Admins (user based on email)
     *
     * @param userId
     * @param email
     * @param role
     * @return
     */
    public String createAccessTokenForAdmin(String userId, String email, String role) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtProperties.getAccessTokenExpiryTime());
        return Jwts.builder()
                .subject(userId)
                .claim(CLAIM_EMAIL, email)
                .claim(CLAIM_ROLE, role)
                .claim(CLAIM_TOKEN_TYPE, ACCESS)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(getAccessTokenSecretKey())
                .compact();
    }

    public
}