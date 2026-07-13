package com.hilton.hotel.security;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public class KeyCloakRoleConverter implements Converter<Jwt , Collection<GrantedAuthority>> {
    @Override
    public Collection<GrantedAuthority> convert(Jwt source) {
        Map<String, Object> realmAccess =
                (Map<String, Object>) source.getClaims().get("realm_access");

        if (realmAccess == null || !realmAccess.containsKey("roles")) {
            return List.of();
        }
        List<String> roles = (List<String>) realmAccess.get("roles");
        return List.of();
    }
}