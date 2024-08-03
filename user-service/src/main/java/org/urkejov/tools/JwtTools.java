package org.urkejov.tools;

import org.springframework.security.oauth2.jwt.Jwt;

public class JwtTools {
    public static String getEmailFromOAuthToken(Jwt jwt) throws Exception {
        String email = jwt.getClaim("email").toString();
        if (email == null || email.isEmpty()) {
            throw new Exception("Email not present in claims");
        }
        return email;
    }
}
