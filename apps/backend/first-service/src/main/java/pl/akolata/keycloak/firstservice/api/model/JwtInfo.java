package pl.akolata.keycloak.firstservice.api.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JwtInfo {
    private String description;
    private String name;
    private String accessToken;
    private String preferredUsername;
    private String id;
    private String subject;

    public static JwtInfo fromAccessToken(String description) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || (authentication instanceof AnonymousAuthenticationToken) || !authentication.isAuthenticated()) {
            return JwtInfo.builder().description(description).build();
        }

        JwtAuthenticationToken jwtAuthenticationToken = (JwtAuthenticationToken) authentication;
        Jwt jwt = (Jwt) authentication.getPrincipal();

        return JwtInfo.builder()
            .description(description)
            .name(jwtAuthenticationToken.getName())
            .accessToken(jwt.getTokenValue())
            .id(jwt.getId())
            .subject(jwt.getSubject())
            .preferredUsername(jwt.getClaimAsString("preferred_username"))
            .build();
    }
}
