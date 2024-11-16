package com.mercadodavidv.hauntedreviews.auth;

import static org.assertj.core.api.Assertions.assertThat;

import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebResponse;
import com.gargoylesoftware.htmlunit.html.HtmlButton;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlInput;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import java.io.IOException;
import org.assertj.core.api.InstanceOfAssertFactories;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponentsBuilder;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = {
    "spring.profiles.include=test"})
@AutoConfigureMockMvc
@Transactional
class AuthorizationServerTests {

  private static final String REDIRECT_URI = "http://127.0.0.1:3001/login/oauth2/code/haunted-reviews";

  // @formatter:off
  private static final String AUTHORIZATION_REQUEST = UriComponentsBuilder
      .fromPath("/oauth2/authorize")
      .queryParam("response_type", "code")
      .queryParam("client_id", "review-site-frontend")
      .queryParam("scope", "openid")
      .queryParam("state", "some-state")
      .queryParam("redirect_uri", REDIRECT_URI)
      .toUriString();
  // @formatter:on

  @Autowired
  private WebClient webClient;

  @Autowired
  private UserIdentityService userIdentityService;

  @BeforeEach
  public void setUp() {

    this.webClient.getOptions().setThrowExceptionOnFailingStatusCode(true);
    this.webClient.getOptions().setRedirectEnabled(true);
    this.webClient.getCookieManager().clearCookies(); // Log out before each test.
  }

  @Test
  void whenLoginSuccessfulThenDisplayBadRequestError() throws IOException {

    String username = "testuser1";
    String password = "password";
    UserIdentityCreateInput newUserInput = new UserIdentityCreateInput(username + "@example.com",
        username, encodePassword(password), null, null, true);
    userIdentityService.saveUser(newUserInput);

    HtmlPage page = this.webClient.getPage("/");

    assertLoginPage(page);

    this.webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
    WebResponse signInResponse = signIn(page, username + "@example.com", password).getWebResponse();

    // There is no index page.
    assertThat(signInResponse.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN.value());
  }

  @Test
  void whenLoginFailsThenDisplayBadCredentials_UserDoesNotExist() throws IOException {

    HtmlPage page = this.webClient.getPage("/");

    HtmlPage loginErrorPage = signIn(page, "user1", "anything");

    HtmlElement alert = loginErrorPage.querySelector("div[role=\"alert\"]");
    assertThat(alert).isNotNull();
    assertThat(alert.asNormalizedText()).isEqualTo("Invalid email or password.");
  }

  @Test
  void whenLoginFailsThenDisplayBadCredentials_WrongPassword() throws IOException {

    String username = "user2";
    String password = "abc123doremi";
    UserIdentityCreateInput newUserInput = new UserIdentityCreateInput(username + "@example.com",
        username, encodePassword(password), null, null, true);
    userIdentityService.saveUser(newUserInput);

    HtmlPage page = this.webClient.getPage("/");

    HtmlPage loginErrorPage = signIn(page, username + "@example.com", "wrong-password!!");

    HtmlElement alert = loginErrorPage.querySelector("div[role=\"alert\"]");
    assertThat(alert).isNotNull();
    assertThat(alert.asNormalizedText()).isEqualTo("Invalid email or password.");
  }

  @Test
  void whenNotLoggedInAndRequestingTokenThenRedirectToLogin() throws IOException {

    HtmlPage page = this.webClient.getPage(AUTHORIZATION_REQUEST);

    assertLoginPage(page);
  }

  @Test
  void whenNotLoggedInAndRequestingTokenThenRedirectToLoginThenLoginThenRedirectToClientApplication()
      throws IOException {

    // Create user
    String username = "testuser1";
    String password = "1234";
    UserIdentityCreateInput newUserInput = new UserIdentityCreateInput(username + "@example.com",
        username, encodePassword(password), null, null, true);
    userIdentityService.saveUser(newUserInput);

    HtmlPage page = this.webClient.getPage(AUTHORIZATION_REQUEST);

    assertLoginPage(page);

    // Log in, then redirect to "/oauth2/authorize"
    this.webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
    this.webClient.getOptions().setRedirectEnabled(false);
    WebResponse signInResponse = signIn(page, username + "@example.com", password).getWebResponse();

    assertThat(signInResponse.getStatusCode()).isEqualTo(HttpStatus.MOVED_PERMANENTLY.value());
    String location = signInResponse.getResponseHeaderValue("location");
    assertThat(UriComponentsBuilder.fromUriString(location).build().getQueryParams()).containsKey(
        "continue");

    // Follow redirect and redirect again to the client application
    WebResponse followRedirectResponse = this.webClient.getPage(location).getWebResponse();

    assertThat(followRedirectResponse.getStatusCode()).isEqualTo(
        HttpStatus.MOVED_PERMANENTLY.value());
    String finalLocation = followRedirectResponse.getResponseHeaderValue("location");
    assertThat(finalLocation).startsWith(REDIRECT_URI);
    MultiValueMap<String, String> queryParams = UriComponentsBuilder.fromUriString(finalLocation)
        .build().getQueryParams();
    assertThat(queryParams).containsKeys("code", "state");
    assertThat(queryParams).extractingByKey("state").asInstanceOf(InstanceOfAssertFactories.LIST)
        .contains("some-state");
  }

  @Test
  void whenLoggedInAndRequestingTokenThenRedirectToClientApplication() throws IOException {

    // Create user
    String username = "testuser1";
    String password = "1234";
    UserIdentityCreateInput newUserInput = new UserIdentityCreateInput(username + "@example.com",
        username, encodePassword(password), null, null, true);
    userIdentityService.saveUser(newUserInput);

    // Log in
    this.webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
    this.webClient.getOptions().setRedirectEnabled(false);
    signIn(this.webClient.getPage("/login"), username + "@example.com", password);

    // Request token
    WebResponse response = this.webClient.getPage(AUTHORIZATION_REQUEST).getWebResponse();

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.MOVED_PERMANENTLY.value());
    String location = response.getResponseHeaderValue("location");
    assertThat(location).startsWith(REDIRECT_URI).contains("code=");
  }

  private static <P extends Page> P signIn(HtmlPage page, String username, String password)
      throws IOException {

    HtmlInput usernameInput = page.querySelector("input[name=\"username\"]");
    HtmlInput passwordInput = page.querySelector("input[name=\"password\"]");
    HtmlButton signInButton = page.querySelector("button");

    usernameInput.type(username);
    passwordInput.type(password);
    return signInButton.click();
  }

  private static void assertLoginPage(HtmlPage page) {

    assertThat(page.getUrl().toString()).endsWith("/login");

    HtmlInput usernameInput = page.querySelector("input[name=\"username\"]");
    HtmlInput passwordInput = page.querySelector("input[name=\"password\"]");
    HtmlButton signInButton = page.querySelector("button");

    assertThat(usernameInput).isNotNull();
    assertThat(passwordInput).isNotNull();
    assertThat(signInButton.getTextContent()).isEqualTo("Sign in");
  }

  private static String encodePassword(String plaintext) {
    PasswordEncoder passwordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
    return passwordEncoder.encode(plaintext);
  }
}
