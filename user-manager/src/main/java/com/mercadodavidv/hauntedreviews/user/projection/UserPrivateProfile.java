package com.mercadodavidv.hauntedreviews.user.projection;

import com.mercadodavidv.hauntedreviews.user.Role;
import com.mercadodavidv.hauntedreviews.user.UserModelBase;
import java.time.Instant;
import java.util.List;
import java.util.Set;

public interface UserPrivateProfile extends UserModelBase {

  Long getId();

  Instant getCreatedDate();

  Instant getLastModifiedDate();

  String getEmail();

  String getUsername();

  Instant getEmailVerifiedDate();

  String getProfileImageUrl();

  Set<Role> getRoles();

  List<UserPrivateProfile.SocialAccount> getSocialAccounts();

  interface SocialAccount {

    Long getId();

    String getProviderId();

    Instant getCreatedDate();

    String getEmail();

  }
}
