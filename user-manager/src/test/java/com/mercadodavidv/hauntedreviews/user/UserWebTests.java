package com.mercadodavidv.hauntedreviews.user;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;

import com.mercadodavidv.hauntedreviews.config.DefaultSecurityConfig;
import com.mercadodavidv.hauntedreviews.common.ErrorResponse;
import com.mercadodavidv.hauntedreviews.config.WebTestClientBuilderCustomizerConfig;
import com.mercadodavidv.hauntedreviews.user.projection.UserPrivateProfile;
import com.mercadodavidv.hauntedreviews.user.projection.UserProfile;
import jakarta.persistence.EntityManager;
import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import lombok.Value;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.core.TypeReferences;
import org.springframework.http.client.reactive.ClientHttpConnector;
import org.springframework.lang.NonNull;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.test.web.reactive.server.WebTestClientConfigurer;
import org.springframework.test.web.servlet.client.MockMvcHttpConnector;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.adapter.WebHttpHandlerBuilder;

@SpringBootTest(properties = {"spring.profiles.include=test"})
@AutoConfigureMockMvc
@Import(WebTestClientBuilderCustomizerConfig.class)
@Transactional
class UserWebTests {

  @Autowired
  private WebTestClient client;

  @Autowired
  private EntityManager entityManager;

  @Test
  void userProfile_Exists() {

    String username = "testuser1";
    // @formatter:off
    this.entityManager.persist(User.builder()
        .email(username + "@example.com")
        .username(username)
        .password("{noop}abcde")
        .emailVerifiedDate(Instant.now())
        .roles(Set.of(Role.BASIC_USER))
        .build()
    );

    client.get().uri("/user/" + username + "/profile")
        .exchange()
        .expectStatus().isOk()
        .expectBody(new TypeReferences.EntityModelType<UserProfileResponse>() {})
        .consumeWith(result -> {
          EntityModel<UserProfileResponse> model = result.getResponseBody();
          assertThat(model).isNotNull();
          assertThat(model.getContent()).isNotNull();
          assertThat(model.getContent().getUsername()).isEqualTo(username);
          assertThat(model.getRequiredLink(IanaLinkRelations.SELF)).isEqualTo(
              Link.of("http://localhost/user/" + username + "/profile", IanaLinkRelations.SELF));
        });
    // @formatter:on
    this.entityManager.flush();
  }

  @Test
  void userProfile_DoesNotExist() {

    String username = "testuser1";
    // @formatter:off
    client.get().uri("/user/" + username + "/profile")
        .exchange()
        .expectStatus().isNotFound()
        .expectBody(ErrorResponse.class)
        .consumeWith(result -> {
          ErrorResponse model = result.getResponseBody();
          assertThat(model).isNotNull();
          assertThat(model.getError()).isEqualTo("User Not Found");
          assertThat(model.getRequiredLink("user-profile")).isEqualTo(
              Link.of("http://localhost/user/" + username + "/profile", "user-profile"));
        });
    // @formatter:on
  }

  @Test
  void userPrivateProfile_Exists() {

    String username = "testuser1";
    // @formatter:off
    User user = User.builder()
        .email(username + "@example.com")
        .username(username)
        .password("{noop}abcde")
        .emailVerifiedDate(Instant.now())
        .roles(Set.of(Role.STAFF))
        .build();
    SocialAccount socialAccount1 = SocialAccount.builder()
        .principalName("98765")
        .providerId("mock-idp")
        .email(username + "@example.com")
        .user(user)
        .build();
    SocialAccount socialAccount2 = SocialAccount.builder()
        .principalName("4321")
        .providerId("some-idp")
        .email("ThisIsOkay@example.com")
        .user(user)
        .build();
    this.entityManager.persist(user);
    this.entityManager.persist(socialAccount1);
    this.entityManager.persist(socialAccount2);
    this.entityManager.flush();
    this.entityManager.refresh(user);

    Long userId = user.getId();

    client
        .mutateWith(jwtConfigurer().jwt(jwt -> jwt
            .claim("roles", Set.of(Role.STAFF.name())) // Thanks, RoleHierarchy.
            .claim("scope", "openid profile email user")
            .claim("sub", userId)))
        .get().uri("/user-account/" + userId)
        .exchange()
        .expectStatus().isOk()
        .expectBody(new TypeReferences.EntityModelType<UserPrivateProfileResponse>() {})
        .consumeWith(result -> {
          EntityModel<UserPrivateProfileResponse> model = result.getResponseBody();
          assertThat(model).isNotNull();
          assertThat(model.getContent()).isNotNull();
          assertThat(model.getContent().getUsername()).isEqualTo(username);
          assertThat(model.getContent().getRoles()).isEqualTo(Set.of(Role.STAFF));
          assertThat(model.getContent().getSocialAccounts()).extracting("providerId", "email")
              .containsOnly(tuple("mock-idp", username + "@example.com"),
                  tuple("some-idp", "ThisIsOkay@example.com"));
          assertThat(model.getRequiredLink(IanaLinkRelations.SELF)).isEqualTo(
              Link.of("http://localhost/user-account/" + userId, IanaLinkRelations.SELF));
          assertThat(model.getRequiredLink("user-profile")).isEqualTo(
              Link.of("http://localhost/user/" + username + "/profile", "user-profile"));
        });
    // @formatter:on
    this.entityManager.flush();
  }

  @Test
  void userPrivateProfileBlocked_SomeOtherAuthenticatedUser() {

    String username = "testuser1";
    // @formatter:off
    User user = User.builder()
        .email(username + "@example.com")
        .username(username)
        .password("{noop}abcde")
        .emailVerifiedDate(Instant.now())
        .roles(Set.of(Role.STAFF))
        .build();
    this.entityManager.persist(user);
    this.entityManager.flush();
    this.entityManager.refresh(user);

    Long userId = user.getId();

    Long someOtherUserId = userId + 1L;

    client
        .mutateWith(jwtConfigurer().jwt(jwt -> jwt
            .claim("roles", Set.of(Role.STAFF.name()))
            .claim("scope", "openid profile email user")
            .claim("sub", someOtherUserId)))
        .get().uri("/user-account/" + userId)
        .exchange()
        .expectStatus().isForbidden();
    // @formatter:on
    this.entityManager.flush();
  }

  @Test
  void userPrivateProfileBlocked_SameUserMissingScope() {

    String username = "testuser1";
    // @formatter:off
    User user = User.builder()
        .email(username + "@example.com")
        .username(username)
        .password("{noop}abcde")
        .emailVerifiedDate(Instant.now())
        .roles(null) // Roles should default to [BASIC_USER]
        .build();
    this.entityManager.persist(user);
    this.entityManager.flush();
    this.entityManager.refresh(user);

    Long userId = user.getId();

    client
        .mutateWith(jwtConfigurer().jwt(jwt -> jwt
            .claim("roles", Set.of(Role.BASIC_USER.name()))
            .claim("scope", "openid profile email") // Scope does not have "user"!
            .claim("sub", userId)))
        .get().uri("/user-account/" + userId)
        .exchange()
        .expectStatus().isForbidden();
    // @formatter:on
    this.entityManager.flush();
  }

  @Test
  void userPrivateProfileBlocked_MissingRole() {

    String username = "testuser1";
    // @formatter:off
    User user = User.builder()
        .email(username + "@example.com")
        .username(username)
        .password("{noop}abcde")
        .emailVerifiedDate(Instant.now())
        .roles(null) // Roles should default to [BASIC_USER]
        .build();
    this.entityManager.persist(user);
    this.entityManager.flush();
    this.entityManager.refresh(user);

    Long userId = user.getId();

    client
        .mutateWith(jwtConfigurer().jwt(jwt -> jwt
            .claim("roles", Set.of("ANONYMOUS")) // Roles does not have "BASIC_USER"!
            .claim("scope", "openid profile email user")
            .claim("sub", userId)))
        .get().uri("/user-account/" + userId)
        .exchange()
        .expectStatus().isForbidden();
    // @formatter:on
    this.entityManager.flush();
  }

  @Test
  void userPrivateProfileBlocked_NotAuthenticated() {

    String username = "testuser1";
    // @formatter:off
    User user = User.builder()
        .email(username + "@example.com")
        .username(username)
        .password("{noop}abcde")
        .emailVerifiedDate(Instant.now())
        .roles(Set.of(Role.STAFF))
        .build();
    this.entityManager.persist(user);
    this.entityManager.flush();
    this.entityManager.refresh(user);

    client
        .get().uri("/user-account/" + user.getId())
        .exchange()
        .expectStatus().isUnauthorized();
    // @formatter:on
    this.entityManager.flush();
  }

  private static JwtWebTestClientConfigurer jwtConfigurer() {
    return new JwtWebTestClientConfigurer();
  }

  /**
   * Enables testing JWT Authentication when using WebTestClient on an MVC servlet application. This
   * specifically addresses the incompatibility when attempting to use
   * {@code WebTestClient.mutateWith(mockJwt())} on a Spring MVC application by substituting
   * {@code mockJwt() WebTestClientConfigurer} with this configurer, which is essentially an adapter
   * for the MockMVC equivalent {@code jwt() RequestPostProcessor}.
   *
   * @see <a href="https://docs.spring.io/spring-security/reference/reactive/test/web/oauth2.html#_mockjwt_webtestclientconfigurer">mockJwt() WebTestClientConfigurer</a>
   * @see <a href="https://docs.spring.io/spring-security/reference/servlet/test/mockmvc/oauth2.html#_jwt_requestpostprocessor">jwt() RequestPostProcessor</a>
   * @see <a href="https://github.com/spring-projects/spring-framework/issues/31298">spring-projects/spring-framework#31298</a>
   */
  private static class JwtWebTestClientConfigurer implements WebTestClientConfigurer {

    private final JwtRequestPostProcessor jwtRequestPostProcessor = SecurityMockMvcRequestPostProcessors.jwt()
        .authorities(DefaultSecurityConfig.getDelegatingJwtGrantedAuthoritiesConverter());

    public JwtWebTestClientConfigurer jwt(Consumer<Jwt.Builder> jwtBuilderConsumer) {

      this.jwtRequestPostProcessor.jwt(jwtBuilderConsumer);
      return this;
    }

    @Override
    public void afterConfigurerAdded(@NonNull WebTestClient.Builder builder,
        WebHttpHandlerBuilder httpHandlerBuilder, ClientHttpConnector connector) {

      if (connector instanceof MockMvcHttpConnector mockMvcConnector) {
        builder.clientConnector(mockMvcConnector.with(List.of(this.jwtRequestPostProcessor)));
      }
    }
  }

  @Value
  private static class UserProfileResponse implements UserProfile {

    Long id;
    Instant createdDate;
    String username;
    String profileImageUrl;
  }

  @Value
  private static class UserPrivateProfileResponse implements UserPrivateProfile {

    Long id;
    Instant createdDate;
    Instant lastModifiedDate;
    String email;
    String username;
    Instant emailVerifiedDate;
    String profileImageUrl;
    Set<Role> roles;
    List<SocialAccountResponse> socialAccounts;

    @Override
    public List<UserPrivateProfile.SocialAccount> getSocialAccounts() {
      return this.socialAccounts.stream()
          .map(UserPrivateProfile.SocialAccount.class::cast)
          .toList();
    }

    @Value
    static class SocialAccountResponse implements UserPrivateProfile.SocialAccount {

      Long id;
      String providerId;
      Instant createdDate;
      String email;
    }
  }
}
