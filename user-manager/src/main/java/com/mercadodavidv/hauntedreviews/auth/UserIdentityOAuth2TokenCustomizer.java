package com.mercadodavidv.hauntedreviews.auth;

import com.mercadodavidv.hauntedreviews.user.Role;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.core.oidc.endpoint.OidcParameterNames;
import org.springframework.security.oauth2.server.authorization.OAuth2TokenType;
import org.springframework.security.oauth2.server.authorization.token.JwtEncodingContext;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenCustomizer;

public final class UserIdentityOAuth2TokenCustomizer implements
    OAuth2TokenCustomizer<JwtEncodingContext> {

  private final UserIdentityService userIdentityService;

  public UserIdentityOAuth2TokenCustomizer(UserIdentityService userIdentityService) {
    this.userIdentityService = userIdentityService;
  }

  @Override
  public void customize(JwtEncodingContext context) {

    // Find the User
    UserIdentity user;
    if (context.getPrincipal().getPrincipal() instanceof UserIdentity registeredUser) {
      // Email/Password Identity
      user = registeredUser;
    } else {
      // Federated Identity
      // Find the User by the Social Account
      String providerId = ((OAuth2AuthenticationToken) context.getPrincipal()).getAuthorizedClientRegistrationId(); // i.e. "google-idp", "github-idp". See application.yml for current list of providers.
      String principalName = context.getPrincipal().getName();
      user = userIdentityService.getUserBySocialAccountKey(principalName, providerId);
    }

    Set<String> roles = user.getRoles().stream().map(Role::name)
        .collect(Collectors.toUnmodifiableSet());

    // Customize the access token and ID token
    if (OAuth2TokenType.ACCESS_TOKEN.equals(context.getTokenType())) {
      context.getClaims().claims(claims -> {
        claims.put("sub", user.getId());
        claims.put("roles", roles);
      });
    } else if (OidcParameterNames.ID_TOKEN.equals(context.getTokenType().getValue())) {
      context.getClaims().claims(claims -> {
        claims.putAll(createOidcUserInfo(user));
        claims.put("roles", roles);
      });
    }
  }

  private static Map<String, Object> createOidcUserInfo(UserIdentity user) {

    // @formatter:off
    return OidcUserInfo.builder()
        .subject(String.valueOf(user.getId()))
        .name(user.getUsername())
        .givenName(null)
        .familyName(null)
        .middleName(null)
        .nickname(user.getUsername())
        .preferredUsername(user.getUsername())
        .profile("https://haunted-reviews.com/" + user.getUsername())
        .picture(user.getProfileImageUrl())
        .website(null)
        .email(user.getEmail())
        .emailVerified(user.getEmailVerified())
        .gender(null)
        .birthdate(null)
        .zoneinfo(null)
        .locale(null)
        .phoneNumber(null)
        .phoneNumberVerified(false)
        .address(null)
        .updatedAt(String.valueOf(user.getLastModifiedDate().getEpochSecond()))
        .build()
        .getClaims();
    // @formatter:on
  }
}
