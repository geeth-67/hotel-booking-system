package com.hilton.hotel.config;

import com.hilton.hotel.security.KeyCloakRoleConverter;
import org.hibernate.annotations.ConcreteProxy;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;

@ConcreteProxy
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    public SecurityFilterChain filterChain(HttpSecurity http) throws  Exception {

        return http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session
                        -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth
                        -> auth.requestMatchers(HttpMethod.GET, "/api/rooms/**").permitAll()
                        .anyRequest().authenticated()).headers((headers)
                        -> headers.frameOptions(frame -> frame.sameOrigin()))
                .oauth2ResourceServer(oauth2
                        -> oauth2.jwt((jwt -> jwt.jwtAuthenticationConverter(KeyCloakJwtConverter()))))
                .build();
    }

    public JwtAuthenticationConverter KeyCloakJwtConverter() {
        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(new KeyCloakRoleConverter());
        return converter;
    }
}
