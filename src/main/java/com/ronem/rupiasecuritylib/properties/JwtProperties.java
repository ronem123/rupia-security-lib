/**
 * Author: Ram Mandal
 * Created on @System: Apple M1 Pro
 * User:rammandal
 * Date:20/02/2026
 * Time:13:56
 */


package com.ronem.rupiasecuritylib.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
//  request spring to look for jwt in application.yml and map them into the filed of this class
//  jwt:
//  access-secret: ${RUPIA_JWT_SECRET_ACCESS}
//  refresh-secret: ${RUPIA_JWT_SECRET_REFRESH}
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {
    private String accessSecret;
    private String refreshSecret;
}