package org.example.messmate.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
public class JwtService {

    @Value("${jwt.issuer}")
    private String issuer;

    @Value("${jwt.expiry}")
    private Long expiry;

    private final JwtEncoder jwtEncoder;
    public JwtService(JwtEncoder jwtEncoder) {
        this.jwtEncoder = jwtEncoder;
    }

    public String generateAccessToken(
            UserDetails userDetails
    ){
        Instant now = Instant.now();

        List<String> roles = userDetails
                .getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer(issuer)
                .subject(userDetails.getUsername())
                .issuedAt(now)
                .expiresAt(now.plusSeconds(expiry))
                .claim("roles", roles)
                .build();

        Jwt jwtToken = jwtEncoder.encode(
                JwtEncoderParameters.from(claims)
        );

        return jwtToken.getTokenValue();
    }
}
