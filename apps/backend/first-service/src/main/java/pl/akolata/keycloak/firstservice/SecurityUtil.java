package pl.akolata.keycloak.firstservice;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import java.util.Optional;


@UtilityClass
public class SecurityUtil {

    private static final ObjectMapper OM;

    static {
        ObjectMapper om = new ObjectMapper();
        om.registerModule(new JavaTimeModule());
        om.setSerializationInclusion(JsonInclude.Include.ALWAYS);
        om.enable(SerializationFeature.INDENT_OUTPUT);

        OM = om;
    }

    @SneakyThrows
    String getAuthenticationAsJson() {
        return Optional.ofNullable(SecurityContextHolder.getContext())
            .map(SecurityContext::getAuthentication)
            .map(authentication -> {
                try {
                    return OM.writerWithDefaultPrettyPrinter().writeValueAsString(authentication);
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
            })
            .orElse("{}");
    }

    String getAuthenticationClass() {
        return Optional.ofNullable(SecurityContextHolder.getContext())
            .map(SecurityContext::getAuthentication)
            .map(clz -> clz.getClass().getSimpleName())
            .orElse("");
    }

    Optional<String> getCustomClaim(@NonNull String claim) {
        return Optional.ofNullable(SecurityContextHolder.getContext()).map(SecurityContext::getAuthentication)
            .filter(authentication -> authentication instanceof JwtAuthenticationToken)
            .map(authentication -> (JwtAuthenticationToken) authentication)
            .map(JwtAuthenticationToken::getToken)
            .filter(jwt -> jwt.hasClaim(claim))
            .map(jwt -> jwt.getClaimAsString(claim));
    }
}
