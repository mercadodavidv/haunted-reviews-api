package com.mercadodavidv.hauntedreviews.config;

import static org.springframework.boot.autoconfigure.security.servlet.PathRequest.toH2Console;
import static org.springframework.security.authorization.AuthorityAuthorizationManager.hasAuthority;
import static org.springframework.security.authorization.AuthorityAuthorizationManager.hasRole;
import static org.springframework.security.authorization.AuthorizationManagers.allOf;

import com.mercadodavidv.hauntedreviews.auth.federation.FederatedIdentityAuthenticationSuccessHandler;
import com.mercadodavidv.hauntedreviews.auth.federation.github.GithubOAuth2UserService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.authorization.AuthorityAuthorizationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer.FrameOptionsConfig;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.DelegatingOAuth2UserService;
import org.springframework.security.oauth2.server.resource.authentication.DelegatingJwtGrantedAuthoritiesConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.expression.WebExpressionAuthorizationManager;

@EnableWebSecurity
@EnableMethodSecurity
@Configuration(proxyBeanMethods = false)
public class DefaultSecurityConfig {

  private static final String LOGIN_PAGE = "/login";

  private final FederatedIdentityAuthenticationSuccessHandler successHandler;

  public DefaultSecurityConfig(FederatedIdentityAuthenticationSuccessHandler successHandler) {
    this.successHandler = successHandler;
  }

  @Bean
  @Order(Ordered.LOWEST_PRECEDENCE)
  SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http,
      @Autowired RoleHierarchy roleHierarchy) throws Exception {

    // @formatter:of
    http
        .authorizeHttpRequests(authorize -> authorize
            .requestMatchers(LOGIN_PAGE, "/assets/**").permitAll()
            .requestMatchers("/user/{username}/profile").permitAll()
            .requestMatchers("/validation/password").permitAll()
            .requestMatchers("/user-account/{userId}").access(allOf(
                hasAuthority("SCOPE_user"),
                hasRoleWithRoleHierarchy("BASIC_USER", roleHierarchy),
                new WebExpressionAuthorizationManager(
                    "T(String).valueOf(#userId) == authentication.name")
            ))
            .anyRequest().denyAll()
        )
        // Users may log in with their email and password
        .formLogin(formLogin -> formLogin
            .loginPage(LOGIN_PAGE)
        )
        // OAuth2 Login handles the redirect to the OAuth 2.0 Login endpoint
        // from the authorization server filter chain
        .oauth2Login(oauth2Login -> oauth2Login
            .loginPage(LOGIN_PAGE)
            .successHandler(successHandler)
            .userInfoEndpoint(userInfo -> userInfo
                .userService(new DelegatingOAuth2UserService<>(
                    List.of(new GithubOAuth2UserService(), new DefaultOAuth2UserService())))
            )
        )
        .oauth2ResourceServer(resourceServer -> resourceServer.jwt(Customizer.withDefaults()));
    // @formatter:on

    return http.build();
  }

  /**
   * SecurityFilterChain for H2 console.
   */
  @Bean
  @Order(1)
  @Profile("local & h2")
  SecurityFilterChain localH2SecurityFilterChain(HttpSecurity http) throws Exception {

    // @formatter:off
    http
        .securityMatcher(toH2Console())
        .csrf(csrf -> csrf.ignoringRequestMatchers(toH2Console()).disable())
        .headers(headers -> headers.frameOptions(FrameOptionsConfig::disable))
        .authorizeHttpRequests(authorize -> authorize.anyRequest().permitAll());
    // @formatter:on

    return http.build();
  }

  @Bean
  @Order(2)
  @Profile("local")
  SecurityFilterChain localActuatorSecurityFilterChain(HttpSecurity http) throws Exception {

    // @formatter:off
    http
        .securityMatcher("/actuator/**")
        .authorizeHttpRequests(authorize -> authorize.anyRequest().permitAll());
    // @formatter:on

    return http.build();
  }

  @Bean
  @Order(2)
  @Profile("!local")
  SecurityFilterChain defaultActuatorSecurityFilterChain(HttpSecurity http) throws Exception {

    // @formatter:off
    http
        .securityMatcher("/actuator/**")
        .authorizeHttpRequests(authorize -> authorize.anyRequest().hasRole("ADMIN"));
    // @formatter:on

    return http.build();
  }

  @Bean
  JwtAuthenticationConverter jwtAuthenticationConverter() {

    DelegatingJwtGrantedAuthoritiesConverter delegatingGrantedAuthoritiesConverter = getDelegatingJwtGrantedAuthoritiesConverter();
    JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
    jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(
        delegatingGrantedAuthoritiesConverter);
    return jwtAuthenticationConverter;
  }

  public static DelegatingJwtGrantedAuthoritiesConverter getDelegatingJwtGrantedAuthoritiesConverter() {

    JwtGrantedAuthoritiesConverter scopesGrantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();

    JwtGrantedAuthoritiesConverter rolesGrantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
    rolesGrantedAuthoritiesConverter.setAuthoritiesClaimName("roles");
    rolesGrantedAuthoritiesConverter.setAuthorityPrefix("ROLE_");

    return new DelegatingJwtGrantedAuthoritiesConverter(scopesGrantedAuthoritiesConverter,
        rolesGrantedAuthoritiesConverter);
  }

  private static <T> AuthorityAuthorizationManager<T> hasRoleWithRoleHierarchy(String role,
      RoleHierarchy roleHierarchy) {

    AuthorityAuthorizationManager<T> authorityAuthorizationManager = hasRole(role);
    authorityAuthorizationManager.setRoleHierarchy(roleHierarchy);
    return authorityAuthorizationManager;
  }
}
