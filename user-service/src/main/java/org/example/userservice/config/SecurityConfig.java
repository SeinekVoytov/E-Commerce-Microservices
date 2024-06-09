package org.example.userservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .oauth2ResourceServer(
                        oauth2Configurer -> oauth2Configurer.jwt(
                                customizer -> customizer.jwtAuthenticationConverter(jwtConverter())
                        )
                )
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(
                        authManager -> authManager
                                .requestMatchers(HttpMethod.POST, "/cart/add").permitAll()
                                .requestMatchers(HttpMethod.PATCH, "/cart/update/{itemId}").permitAll()
                                .requestMatchers(HttpMethod.DELETE, "/cart/delete/{itemId}").permitAll()
                                .anyRequest().authenticated()
                );

        return http.build();
    }

    @Bean
    public JwtAuthenticationConverter jwtConverter() {

        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        JwtGrantedAuthoritiesConverter grantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
        converter.setPrincipalClaimName("preferred_username");

        converter.setJwtGrantedAuthoritiesConverter(jwt -> {

            Collection<GrantedAuthority> authorities = grantedAuthoritiesConverter.convert(jwt);

            Map<String, Object> claims = jwt.getClaimAsMap("realm_access");
            List<String> roles = (List<String>) claims.get("roles");

            return Stream.concat(authorities.stream(),
                            roles.stream()
                                    .filter(role -> role.startsWith("ROLE"))
                                    .map(SimpleGrantedAuthority::new))
                    .toList();
        });

        return converter;
    }
}