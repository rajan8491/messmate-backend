package org.example.messmate.properties;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "jwt.cookie")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class JwtCookieProperties {
    public String name;
    public String path;
    public long maxAge;
    public boolean httpOnly;
    public boolean secure;
    public String sameSite;
}
