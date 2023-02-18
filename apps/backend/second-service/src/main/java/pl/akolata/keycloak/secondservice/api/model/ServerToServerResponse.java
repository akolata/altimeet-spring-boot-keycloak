package pl.akolata.keycloak.secondservice.api.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ServerToServerResponse {
    private JwtInfo jwtInfoInFirstServer;
    private JwtInfo jwtInfoInSecondServer;
}
