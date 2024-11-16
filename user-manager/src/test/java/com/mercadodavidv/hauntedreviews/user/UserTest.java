package com.mercadodavidv.hauntedreviews.user;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.mercadodavidv.hauntedreviews.auth.UserIdentityCreateInput;
import jakarta.persistence.EntityManager;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.platform.commons.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest(properties = {"spring.profiles.include=test"})
@Transactional(propagation = Propagation.NOT_SUPPORTED)
class UserTest {

  @Autowired
  private Validator validator;

  @Autowired
  private EntityManager entityManager;

  @Test
  void testEmailValidation_Valid() {

    // @formatter:off
    List<String> validEmails = Arrays.asList(
        "username@example.com",
        "user@domain.co",
        "user@domain.org",
        "user@a.abc",
        "a@a.a.bc"
    );
    // @formatter:on
    validEmails.forEach(email -> {
      UserIdentityCreateInput userInput = new UserIdentityCreateInput(email, "user1", null, "",
          null, false);
      User user = UserMapper.toUser(userInput);

      Set<ConstraintViolation<User>> constraintViolations = validator.validate(user);

      System.out.println("Asserting email is valid: '" + email + "'");
      assertEquals(0, constraintViolations.size());
    });
  }

  @Test
  void testEmailValidation_Invalid() {

    // @formatter:off
    List<String> invalidEmails = Arrays.asList(
        "not an email",
        "atSign.com",
        "@atSign.org",
        "user@a.",
        "user@a.a bc",
        "us er@abc",
        "",
        " ",
        "___",
        null
    );
    // @formatter:on
    invalidEmails.forEach(email -> {
      UserIdentityCreateInput userInput = new UserIdentityCreateInput(email, "user1", null, "",
          null, false);
      User user = UserMapper.toUser(userInput);

      Set<ConstraintViolation<User>> constraintViolations = validator.validate(user);

      System.out.println("Asserting email NOT valid: '" + email + "'");
      assertThat(constraintViolations).isNotEmpty();
      String message = constraintViolations.iterator().next().getMessage();
      assertThat(message).containsIgnoringCase("email");
      if (StringUtils.isNotBlank(email)) {
        assertThat(message).doesNotContainIgnoringCase(email);
      }
    });
  }

  @Test
  void testUsernameValidation_Valid() {

    // @formatter:off
    List<String> validUsernames = Arrays.asList(
        "user1",
        "1234",
        "1234_",
        "abc123",
        "abc_123",
        "abc__123",
        "123abc",
        "123_abc",
        "123__abc",
        "a_b_c_1_2_3_",
        "ab12____",
        "12345678901234567890",
        null
    );
    // @formatter:on
    validUsernames.forEach(username -> {
      UserIdentityCreateInput userInput = new UserIdentityCreateInput("example@domain.com",
          username, null, "", null, false);
      User user = UserMapper.toUser(userInput);

      Set<ConstraintViolation<User>> constraintViolations = validator.validate(user);

      System.out.println("Asserting username is valid: '" + username + "'");
      assertEquals(0, constraintViolations.size());
    });
  }

  @Test
  void testUsernameValidation_Invalid() {

    // @formatter:off
    List<String> invalidUsernames = Arrays.asList(
        "123",
        "12",
        "1",
        "",
        " ",
        "    ",
        "     ",
        "abc 123",
        "abc123 ",
        " abc123 ",
        " abc123",
        "_abc123",
        "__abc123",
        "_123abc",
        "__123abc",
        "thIsTwenty1Characters"
    );
    // @formatter:on
    invalidUsernames.forEach(username -> {
      UserIdentityCreateInput userInput = new UserIdentityCreateInput("example@domain.com",
          username, null, "", null, false);
      User user = UserMapper.toUser(userInput);

      Set<ConstraintViolation<User>> constraintViolations = validator.validate(user);

      System.out.println("Asserting username NOT valid: '" + username + "'");
      assertThat(constraintViolations).isNotEmpty();
      String message = constraintViolations.iterator().next().getMessage();
      assertThat(message).containsIgnoringCase("username");
      if (StringUtils.isNotBlank(username)) {
        assertThat(message).doesNotContainIgnoringCase(username);
      }
    });
  }

  @Test
  void testEncodedPasswordValidation_Valid() {

    // @formatter:off
    List<String> validEncodedPasswords = Arrays.asList(
        "{bcrypt}$2a$10$iJEmo16umrbjQNmsDXDos.K27iSAhyMNaRjofP7WYyShFETHxm7ry", // "password"
        "{noop}password",
        "{noop}a",
        "{noop}abcdef{}",
        "{noop}abcdef{123}",
        "{noop}abcdef}",
        "{noop}abcdef{",
        null
    );
    // @formatter:on
    validEncodedPasswords.forEach(encodedPassword -> {
      UserIdentityCreateInput userInput = new UserIdentityCreateInput("example@domain.com",
          "user1", encodedPassword, "", null, false);
      User user = UserMapper.toUser(userInput);

      Set<ConstraintViolation<User>> constraintViolations = validator.validate(user);

      System.out.println("Asserting encoded password is valid: '" + encodedPassword + "'");
      assertEquals(0, constraintViolations.size());
    });
  }

  @Test
  void testEncodedPasswordValidation_Invalid() {

    // @formatter:off
    List<String> invalidEncodedPasswords = Arrays.asList(
        "passcode",
        "{passcode}",
        "{abcd}",
        "{noop}",
        "noop",
        "{bcrypt}",
        "bcrypt",
        "{}",
        " {noop}",
        " {bcrypt}",
        " {noop}passCode11!!",
        " {bcrypt}123"
    );
    // @formatter:on
    invalidEncodedPasswords.forEach(encodedPassword -> {
      UserIdentityCreateInput userInput = new UserIdentityCreateInput("example@domain.com",
          "user1", encodedPassword, "", null, false);
      User user = UserMapper.toUser(userInput);

      Set<ConstraintViolation<User>> constraintViolations = validator.validate(user);

      System.out.println("Asserting encoded password NOT valid: '" + encodedPassword + "'");
      assertThat(constraintViolations).isNotEmpty();
      String message = constraintViolations.iterator().next().getMessage();
      assertThat(message).containsIgnoringCase("password");
      if (StringUtils.isNotBlank(encodedPassword)) {
        assertThat(message).doesNotContainIgnoringCase(encodedPassword);
      }
    });
  }
}
