import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import {OAuthModule, OAuthStorage} from "angular-oauth2-oidc";
import {HttpClientModule} from "@angular/common/http";
import { PrettyPrintPipe } from './prettyprint.pipe';

export function storageFactory() : OAuthStorage {
  return localStorage;
}

@NgModule({
  declarations: [
    AppComponent,
    PrettyPrintPipe
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    HttpClientModule,
    OAuthModule.forRoot()
  ],
  providers: [
    { provide: OAuthStorage, useFactory: storageFactory }
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
