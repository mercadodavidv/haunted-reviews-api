package com.mercadodavidv.hauntedreviews.auth;

import com.mercadodavidv.hauntedreviews.user.Role;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserIdentityCreateInput {

  private final String email;

  private final String username;

  private final String password;

  private final String profileImageUrl;

  private final Set<Role> roles;

  private final boolean emailVerified;

}
