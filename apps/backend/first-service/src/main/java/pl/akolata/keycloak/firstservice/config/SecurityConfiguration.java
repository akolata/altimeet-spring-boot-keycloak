package pl.akolata.keycloak.firstservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true, jsr250Enabled = true)
class SecurityConfiguration {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(new KeycloakGrantedAuthorityConverter());

        http.authorizeRequests(authorize ->
                authorize
                    .antMatchers("/api/first-service/public-resource").permitAll()
                    .antMatchers("/api/first-service/private-resource").authenticated()
                    .antMatchers("/api/first-service/private-resource-role-protected").hasRole("altimeet_role")
                    .antMatchers("/api/first-service/private-resource-scope-protected").hasAuthority("SCOPE_altimeet_test_scope")
                    .antMatchers("/api/first-service/private-resource-for-second-service")
                    .hasAuthority("SCOPE_altimeet-system:first-service:test-api-get")
                    .anyRequest().authenticated()
            )
            .oauth2ResourceServer().jwt().jwtAuthenticationConverter(jwtAuthenticationConverter);
        http.csrf().disable();
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        return http.build();
    }
}
