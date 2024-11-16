package com.mercadodavidv.hauntedreviews.auth.federation;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.function.BiConsumer;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class FederatedIdentityAuthenticationSuccessHandler implements
    AuthenticationSuccessHandler {

  private final AuthenticationSuccessHandler delegate = new SavedRequestAwareAuthenticationSuccessHandler();

  private BiConsumer<OAuth2User, String> oauth2UserHandler;

  private final BiConsumer<OidcUser, String> oidcUserHandler = (oAuth2User, providerId) -> this.oauth2UserHandler.accept(
      oAuth2User, providerId);

  public FederatedIdentityAuthenticationSuccessHandler(
      BiConsumer<OAuth2User, String> oauth2UserHandler) {
    this.oauth2UserHandler = oauth2UserHandler;
  }

  @Override
  @Transactional
  public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
      Authentication authentication) throws IOException, ServletException {
    if (authentication instanceof OAuth2AuthenticationToken oAuth2AuthenticationToken) {
      String providerId = oAuth2AuthenticationToken.getAuthorizedClientRegistrationId();
      if (authentication.getPrincipal() instanceof OidcUser oidcUser) {
        this.oidcUserHandler.accept(oidcUser, providerId);
      } else if (authentication.getPrincipal() instanceof OAuth2User oAuth2User) {
        this.oauth2UserHandler.accept(oAuth2User, providerId);
      }
    }

    this.delegate.onAuthenticationSuccess(request, response, authentication);
  }
}
