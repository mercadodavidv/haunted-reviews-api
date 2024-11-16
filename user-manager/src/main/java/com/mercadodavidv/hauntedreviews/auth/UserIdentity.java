package com.mercadodavidv.hauntedreviews.auth;

import com.mercadodavidv.hauntedreviews.user.Role;
import java.io.Serial;
import java.time.Instant;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.CollectionUtils;

@Getter
@AllArgsConstructor
@Builder
@EqualsAndHashCode
public class UserIdentity implements UserDetails {

  @Serial
  private static final long serialVersionUID = -5022287418809417889L;

  private final Long id;

  private final Instant createdDate;

  private final Instant lastModifiedDate;

  private final String email;

  private final String username;

  private final String password;

  private final Instant emailVerifiedDate;

  private final String profileImageUrl;

  private final Set<Role> roles;

  public boolean getEmailVerified() {
    return this.emailVerifiedDate != null && Instant.now().isAfter(this.emailVerifiedDate);
  }

  public Set<Role> getRoles() {
    return CollectionUtils.isEmpty(this.roles) ? Set.of(Role.BASIC_USER) : this.roles;
  }

  @Override
  public String getUsername() {
    return this.username;
  }

  @Override
  public String getPassword() {
    return this.password;
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return this.getRoles().stream().map(role -> new SimpleGrantedAuthority("ROLE_" + role.name()))
        .collect(Collectors.toUnmodifiableSet());
  }

  // TODO Implement email verification
  // TODO Implement account bans
  @Override
  public boolean isEnabled() {
    // return this.getEmailVerified();
    return true;
  }
}
