package pl.akolata.keycloak.secondservice.config;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Classes worth reading:
 * org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter
 * org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter
 */
public class KeycloakGrantedAuthorityConverter implements Converter<Jwt, Collection<GrantedAuthority>> {
    private static final String ROLE_PREFIX = "ROLE_";
    private static final String SCOPE_PREFIX = "SCOPE_";

    @Override
    public Collection<GrantedAuthority> convert(Jwt jwt) {
        List<SimpleGrantedAuthority> roles = mapRoles(jwt);
        List<SimpleGrantedAuthority> scopes = mapScopes(jwt);
        return Stream.concat(roles.stream(), scopes.stream()).collect(Collectors.toList());
    }

    private List<SimpleGrantedAuthority> mapRoles(Jwt jwt) {
        Map<String, Object> realmAccess = (Map<String, Object>) jwt.getClaims().get("realm_access");
        if (realmAccess == null || realmAccess.isEmpty()) {
            return Collections.emptyList();
        }
        return ((List<String>) realmAccess.get("roles"))
            .stream()
            .map(roleName -> ROLE_PREFIX + roleName)
            .map(SimpleGrantedAuthority::new)
            .toList();

    }


    private List<SimpleGrantedAuthority> mapScopes(Jwt jwt) {
        String scope = jwt.getClaimAsString("scope");
        List<String> scopes = StringUtils.hasText(scope) ? Arrays.asList((scope).split(" ")) : Collections.emptyList();
        return scopes.stream()
            .map(roleName -> SCOPE_PREFIX + roleName)
            .map(SimpleGrantedAuthority::new)
            .toList();
    }
}
