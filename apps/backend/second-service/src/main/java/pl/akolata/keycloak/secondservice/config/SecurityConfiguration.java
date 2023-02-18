package pl.akolata.keycloak.secondservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.client.*;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository;
import org.springframework.security.oauth2.client.web.reactive.function.client.ServletOAuth2AuthorizedClientExchangeFilterFunction;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true, jsr250Enabled = true)
class SecurityConfiguration {

    @Bean
    public WebClient webClient(
        ClientRegistrationRepository clientRegistrationrepository,
        OAuth2AuthorizedClientRepository oAuth2AuthorizedClientRepository) {

        ServletOAuth2AuthorizedClientExchangeFilterFunction oauth2 =
            new ServletOAuth2AuthorizedClientExchangeFilterFunction(clientRegistrationrepository,
                oAuth2AuthorizedClientRepository);

        oauth2.setDefaultOAuth2AuthorizedClient(true);

        return WebClient.builder()
            .clientConnector(new ReactorClientHttpConnector(
                HttpClient.create().wiretap(true)
            ))
            .apply(oauth2.oauth2Configuration()).build();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(new KeycloakGrantedAuthorityConverter());

        http.authorizeRequests(authorize ->
                authorize
                    .antMatchers("/api/second-service/call-first-service-as-service-client").authenticated()
                    .anyRequest().authenticated()
            )
            .oauth2Client()
            .and()
            .oauth2ResourceServer().jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter));
        http.csrf().disable();
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        return http.build();
    }

    @Bean
    ClientRegistration keycloakClientRegistration(
        @Value("${spring.security.oauth2.client.provider.keycloak.token-uri}") String token_uri,
        @Value("${spring.security.oauth2.client.registration.keycloak.client-id}") String client_id,
        @Value("${spring.security.oauth2.client.registration.keycloak.client-secret}") String client_secret,
        //        @Value("${spring.security.oauth2.client.registration.keycloak.scope:[]}") Set<String> scope,
        @Value("${spring.security.oauth2.client.registration.keycloak.authorization-grant-type}") String authorizationGrantType
    ) {
        return ClientRegistration
            .withRegistrationId("keycloak")
            .tokenUri(token_uri)
            .clientId(client_id)
            .clientSecret(client_secret)
            .scope("altimeet-system:first-service:test-api-get")
            .authorizationGrantType(new AuthorizationGrantType(authorizationGrantType))
            .build();
    }

    @Bean
    public ClientRegistrationRepository clientRegistrationRepository(ClientRegistration keycloakClientRegistration) {
        return new InMemoryClientRegistrationRepository(keycloakClientRegistration);
    }

    @Bean
    public OAuth2AuthorizedClientService auth2AuthorizedClientService(ClientRegistrationRepository clientRegistrationRepository) {
        return new InMemoryOAuth2AuthorizedClientService(clientRegistrationRepository);
    }

    @Bean
    public AuthorizedClientServiceOAuth2AuthorizedClientManager authorizedClientServiceAndManager(
        ClientRegistrationRepository clientRegistrationRepository,
        OAuth2AuthorizedClientService authorizedClientService) {

        OAuth2AuthorizedClientProvider authorizedClientProvider =
            OAuth2AuthorizedClientProviderBuilder.builder()
                .clientCredentials()
                .build();

        AuthorizedClientServiceOAuth2AuthorizedClientManager authorizedClientManager =
            new AuthorizedClientServiceOAuth2AuthorizedClientManager(
                clientRegistrationRepository, authorizedClientService);
        authorizedClientManager.setAuthorizedClientProvider(authorizedClientProvider);

        return authorizedClientManager;
    }
}
