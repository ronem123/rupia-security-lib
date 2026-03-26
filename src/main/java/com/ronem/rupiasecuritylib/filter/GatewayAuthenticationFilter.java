/**
 * Author: Ram Mandal
 * Created on @System: Apple M1 Pro
 * User:rammandal
 * Date:11/03/2026
 * Time:15:54
 */


package com.ronem.rupiasecuritylib.filter;

import com.ronem.rupiasecuritylib.constants.HeaderUtil;
import com.ronem.rupiasecuritylib.constants.PublicPaths;
import com.ronem.rupiasecuritylib.enums.UserRole;
import com.ronem.rupiasecuritylib.model.UserPrincipal;
import com.ronem.rupiasecuritylib.properties.JwtProperties;
import com.ronem.rupiasecuritylib.util.UserRoleUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class GatewayAuthenticationFilter extends OncePerRequestFilter {

    private final JwtProperties jwtProperties;
    private final UserRoleUtil userRoleUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String path = request.getRequestURI();

        //skip actuator
        if (path.startsWith("/actuator") || PublicPaths.isPublicPath(path)) {
            filterChain.doFilter(request, response);
            log.warn("The provided path is public, {}", path);
            return;
        }


        // 1. validate internal secret
        String internalSecret = request.getHeader(HeaderUtil.xInternalSecret);

        if (internalSecret == null || !internalSecret.equals(jwtProperties.getAccessSecret())) {
            log.warn("Blocked direct access: Invalid internal secret from {}", request.getRemoteAddr());
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\":\"Forbidden\"}");
            return;
        }

        log.info("Secret from Gateway {}", internalSecret);
        log.info("Secret from microservice {}", jwtProperties.getAccessSecret());

        // 2. Read user info from headers (set by Api Gateway)
        String hUserId = request.getHeader(HeaderUtil.xUserId);
        String hUserEmail = request.getHeader(HeaderUtil.xUserEmail);
        String hUserMobile = request.getHeader(HeaderUtil.xUserMobile);
        String hRole = request.getHeader(HeaderUtil.xUserRole);

        log.info("Userid :{}", hUserId);
        log.info("UserEmail :{}", hUserEmail);
        log.info("UserRole :{}", hRole);

        // 3. set the SecurityContext if user is present
        if (hUserId != null && hRole != null) {
            Long userId = Long.parseLong(hUserId);

            //determine userType
            UserRole userRole = userRoleUtil.getMappedUserRole(hRole);

            UserPrincipal userPrincipal;

            /**
             * If user is CUSTOMER don't set email
             * only set email for the admins
             */
            if (userRole.equals(UserRole.CUSTOMER)) {
                userPrincipal = UserPrincipal.builder()
                        .userId(userId)
                        .email(null)
                        .mobileNumber(hUserMobile)
                        .role(userRole)
                        .build();
            } else {
                userPrincipal = UserPrincipal.builder()
                        .userId(userId)
                        .email(hUserEmail)
                        .mobileNumber(null)
                        .role(userRole)
                        .build();
            }

            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(userPrincipal, null, userPrincipal.getAuthorities());
            authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);

        }
        // 4. continue filter chain
        filterChain.doFilter(request, response);
    }
}