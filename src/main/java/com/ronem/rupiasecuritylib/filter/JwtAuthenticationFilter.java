/**
 * Author: Ram Mandal
 * Created on @System: Apple M1 Pro
 * User:rammandal
 * Date:20/02/2026
 * Time:13:15
 */


package com.ronem.rupiasecuritylib.filter;

import com.ronem.rupiasecuritylib.enums.UserRole;
import com.ronem.rupiasecuritylib.model.UserPrincipal;
import com.ronem.rupiasecuritylib.properties.JwtProperties;
import com.ronem.rupiasecuritylib.service.JwtTokenService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenService jwtTokenService;
    private final JwtProperties jwtProperties;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        try {
            String token = extractToken(request);
            if (token != null && jwtTokenService.validateAccessToken(token)) {
                Long userId = jwtTokenService.getSubject(token);
                String role = jwtTokenService.getRole(token);

                UserRole userRole = getMappedUserRole(role);
                UserPrincipal userPrincipal;
                /**
                 * If user is CUSTOMER don't set email
                 * only set email for the admins
                 */
                if (userRole.equals(UserRole.CUSTOMER)) {
                    String mobileNumber = jwtTokenService.getClaimMobileNumber(token);
                    userPrincipal = UserPrincipal.builder()
                            .userId(userId)
                            .email(null)
                            .mobileNumber(mobileNumber)
                            .role(getMappedUserRole(role))
                            .build();
                } else {
                    String email = jwtTokenService.getClaimEmail(token);
                    userPrincipal = UserPrincipal.builder()
                            .userId(userId)
                            .email(email)
                            .mobileNumber(null)
                            .role(getMappedUserRole(role))
                            .build();
                }

                /**
                 * create authentication object
                 */
                UsernamePasswordAuthenticationToken authenticationToken =
                        new UsernamePasswordAuthenticationToken(userPrincipal, null, userPrincipal.getAuthorities());

                /**
                 * Set authentication to Security Context Holder
                 */
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);

            }

        } catch (Exception e) {
            log.info("Could not set userauthentication {}", e.getMessage());
        }

        /**
         * Proceed with the request and response
         */
        filterChain.doFilter(request, response);
    }

    private UserRole getMappedUserRole(String role) {
        if (role.equals(UserRole.ADMIN.name()))
            return UserRole.ADMIN;
        else if (role.equals(UserRole.SUPER_ADMIN.name()))
            return UserRole.SUPER_ADMIN;
        else return UserRole.ADMIN;
    }

    private String extractToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}