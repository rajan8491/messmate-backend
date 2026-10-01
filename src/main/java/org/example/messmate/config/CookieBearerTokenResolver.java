package org.example.messmate.config;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.example.messmate.properties.JwtCookieProperties;
import org.jspecify.annotations.Nullable;
import org.springframework.security.oauth2.server.resource.web.BearerTokenResolver;
import org.springframework.stereotype.Component;

//@Component
public class CookieBearerTokenResolver implements BearerTokenResolver {

    private final JwtCookieProperties jwtCookieProperties;
    public CookieBearerTokenResolver(JwtCookieProperties jwtCookieProperties) {
        this.jwtCookieProperties = jwtCookieProperties;
    }

    @Override
    public @Nullable String resolve(HttpServletRequest request) {
        System.out.println("=== COOKIE RESOLVER ===");
        Cookie[] cookies = request.getCookies();
        if(cookies ==  null) {
            System.out.println("NO COOKIES");
            return null;
        }
        for(Cookie cookie : cookies) {
            System.out.println(
                    "COOKIE: " +
                            cookie.getName() +
                            " = " +
                            cookie.getValue()
            );
            if(jwtCookieProperties.name.equals(cookie.getName())) {
                System.out.println("JWT COOKIE FOUND");
                return cookie.getValue();
            }
        }
        System.out.println("NO JWT COOKIE FOUND");
        return null;
    }
}
