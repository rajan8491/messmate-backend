package org.example.messmate.config;

import org.example.messmate.exception.security.CustomAccessDeniedHandler;
import org.example.messmate.exception.security.CustomAuthenticationEntryPoint;
import org.springframework.boot.security.autoconfigure.actuate.web.servlet.EndpointRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.web.BearerTokenResolver;
import org.springframework.security.web.SecurityFilterChain;


@Configuration
public class SecurityConfig {
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            JwtDecoder jwtDecoder,
            JwtAuthenticationConverter jwtAuthenticationConverter,
            
            CustomAuthenticationEntryPoint customAuthenticationEntryPoint,
            CustomAccessDeniedHandler customAccessDeniedHandler
    ) {
        http
                .cors(Customizer.withDefaults())
                .csrf(csrf -> csrf.disable())

                .sessionManagement(session ->
                        session.sessionCreationPolicy(
                                SessionCreationPolicy.STATELESS
                        )
                )

                .authorizeHttpRequests(auth -> auth
                        // Public actuator endpoint
                        .requestMatchers(
                                EndpointRequest.to(
                                        "health"
                                )
                        )
                        .permitAll()

                        // All other actuator endpoints
                        .requestMatchers(
                                EndpointRequest.toAnyEndpoint()
                        )
                        .hasRole("ADMIN")

                        .requestMatchers("/api/auth/me")
                        .authenticated()


                        .requestMatchers("/api/auth/**")
                        .permitAll()

                        .requestMatchers("/api/public/**")
                        .permitAll()

                        .requestMatchers("/api/admin/**")
                        .hasRole("ADMIN")

                        .requestMatchers("/api/student/**")
                        .hasRole("STUDENT")

                        .requestMatchers("/api/accountant/**")
                        .hasRole("ACCOUNTANT")

                        .anyRequest()
                        .authenticated()
                )

                .oauth2ResourceServer(oauth2 ->
                        oauth2/*.bearerTokenResolver(
                                        cookieBearerTokenResolver
                                )*/
                                .jwt(jwt -> jwt
                                        .decoder(jwtDecoder)
                                        .jwtAuthenticationConverter(
                                                jwtAuthenticationConverter
                                        )
                        )
                )
                .exceptionHandling(ex -> ex
                                .authenticationEntryPoint(customAuthenticationEntryPoint)
                                .accessDeniedHandler(customAccessDeniedHandler)
                );

        return http.build();
    }

}
