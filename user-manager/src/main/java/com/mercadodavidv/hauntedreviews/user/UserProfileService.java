package com.mercadodavidv.hauntedreviews.user;

import com.mercadodavidv.hauntedreviews.user.projection.UserPrivateProfile;
import com.mercadodavidv.hauntedreviews.user.projection.UserProfile;
import com.mercadodavidv.hauntedreviews.user.validation.UserPassword;
import org.springframework.lang.NonNull;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

/**
 * User service for returning public user profile information.
 */
@Service
@Transactional
@Validated
public class UserProfileService {

  private final UserRepository userRepository;

  UserProfileService(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  public UserProfile getUserProfile(@NonNull String username) {
    return userRepository.findByUsernameIgnoreCase(username, UserProfile.class)
        .orElseThrow(() -> new UserProfileNotFoundException(username));
  }

  @PreAuthorize("T(String).valueOf(#userId) == authentication.name")
  public UserPrivateProfile getUserPrivateProfile(@P("userId") Long userId) {
    return userRepository.findById(userId, UserPrivateProfile.class)
        .orElseThrow(() -> new UserPrivateProfileNotFoundException(userId));
  }

  public boolean isUsernameAvailable(String username) {
    return userRepository.countByUsernameIgnoreCase(username) == 0;
  }

  public boolean isEmailAvailable(String email) {
    return userRepository.countByEmailIgnoreCase(email) == 0;
  }

  public void validatePassword(@UserPassword String password) {
    // TODO Return a machine-readable collection of validation failures.
  }
}
