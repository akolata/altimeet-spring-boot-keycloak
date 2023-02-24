import {Component, OnInit} from '@angular/core';
import {AuthConfig, OAuthService} from "angular-oauth2-oidc";
import {tap} from "rxjs";
import {HttpClient, HttpHeaders} from "@angular/common/http";
import jwtDecode, { JwtPayload } from "jwt-decode";

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent implements OnInit {
  authConfig: AuthConfig = {
    issuer: 'http://localhost:9990/auth/realms/altimeet',
    redirectUri: window.location.origin,
    clientId: 'altimeet-frontend-first-app',
    responseType: 'code',
    scope: 'openid profile email',
    showDebugInformation: true,
    sessionChecksEnabled: true,
  };

  decodedAccessToken: any;
  userInfo: any;
  firstServiceResponse: any;
  secondServiceResponsePassToken: any;
  secondServiceResponseAsServiceClient: any;

  constructor(public readonly oauthService: OAuthService, private readonly httpClient: HttpClient) {
  }

  ngOnInit() {
    this.oauthService.configure(this.authConfig);
    this.oauthService.loadDiscoveryDocumentAndTryLogin().then(
      _ => {
        if (this.oauthService.hasValidAccessToken()) {
          this.oauthService.loadUserProfile()
            .then(userInfo => this.userInfo = userInfo)
            .catch(_ => this.userInfo = null);

          this.decodedAccessToken = jwtDecode<JwtPayload>(this.oauthService.getAccessToken());
        }
      }
    );

    this.oauthService.events
      .pipe(tap(event => {
        console.log(event);
      }))

  }

  logout() {
    this.oauthService.revokeTokenAndLogout();
  }

  login() {
    this.oauthService.initCodeFlow();
  }

  callFirstService() {
    const headers: HttpHeaders = new HttpHeaders().set('Authorization', this.oauthService.authorizationHeader())
    this.httpClient.get<any>('/api/first-service/private-resource', {headers})
      .subscribe(
        res => this.firstServiceResponse = res,
        error => this.firstServiceResponse = error
      )
  }

  callSecondServicePassToken() {
    // scope 'altimeet-system:first-service:test-api-get' is missing
    const headers: HttpHeaders = new HttpHeaders().set('Authorization', this.oauthService.authorizationHeader())
    this.httpClient.get<any>('/api/second-service/call-first-service-pass-token', {headers})
      .subscribe(
        res => this.secondServiceResponsePassToken = res,
        error => this.secondServiceResponsePassToken = error
      )
  }

  callSecondServiceAsServiceClient() {
    const headers: HttpHeaders = new HttpHeaders().set('Authorization', this.oauthService.authorizationHeader())
    this.httpClient.get<any>('/api/second-service/call-first-service-as-service-client', {headers})
      .subscribe(
      res => this.secondServiceResponseAsServiceClient = res,
        error => this.secondServiceResponseAsServiceClient = error
    )
  }

  logoutExternally() {
    window.open(this.oauthService.logoutUrl);
  }
}
