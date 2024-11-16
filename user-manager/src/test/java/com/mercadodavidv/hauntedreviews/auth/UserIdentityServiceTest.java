package com.mercadodavidv.hauntedreviews.auth;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.mercadodavidv.hauntedreviews.auth.federation.SocialAccountCreateInput;
import com.mercadodavidv.hauntedreviews.testutil.JpaRepositoryUtils;
import com.mercadodavidv.hauntedreviews.user.Role;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest(properties = {"spring.profiles.include=test"})
@Transactional
class UserIdentityServiceTest {

  private static final String USERNAME_1 = "user1";
  private static final String USERNAME_1_CI = "uSEr1";
  private static final String USERNAME_2 = "user2";
  private static final String PASSWORD_1 = "{noop}abcde";
  private static final String EMAIL_A = "abc@a.bc";
  private static final String EMAIL_A_CI = "aBC@a.bc";
  private static final String EMAIL_X = "xyz@x.yz";

  @Autowired
  private UserIdentityService userIdentityService;

  @Autowired
  private JpaRepositoryUtils jpaRepositoryUtils;

  @Test
  void whenSavingNewUserThenUserIsSearchableByEmail() {

    // Save user1/abc@a.bc
    UserIdentity userIdentityFromSave = userIdentityService.saveUser(
        new UserIdentityCreateInput(EMAIL_A, USERNAME_1, PASSWORD_1, "https://example.com/img.jpg",
            null, false));
    // Get user by email abc@a.bc
    Optional<UserIdentity> userIdentityFromGet = userIdentityService.findUserByEmail(EMAIL_A);
    assertThat(userIdentityFromGet).isPresent().get().isEqualTo(userIdentityFromSave);
    assertThat(userIdentityFromSave)
        .extracting("email", "username", "password", "profileImageUrl", "emailVerifiedDate")
        .containsExactly(EMAIL_A, USERNAME_1, PASSWORD_1, "https://example.com/img.jpg", null);
    assertThat(userIdentityFromSave.getAuthorities()).extracting(GrantedAuthority::getAuthority)
        .map(authority -> authority.replace("ROLE_", "")).isNotNull().doesNotContainNull()
        .containsOnly(Role.BASIC_USER.name());
  }

  @Test
  void whenSavingNewUserWithRolesThenUserRolesAreReturned() {

    // Save user1/abc@a.bc
    UserIdentity userIdentityFromSave = userIdentityService.saveUser(
        new UserIdentityCreateInput(EMAIL_A, USERNAME_1, PASSWORD_1, "",
            Set.of(Role.ADMIN, Role.STAFF), false));
    // Get user by email abc@a.bc
    Optional<UserIdentity> userIdentityFromGet = userIdentityService.findUserByEmail(EMAIL_A);
    assertThat(userIdentityFromGet).isPresent().get().isEqualTo(userIdentityFromSave);
    assertThat(userIdentityFromSave)
        .extracting("email", "username", "password", "profileImageUrl", "emailVerifiedDate")
        .containsExactly(EMAIL_A, USERNAME_1, PASSWORD_1, "", null);
    assertThat(userIdentityFromSave.getAuthorities()).extracting(GrantedAuthority::getAuthority)
        .map(authority -> authority.replace("ROLE_", "")).isNotNull().doesNotContainNull()
        .containsOnly(Role.ADMIN.name(), Role.STAFF.name());
  }

  @Test
  void whenSavingNewUserWithConflictingUsernameThenFail_SameCase() {

    // Save user1/abc@a.bc
    UserIdentity userIdentityFromSave1 = userIdentityService.saveUser(
        new UserIdentityCreateInput(EMAIL_A, USERNAME_1, PASSWORD_1, "",
            null, false));
    // Get user by email abc@a.bc
    Optional<UserIdentity> userIdentityFromGet1 = userIdentityService.findUserByEmail(EMAIL_A);
    assertThat(userIdentityFromGet1).isPresent().get().isEqualTo(userIdentityFromSave1);

    // Try to save user1/xyz@x.yz and fail
    userIdentityService.saveUser(new UserIdentityCreateInput(EMAIL_X, USERNAME_1, PASSWORD_1, "",
        null, false));
    assertThatThrownBy(() -> jpaRepositoryUtils.flush())
        .isInstanceOf(DataIntegrityViolationException.class)
        .hasMessageContainingAll("IDX_LOWER_USERNAME", "Unique"); // Depends on index name in DB
    // Try to get user by email xyz@x.yz and it does not exist
    jpaRepositoryUtils.clear();
    Optional<UserIdentity> userIdentityFromGet2 = userIdentityService.findUserByEmail(EMAIL_X);
    assertThat(userIdentityFromGet2).isEmpty();
    jpaRepositoryUtils.flush();
  }

  @Test
  void whenSavingNewUserWithConflictingUsernameThenFail_CaseInsensitive() {

    // Save user1/abc@a.bc
    UserIdentity userIdentityFromSave1 = userIdentityService.saveUser(
        new UserIdentityCreateInput(EMAIL_A, USERNAME_1, PASSWORD_1, "",
            null, false));
    // Get user by email abc@a.bc
    Optional<UserIdentity> userIdentityFromGet1 = userIdentityService.findUserByEmail(EMAIL_A);
    assertThat(userIdentityFromGet1).isPresent().get().isEqualTo(userIdentityFromSave1);

    // Try to save uSeR1/xyz@x.yz and fail
    userIdentityService.saveUser(new UserIdentityCreateInput(EMAIL_X, USERNAME_1_CI, PASSWORD_1, "",
        null, false));
    assertThatThrownBy(() -> jpaRepositoryUtils.flush())
        .isInstanceOf(DataIntegrityViolationException.class)
        .hasMessageContainingAll("IDX_LOWER_USERNAME", "Unique"); // Depends on index name in DB
    // Try to get user by email xyz@x.yz and it does not exist
    jpaRepositoryUtils.clear();
    Optional<UserIdentity> userIdentityFromGet2 = userIdentityService.findUserByEmail(EMAIL_X);
    assertThat(userIdentityFromGet2).isEmpty();
    jpaRepositoryUtils.flush();
  }

  @Test
  void whenSavingNewUserWithConflictingEmailThenFail_SameCase() {

    // Save user1/abc@a.bc
    UserIdentity userIdentityFromSave1 = userIdentityService.saveUser(
        new UserIdentityCreateInput(EMAIL_A, USERNAME_1, PASSWORD_1, "",
            null, false));
    // Get user by email abc@a.bc
    Optional<UserIdentity> userIdentityFromGet1 = userIdentityService.findUserByEmail(EMAIL_A);
    assertThat(userIdentityFromGet1).isPresent().get().isEqualTo(userIdentityFromSave1);

    // Try to save user2/aBC@a.bc and fail
    userIdentityService.saveUser(new UserIdentityCreateInput(EMAIL_A, USERNAME_2, PASSWORD_1, "",
        null, false));
    assertThatThrownBy(() -> jpaRepositoryUtils.flush())
        .isInstanceOf(DataIntegrityViolationException.class)
        .hasMessageContainingAll("IDX_LOWER_EMAIL", "Unique"); // Depends on index name in DB
    jpaRepositoryUtils.clear();
    jpaRepositoryUtils.flush();
  }

  @Test
  void whenSavingNewUserWithConflictingEmailThenFail_CaseInsensitive() {

    // Save user1/abc@a.bc
    UserIdentity userIdentityFromSave1 = userIdentityService.saveUser(
        new UserIdentityCreateInput(EMAIL_A, USERNAME_1, PASSWORD_1, "",
            null, false));
    // Get user by email abc@a.bc or aBC@a.bc
    Optional<UserIdentity> userIdentityFromGet1 = userIdentityService.findUserByEmail(EMAIL_A);
    Optional<UserIdentity> userIdentityFromGet1CI = userIdentityService.findUserByEmail(EMAIL_A_CI);
    assertThat(userIdentityFromGet1).isPresent().get().isEqualTo(userIdentityFromSave1);
    assertThat(userIdentityFromGet1CI).isPresent().get().isEqualTo(userIdentityFromSave1);

    // Try to save user2/aBC@a.bc and fail
    userIdentityService.saveUser(new UserIdentityCreateInput(EMAIL_A_CI, USERNAME_2, PASSWORD_1, "",
        null, false));
    assertThatThrownBy(() -> jpaRepositoryUtils.flush())
        .isInstanceOf(DataIntegrityViolationException.class)
        .hasMessageContainingAll("IDX_LOWER_EMAIL", "Unique"); // Depends on index name in DB
    jpaRepositoryUtils.clear();
    jpaRepositoryUtils.flush();
  }

  @Test
  void whenSavingNewUserAndSocialAccountsThenSocialAccountsAreLinkedToUser() {

    // Save user1/abc@a.bc (null password)
    UserIdentity userIdentityFromSave = userIdentityService.saveUser(
        new UserIdentityCreateInput(EMAIL_A, USERNAME_1, PASSWORD_1, "", null, false));
    Long userId = userIdentityFromSave.getId();
    // Save socialAccount1/abc@a.bc
    userIdentityService.saveSocialAccount(
        new SocialAccountCreateInput("1234", "provider1-idp", EMAIL_A, userId));
    // Save socialAccount2/abc@a.bc
    userIdentityService.saveSocialAccount(
        new SocialAccountCreateInput("5678", "provider2-idp", EMAIL_A, userId));
    // Save socialAccount3/xyz@x.yz
    userIdentityService.saveSocialAccount(
        new SocialAccountCreateInput("9012", "provider3-idp", EMAIL_X, userId));

    // Get user by socialAccount1
    assertThat(userIdentityService.socialAccountExists("1234", "provider1-idp")).isTrue();
    UserIdentity userIdentityFromGet1 = userIdentityService.getUserBySocialAccountKey("1234",
        "provider1-idp");
    assertThat(userIdentityFromGet1).isEqualTo(userIdentityFromSave);
    // Get user by socialAccount2
    assertThat(userIdentityService.socialAccountExists("5678", "provider2-idp")).isTrue();
    UserIdentity userIdentityFromGet2 = userIdentityService.getUserBySocialAccountKey("5678",
        "provider2-idp");
    assertThat(userIdentityFromGet2).isEqualTo(userIdentityFromSave);
    // Get user by socialAccount3
    assertThat(userIdentityService.socialAccountExists("9012", "provider3-idp")).isTrue();
    UserIdentity userIdentityFromGet3 = userIdentityService.getUserBySocialAccountKey("9012",
        "provider3-idp");
    assertThat(userIdentityFromGet3).isEqualTo(userIdentityFromSave);
    // Try to get user by email xyz@x.yz and it does not exist
    assertThat(userIdentityService.findUserByEmail(EMAIL_X)).isEmpty();
  }

  @Test
  void whenSavingNewSocialAccountWithoutUserThenFail() {

    // Try to save socialAccount1/abc@a.bc without a user and fail
    SocialAccountCreateInput input = new SocialAccountCreateInput("1234", "provider1-idp", EMAIL_A,
        1L);
    assertThatThrownBy(() -> userIdentityService.saveSocialAccount(input))
        .isInstanceOf(NoSuchElementException.class)
        .hasMessageContainingAll("Failed to save social account",
            "The user with ID=1 does not exist");

    // Try to get user by socialAccount1 and fail
    assertThat(userIdentityService.socialAccountExists("1234", "provider1-idp")).isFalse();
    assertThatThrownBy(() -> {
      userIdentityService.getUserBySocialAccountKey("1234", "provider1-idp");
    })
        .isInstanceOf(RuntimeException.class) // SocialAccountNotFoundException
        .hasMessageContaining(
            "Could not find social account with provider ID=provider1-idp and principal name=1234");
    // Try to get user by email abc@a.bc and it does not exist
    assertThat(userIdentityService.findUserByEmail(EMAIL_A)).isEmpty();
  }
}
