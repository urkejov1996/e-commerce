package org.urkejov.tools;

import org.springframework.security.oauth2.jwt.Jwt;

import java.util.ArrayList;
import java.util.List;

public class RoleTools {
    public static Boolean hasAccess(Jwt jwt, ArrayList<String> roles) {
        try {
            List<String> userRoles = jwt.getClaim("roles");
            return userRoles.stream().anyMatch(roles::contains);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    public static Boolean hasAccess(Jwt jwt, String role) {
        try {
            List<String> userRoles = jwt.getClaim("roles");
            return userRoles.stream().anyMatch(role::equals);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
