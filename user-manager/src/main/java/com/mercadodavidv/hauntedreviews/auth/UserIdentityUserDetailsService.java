package com.mercadodavidv.hauntedreviews.auth;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserIdentityUserDetailsService implements UserDetailsService {

  private final UserIdentityService userIdentityService;

  UserIdentityUserDetailsService(UserIdentityService userIdentityService) {
    this.userIdentityService = userIdentityService;
  }

  /**
   * Locates the user based on the email. The search is case-insensitive.
   *
   * @param email the email identifying the user.
   */
  @Override
  public UserDetails loadUserByUsername(String email) {
    return userIdentityService.findUserByEmail(email)
        .orElseThrow(() -> new UsernameNotFoundException(email));
  }
}
