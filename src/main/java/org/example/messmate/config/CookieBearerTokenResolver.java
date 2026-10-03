package org.example.messmate.config;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.example.messmate.properties.JwtCookieProperties;
import org.jspecify.annotations.Nullable;
import org.springframework.security.oauth2.server.resource.web.BearerTokenResolver;

//@Component
public class CookieBearerTokenResolver implements BearerTokenResolver {

    private final JwtCookieProperties jwtCookieProperties;
    public CookieBearerTokenResolver(JwtCookieProperties jwtCookieProperties) {
        this.jwtCookieProperties = jwtCookieProperties;
    }

    @Override
    public @Nullable String resolve(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if(cookies ==  null) {
            return null;
        }
        for(Cookie cookie : cookies) {
            if(jwtCookieProperties.name.equals(cookie.getName())) {
                return cookie.getValue();
            }
        }
        return null;
    }
}
