package ma.ensa.StorageManager.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // Disable CSRF for testing purposes
                .cors(Customizer.withDefaults()) // Enable CORS with defaults
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/files/**").permitAll() // Public access for file endpoints
                        .anyRequest().authenticated() // Require authentication for other requests
                );
        return http.build();
    }
}
