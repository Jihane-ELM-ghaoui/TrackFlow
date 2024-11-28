package com.example.userKPI.config;


import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class CustomJwtAuthoritiesConverter implements Converter<Jwt, Collection<GrantedAuthority>> {
    @Override
    public Collection<GrantedAuthority> convert(Jwt jwt) {
        // Start with the default authorities
        Collection<GrantedAuthority> authorities = List.of();

        // Extract roles from the JWT claims
        List<String> roles = (List<String>) jwt.getClaims().get("https://demo.app.com/roles");

        // Convert roles to GrantedAuthority and return
        if (roles != null) {
            authorities = roles.stream()
                    .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                    .collect(Collectors.toList());
        }

        return authorities;
    }
}
