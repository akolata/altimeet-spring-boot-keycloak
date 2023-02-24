package pl.akolata.keycloak.firstservice.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.akolata.keycloak.firstservice.api.model.JwtInfo;

@RestController
@RequestMapping(value = "/api/first-service")
class FirstServiceApi {

    @GetMapping(value = "/public-resource")
    public ResponseEntity<JwtInfo> getPublicResource() {
        return ResponseEntity.ok(JwtInfo.fromAccessToken("/api/first-service/public-resource"));
    }

    @GetMapping(value = "/private-resource")
    public ResponseEntity<JwtInfo> getPrivateResource() {
        return ResponseEntity.ok(JwtInfo.fromAccessToken("/api/first-service/private-resource"));
    }

    @GetMapping(value = "/private-resource-role-protected")
    public ResponseEntity<JwtInfo> getPrivateRoleProtectedResource() {
        return ResponseEntity.ok(JwtInfo.fromAccessToken("/api/first-service/private-resource-role-protected"));
    }

    @GetMapping(value = "/private-resource-scope-protected")
    public ResponseEntity<JwtInfo> getPrivateScopeProtectedResource() {
        return ResponseEntity.ok(JwtInfo.fromAccessToken("/api/first-service/private-resource-scope-protected"));
    }

    @GetMapping(value = "/private-resource-for-second-service")
    public ResponseEntity<JwtInfo> getPrivateResourceForSecondService() {
        return ResponseEntity.ok(JwtInfo.fromAccessToken("In /api/first-service/private-resource-for-second-service endpoint"));
    }
}
