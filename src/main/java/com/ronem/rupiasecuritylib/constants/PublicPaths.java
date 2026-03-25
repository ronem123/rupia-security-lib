/**
 * Author: Ram Mandal
 * Created on @System: Apple M1 Pro
 * User:rammandal
 * Date:19/03/2026
 * Time:10:09
 */


package com.ronem.rupiasecuritylib.constants;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PublicPaths {
    // rupia-auth-service public paths
    public static final List<String> AUTH_PUBLIC_PATHS = Arrays.asList(
            "/auth/admin/login"
    );

    // rupia-customer public paths
    public static final List<String> CUSTOMER_PUBLIC_PATHS = Arrays.asList(
            "/customer/register"
    );

    // Common public paths
    public static final List<String> COMMON_PUBLIC_PATHS = Arrays.asList(
            "/actuator/health",
            "/actuator/info"
    );

    // all public paths
    public static final List<String> ALL_PUBLIC_PATHS = combineAllPublicPaths();


    private static List<String> combineAllPublicPaths() {
        List<String> allPaths = new ArrayList<>();
        allPaths.addAll(AUTH_PUBLIC_PATHS);
        allPaths.addAll(CUSTOMER_PUBLIC_PATHS);
        allPaths.addAll(COMMON_PUBLIC_PATHS);

        return allPaths;
    }

    public static boolean isPublicPath(String path) {
        return ALL_PUBLIC_PATHS.stream().anyMatch(path::startsWith);
    }
}