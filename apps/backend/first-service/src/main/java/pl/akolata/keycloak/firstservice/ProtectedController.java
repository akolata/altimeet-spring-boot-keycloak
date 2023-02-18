package pl.akolata.keycloak.firstservice;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/first-service")
class ProtectedController {

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
}
