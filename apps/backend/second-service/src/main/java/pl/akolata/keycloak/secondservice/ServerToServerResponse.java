package pl.akolata.keycloak.secondservice;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ServerToServerResponse {
    private String firstServerMessage;
    private String secondServerMessage;
}
