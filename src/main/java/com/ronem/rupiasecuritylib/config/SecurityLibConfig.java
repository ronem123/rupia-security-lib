/**
 * Author: Ram Mandal
 * Created on @System: Apple M1 Pro
 * User:rammandal
 * Date:18/03/2026
 * Time:10:54
 */


package com.ronem.rupiasecuritylib.config;

import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = "com.ronem.rupiasecuritylib")
@ConfigurationPropertiesScan(basePackages = "com.ronem.rupiasecuritylib.properties")
public class SecurityLibConfig {
    //Enable al components under security-lib
}