/**
 * Author: Ram Mandal
 * Created on @System: Apple M1 Pro
 * User:rammandal
 * Date:11/03/2026
 * Time:16:11
 */


package com.ronem.rupiasecuritylib.util;

import com.ronem.rupiasecuritylib.enums.UserRole;
import org.springframework.stereotype.Component;

@Component
public class UserRoleUtil {
    public UserRole getMappedUserRole(String role) {
//        if (role.equals(UserRole.ADMIN.name()))
//            return UserRole.ADMIN;
//        else if (role.equals(UserRole.SUPER_ADMIN.name()))
//            return UserRole.SUPER_ADMIN;
//        else return UserRole.ADMIN;

        return UserRole.valueOf(role);
    }
}