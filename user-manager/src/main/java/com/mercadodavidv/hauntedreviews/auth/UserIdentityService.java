package com.mercadodavidv.hauntedreviews.auth;

import com.mercadodavidv.hauntedreviews.auth.federation.SocialAccountCreateInput;
import java.util.Optional;

public interface UserIdentityService {

  void saveSocialAccount(SocialAccountCreateInput socialAccountCreateInput);

  UserIdentity saveUser(UserIdentityCreateInput userInput);

  Optional<UserIdentity> findUserByEmail(String email);

  UserIdentity getUserBySocialAccountKey(String principalName, String providerId);

  boolean socialAccountExists(String principalName, String providerId);
}
