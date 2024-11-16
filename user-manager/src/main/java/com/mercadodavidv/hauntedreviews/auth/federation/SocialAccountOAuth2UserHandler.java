package com.mercadodavidv.hauntedreviews.auth.federation;

import com.mercadodavidv.hauntedreviews.auth.UserIdentity;
import com.mercadodavidv.hauntedreviews.auth.UserIdentityCreateInput;
import com.mercadodavidv.hauntedreviews.auth.UserIdentityService;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.logging.Logger;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;

/**
 * Creates a new {@code SocialAccount} whenever a user logs in for the first time using a social
 * login provider. A {@code User} is also created/linked automatically by matching the email of the
 * social account.
 */
@Component
public final class SocialAccountOAuth2UserHandler implements BiConsumer<OAuth2User, String> {

  Logger logger = Logger.getLogger(getClass().getName());

  private final UserIdentityService userIdentityService;

  SocialAccountOAuth2UserHandler(UserIdentityService userIdentityService) {
    this.userIdentityService = userIdentityService;
  }

  // Find or create a User when a user logs in with a third party provider.
  @Override
  public void accept(OAuth2User oAuth2User, String providerId) {

    String principalName = oAuth2User.getName();
    // If this Social Account already exists by principalName (user-name-attribute) and providerId do nothing.
    if (!userIdentityService.socialAccountExists(principalName, providerId)) {
      // Need to create a new Social Account (and possibly new User) for this user.
      String socialAccountEmail = oAuth2User.getAttribute("email");
      if (StringUtils.isBlank(socialAccountEmail)) {
        throw new SocialAccountLinkingException(
            "An error occurred while attempting to retrieve the user's email from the authentication provider.",
            providerId);
      }

      Optional<UserIdentity> existingUser = userIdentityService.findUserByEmail(socialAccountEmail);
      if (existingUser.isPresent()) {
        // If User exists (email), create a new Social Account linked to the User ID.
        logger.info(() -> "Saving new social account linked to existing user: principal_name="
            + oAuth2User.getName() + ", provider_id=" + providerId + ", username="
            + existingUser.get().getUsername() + ".");
        SocialAccountCreateInput newSocialAccount = new SocialAccountCreateInput(principalName,
            providerId, socialAccountEmail, existingUser.get().getId());
        userIdentityService.saveSocialAccount(newSocialAccount);
      } else {
        // If User does not exist, create a new Social Account AND a new User.
        logger.info(() -> "Saving new social account linked to first-time user: principal_name="
            + oAuth2User.getName() + ", provider_id=" + providerId + ".");
        UserIdentityCreateInput newUserInput = new UserIdentityCreateInput(socialAccountEmail, null,
            null, null, null, Boolean.TRUE.equals(oAuth2User.getAttribute("email_verified")));
        UserIdentity newUser = userIdentityService.saveUser(newUserInput);

        SocialAccountCreateInput newSocialAccount = new SocialAccountCreateInput(principalName,
            providerId, socialAccountEmail, newUser.getId());
        userIdentityService.saveSocialAccount(newSocialAccount);
      }
    }
  }
}
