package com.mercadodavidv.hauntedreviews.user;

import com.mercadodavidv.hauntedreviews.auth.UserIdentity;
import com.mercadodavidv.hauntedreviews.auth.UserIdentityCreateInput;
import java.time.Instant;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
final class UserMapper {

  static User toUser(UserIdentityCreateInput userIdentityCreateInput) {

    // @formatter:off
    return User.builder()
        .email(userIdentityCreateInput.getEmail())
        .username(userIdentityCreateInput.getUsername())
        .password(userIdentityCreateInput.getPassword())
        .profileImageUrl(userIdentityCreateInput.getProfileImageUrl())
        .roles(userIdentityCreateInput.getRoles())
        .emailVerifiedDate(userIdentityCreateInput.isEmailVerified() ? Instant.now() : null)
        .build();
    // @formatter:on
  }

  static UserIdentity toUserIdentity(User user) {

    // @formatter:off
    return UserIdentity.builder()
        .id(user.getId())
        .createdDate(user.getCreatedDate())
        .lastModifiedDate(user.getLastModifiedDate())
        .email(user.getEmail())
        .username(user.getUsername())
        .password(user.getPassword())
        .emailVerifiedDate(user.getEmailVerifiedDate())
        .profileImageUrl(user.getProfileImageUrl())
        .roles(user.getRoles())
        .build();
    // @formatter:on
  }
}
