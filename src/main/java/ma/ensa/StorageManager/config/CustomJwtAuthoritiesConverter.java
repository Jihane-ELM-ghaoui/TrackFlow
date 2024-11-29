// CustomJwtAuthoritiesConverter.java
package ma.ensa.StorageManager.config;

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
        // Start with an empty collection
        Collection<GrantedAuthority> authorities = List.of();

        // Safely retrieve roles from the JWT claims
        List<String> roles = jwt.getClaimAsStringList("https://demo.app.com/roles");

        // Convert roles to GrantedAuthority and return
        if (roles != null) {
            authorities = roles.stream()
                    .map(role -> new SimpleGrantedAuthority("ROLE_" + role.toUpperCase()))
                    .collect(Collectors.toList());
        }

        return authorities;
    }
}
