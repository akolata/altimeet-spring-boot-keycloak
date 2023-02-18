package pl.akolata.keycloak.secondservice;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.*;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/second-service")
class SecondServiceApi {
    private final WebClient webClient;
    private final OAuth2AuthorizedClientService oauth2ClientService;
    private final OAuth2AuthorizedClientManager oAuth2AuthorizedClientManager;
    //    @Autowired
    //    private AuthorizedClientServiceOAuth2AuthorizedClientManager authorizedClientServiceAndManager;

    @GetMapping(value = "/call-first-service-pass-token")
    public ResponseEntity<ServerToServerResponse> callFirstServicePassToken() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Object principal = authentication.getPrincipal();

        OAuth2AuthorizeRequest authorizeRequest =
            OAuth2AuthorizeRequest
                .withClientRegistrationId("keycloak")
                .principal("altimeet-test-client")
                .build();

        OAuth2AuthorizedClient authorizedClient =
            this.oAuth2AuthorizedClientManager
                .authorize(authorizeRequest);

        OAuth2AccessToken accessToken = authorizedClient.getAccessToken();

        String url = "http://localhost:8081/api/first-service/private-resource-for-second-service";

        //        StringResponse albums = webClient.get()
        //            .uri(url)
        //            .retrieve()
        //            .bodyToMono(new ParameterizedTypeReference<StringResponse>(){})
        //            .block();

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + ((JwtAuthenticationToken) authentication).getToken().getTokenValue());
        HttpEntity<StringResponse> request = new HttpEntity<>(headers);

        // Make the actual HTTP GET request
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<StringResponse> response = restTemplate.exchange(
            url,
            HttpMethod.GET,
            request,
            StringResponse.class
        );

        StringBuilder sb = new StringBuilder("Message from GET /api/second-service/call-first-service-pass-token endpoint");

        if (authentication instanceof JwtAuthenticationToken jwtAuthenticationToken) {
            sb.append(" username ").append(jwtAuthenticationToken.getToken().getClaimAsString("preferred_username"));
            sb.append(" authentication JwtAuthenticationToken");
        }

        sb.append(" principal ").append(authentication.getPrincipal().getClass());
        ServerToServerResponse toServerResponse = ServerToServerResponse.builder()
            .firstServerMessage(response.getBody().getMessage())
            .secondServerMessage(sb.toString())
            .build();

        return ResponseEntity.ok(toServerResponse);
    }

    @GetMapping(value = "/call-first-service-as-service-client")
    public ResponseEntity<ServerToServerResponse> callSecondServiceAsServiceClient() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Object principal = authentication.getPrincipal();
        log.info("GET /api/second-service/call-first-service-as-service-client");
        if (principal instanceof Jwt jwt) {
            log.info("\tprincipal is {}, {}", jwt.getClaimAsString("sub"), jwt);
        }

        oauth2ClientService.loadAuthorizedClient("keycloak", "altimeet-test-client");

        OAuth2AuthorizeRequest authorizeRequest =
            OAuth2AuthorizeRequest
                .withClientRegistrationId("keycloak")
                .principal("altimeet-test-client")
                .build();

        OAuth2AuthorizedClient authorizedClient =
            this.oAuth2AuthorizedClientManager
                .authorize(authorizeRequest);

        OAuth2AccessToken accessToken = authorizedClient.getAccessToken();

        String url = "http://localhost:8081/api/first-service/private-resource-for-second-service";

        //        StringResponse albums = webClient.get()
        //            .uri(url)
        //            .retrieve()
        //            .bodyToMono(new ParameterizedTypeReference<StringResponse>(){})
        //            .block();

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + accessToken.getTokenValue());
        HttpEntity<StringResponse> request = new HttpEntity<>(headers);

        // Make the actual HTTP GET request
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<StringResponse> response = restTemplate.exchange(
            url,
            HttpMethod.GET,
            request,
            StringResponse.class
        );


        StringBuilder sb = new StringBuilder("Message from GET /api/second-service/call-first-service-as-service-client endpoint");

        if (authentication instanceof JwtAuthenticationToken jwtAuthenticationToken) {
            sb.append(" username ").append(jwtAuthenticationToken.getToken().getClaimAsString("preferred_username"));
            sb.append(" authentication JwtAuthenticationToken");
        }

        sb.append(" principal ").append(authentication.getPrincipal().getClass());
        ServerToServerResponse toServerResponse = ServerToServerResponse.builder()
            .firstServerMessage(response.getBody().getMessage())
            .secondServerMessage(sb.toString())
            .build();

        return ResponseEntity.ok(toServerResponse);
    }
}
