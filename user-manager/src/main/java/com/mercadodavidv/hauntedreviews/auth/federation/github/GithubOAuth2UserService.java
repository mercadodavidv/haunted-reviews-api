package com.mercadodavidv.hauntedreviews.auth.federation.github;

import com.mercadodavidv.hauntedreviews.auth.federation.SocialAccountLinkingException;
import java.net.URI;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.http.OAuth2ErrorResponseErrorHandler;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * Custom OAuth2UserService that loads a GitHub user's primary email from the '/user/emails'
 * endpoint. If a GitHub user has their email set to private, then their user info does not contain
 * their email.
 * <p>
 * NOTE: Use with a DelegatingOAuth2UserService and a DefaultOAuth2UserService to load all other
 * OAuth2 providers.
 */
public class GithubOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

  private static final String INVALID_GITHUB_USER_EMAILS_RESPONSE_ERROR_CODE = "invalid_github_user_emails_response";

  private static final ParameterizedTypeReference<List<GithubUserEmailResponse>> GH_EMAIL_LIST_PARAMETERIZED_TYPE_REFERENCE = new ParameterizedTypeReference<>() {
  };

  @Override
  public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

    String providerId = userRequest.getClientRegistration().getRegistrationId();
    if ("github-idp".equals(providerId)) {
      try {
        //////////////////////
        final DefaultOAuth2UserService delegate = new DefaultOAuth2UserService();

        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setErrorHandler(new OAuth2ErrorResponseErrorHandler());

        // Delegate to the default implementation for loading a user
        OAuth2User oAuth2User = delegate.loadUser(userRequest);
        //////////////////////

        HttpMethod httpMethod = HttpMethod.GET;
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(userRequest.getAccessToken().getTokenValue());
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        URI uri = UriComponentsBuilder.fromUriString("https://api.github.com/user/emails").build()
            .toUri();

        RequestEntity<?> request = new RequestEntity<>(headers, httpMethod, uri);
        ResponseEntity<List<GithubUserEmailResponse>> response = restTemplate.exchange(request,
            GH_EMAIL_LIST_PARAMETERIZED_TYPE_REFERENCE);
        List<GithubUserEmailResponse> emails = Objects.requireNonNullElse(response.getBody(),
            List.of());

        // Find a usable email. It must be primary and verified.
        // @formatter:off
        String primaryVerifiedEmail = emails.stream()
            .filter(emailResponse -> emailResponse.primary() && emailResponse.verified())
            .findFirst()
            .orElse(new GithubUserEmailResponse(null, false, false))
            .email();
        // formatter:on
        if (StringUtils.isBlank(primaryVerifiedEmail)) {
          throw new SocialAccountLinkingException(
              "An error occurred while attempting to retrieve the GitHub user's email, which must be a verified primary email.",
              providerId);
        }

        LinkedHashMap<String, Object> newAttributes = new LinkedHashMap<>(
            oAuth2User.getAttributes());
        newAttributes.put("email", primaryVerifiedEmail);
        newAttributes.put("email_verified", Boolean.TRUE);
        return new DefaultOAuth2User(oAuth2User.getAuthorities(),
            Collections.unmodifiableMap(newAttributes), "id");
      } catch (RestClientException ex) {
        OAuth2Error oauth2Error = new OAuth2Error(INVALID_GITHUB_USER_EMAILS_RESPONSE_ERROR_CODE,
            "An error occurred while attempting to retrieve the GitHub user's list of emails: "
                + ex.getMessage(), null);
        throw new OAuth2AuthenticationException(oauth2Error, oauth2Error.toString(), ex);
      }
    }
    return null;
  }
}
