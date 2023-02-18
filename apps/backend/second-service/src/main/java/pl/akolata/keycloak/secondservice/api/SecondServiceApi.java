package pl.akolata.keycloak.secondservice.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.OAuth2AuthorizeRequest;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import pl.akolata.keycloak.secondservice.api.model.JwtInfo;
import pl.akolata.keycloak.secondservice.api.model.ServerToServerResponse;
import pl.akolata.keycloak.secondservice.api.model.StringResponse;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/second-service")
class SecondServiceApi {
    private static final String FIRST_SERVICE_ENDPOINT_URL = "http://localhost:8081/api/first-service/private-resource-for-second-service";

    //    private final WebClient webClient;
    //    private final OAuth2AuthorizedClientService oauth2ClientService;
    private final OAuth2AuthorizedClientManager oAuth2AuthorizedClientManager;
    //    @Autowired
    //    private AuthorizedClientServiceOAuth2AuthorizedClientManager authorizedClientServiceAndManager;

    @GetMapping(value = "/call-first-service-pass-token")
    public ResponseEntity<ServerToServerResponse> callFirstServicePassToken() {
        log.info("GET /api/second-service/call-first-service-pass-token");

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String authTokenFromCurrentRequest = ((JwtAuthenticationToken) authentication).getToken().getTokenValue();

        ServerToServerResponse toServerResponse = ServerToServerResponse.builder()
            .jwtInfoInFirstServer(getJwtInfoFromFirstService(authTokenFromCurrentRequest))
            .jwtInfoInSecondServer(JwtInfo.fromAccessToken("in /api/second-service/call-first-service-pass-token endpoint"))
            .build();

        return ResponseEntity.ok(toServerResponse);
    }

    @GetMapping(value = "/call-first-service-as-service-client")
    public ResponseEntity<ServerToServerResponse> callSecondServiceAsServiceClient() {
        log.info("GET /api/second-service/call-first-service-as-service-client");

        //        oauth2ClientService.loadAuthorizedClient("keycloak", "altimeet-test-client");

        OAuth2AuthorizeRequest authorizeRequest =
            OAuth2AuthorizeRequest
                .withClientRegistrationId("keycloak")
                .principal("altimeet-test-client")
                .build();

        OAuth2AuthorizedClient authorizedClient = oAuth2AuthorizedClientManager.authorize(authorizeRequest);
        OAuth2AccessToken accessToken = authorizedClient.getAccessToken();

        ServerToServerResponse toServerResponse = ServerToServerResponse.builder()
            .jwtInfoInFirstServer(getJwtInfoFromFirstService(accessToken.getTokenValue()))
            .jwtInfoInSecondServer(JwtInfo.fromAccessToken("in /api/second-service/call-first-service-as-service-client endpoint"))
            .build();

        return ResponseEntity.ok(toServerResponse);
    }

    private JwtInfo getJwtInfoFromFirstService(String authToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + authToken);

        HttpEntity<StringResponse> request = new HttpEntity<>(headers);

        ResponseEntity<JwtInfo> responseEntity = new RestTemplate().exchange(
            FIRST_SERVICE_ENDPOINT_URL,
            HttpMethod.GET,
            request,
            JwtInfo.class
        );

        return responseEntity.getBody();
    }
}
