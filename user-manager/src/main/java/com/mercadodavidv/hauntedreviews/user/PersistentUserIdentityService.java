package com.mercadodavidv.hauntedreviews.user;

import com.mercadodavidv.hauntedreviews.auth.UserIdentity;
import com.mercadodavidv.hauntedreviews.auth.UserIdentityCreateInput;
import com.mercadodavidv.hauntedreviews.auth.UserIdentityService;
import com.mercadodavidv.hauntedreviews.auth.federation.SocialAccountCreateInput;
import java.util.NoSuchElementException;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * User service for returning private user information to facilitate authentication/authorization.
 */
@Service
@Transactional
class PersistentUserIdentityService implements UserIdentityService {

  private final UserRepository userRepository;

  private final SocialAccountRepository socialAccountRepository;

  PersistentUserIdentityService(UserRepository userRepository,
      SocialAccountRepository socialAccountRepository) {

    this.userRepository = userRepository;
    this.socialAccountRepository = socialAccountRepository;
  }

  @Override
  public void saveSocialAccount(SocialAccountCreateInput socialAccountCreateInput) {

    Long userId = socialAccountCreateInput.getUserId();
    User user = userRepository.findById(userId).orElseThrow(() -> new NoSuchElementException(
        "Failed to save social account. The user with ID=" + userId + " does not exist."));
    SocialAccount socialAccount = new SocialAccount(socialAccountCreateInput.getPrincipalName(),
        socialAccountCreateInput.getProviderId(), socialAccountCreateInput.getEmail(), user);
    socialAccountRepository.save(socialAccount);
  }

  @Override
  public UserIdentity saveUser(UserIdentityCreateInput userInput) {

    User user = UserMapper.toUser(userInput);
    return UserMapper.toUserIdentity(userRepository.save(user));
  }

  @Override
  public Optional<UserIdentity> findUserByEmail(String email) {

    return userRepository.findByEmailIgnoreCaseAndEmailNotNull(email)
        .map(UserMapper::toUserIdentity);
  }

  @Override
  public UserIdentity getUserBySocialAccountKey(String principalName, String providerId) {

    // @formatter:off
    return socialAccountRepository
        .findByPrincipalNameAndProviderId(principalName, providerId)
        .map(socialAccount -> UserMapper.toUserIdentity(socialAccount.getUser()))
        .orElseThrow(() -> new SocialAccountNotFoundException(principalName, providerId));
    // @formatter:on
  }

  @Override
  public boolean socialAccountExists(String principalName, String providerId) {
    return socialAccountRepository.existsByPrincipalNameAndProviderId(principalName, providerId);
  }
}
