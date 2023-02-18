package pl.akolata.keycloak.firstservice.api;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.akolata.keycloak.firstservice.api.model.JwtInfo;
import pl.akolata.keycloak.firstservice.SecurityUtil;
import pl.akolata.keycloak.firstservice.api.model.StringResponse;
import pl.akolata.keycloak.firstservice.api.model.UserInfoResponse;

@RestController
@RequestMapping(value = "/api/first-service")
class FirstServiceApi {

    @GetMapping(value = "/public-resource")
    public ResponseEntity<StringResponse> getPublicResource() {
        StringResponse response = StringResponse.builder().message("Message from the public resource").build();
        return ResponseEntity.ok(response);
    }

    @GetMapping(value = "/private-resource")
    public ResponseEntity<UserInfoResponse> getPrivateResource() {
        UserInfoResponse response = UserInfoResponse.builder()
            .json(SecurityUtil.getAuthenticationAsJson())
            .authenticationClass(SecurityUtil.getAuthenticationClass())
            .build();

        return ResponseEntity.ok(response);
    }

    @GetMapping(value = "/private-resource-role-protected")
    public ResponseEntity<StringResponse> getPrivateRoleProtectedResource() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        StringResponse response = StringResponse.builder().message("Message from the private, role-protected resource").build();
        return ResponseEntity.ok(response);
    }

    @GetMapping(value = "/private-resource-scope-protected")
    public ResponseEntity<StringResponse> getPrivateScopeProtectedResource() {
        StringResponse response = StringResponse.builder().message("Message from the private, scope-protected resource").build();
        return ResponseEntity.ok(response);
    }

    @GetMapping(value = "/private-resource-for-second-service")
    public ResponseEntity<JwtInfo> getPrivateResourceForSecondService() {
        return ResponseEntity.ok(JwtInfo.fromAccessToken("In /api/first-service/private-resource-for-second-service endpoint"));
    }
}
