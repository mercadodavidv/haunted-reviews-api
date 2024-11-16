package com.mercadodavidv.hauntedreviews.user;

import com.mercadodavidv.hauntedreviews.user.validation.EncodedPassword;
import com.mercadodavidv.hauntedreviews.user.validation.Username;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import java.time.Instant;
import java.util.List;
import java.util.Set;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.util.CollectionUtils;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "_USER")
@Getter
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(access = AccessLevel.PACKAGE)
class User {

  @Id
  @GeneratedValue
  private Long id;

  @CreatedDate
  private Instant createdDate;

  @LastModifiedDate
  private Instant lastModifiedDate;

  @Column(nullable = false)
  @NotBlank(message = "{validator.email.notblank.message}")
  @Email(message = "{validator.email.pattern.message}")
  @Setter
  // This does NOT need to match the email of any Social Account.
  private String email;

  @Username
  private String username;

  @EncodedPassword
  private String password;

  @Setter
  private Instant emailVerifiedDate;

  @Setter
  private String profileImageUrl;

  @Enumerated(EnumType.STRING)
  @ElementCollection(fetch = FetchType.EAGER)
  @Setter
  private Set<Role> roles;

  @OneToMany(mappedBy = "user", fetch = FetchType.EAGER)
  private List<SocialAccount> socialAccounts;

  @Setter
  private Instant usernameLastModifiedDate;

  public Set<Role> getRoles() {
    return CollectionUtils.isEmpty(this.roles) ? Set.of(Role.BASIC_USER) : this.roles;
  }

  public List<SocialAccount> getSocialAccounts() {
    return CollectionUtils.isEmpty(this.socialAccounts) ? List.of() : this.socialAccounts;
  }

  public Instant getUsernameLastModifiedDate() {
    return ObjectUtils.firstNonNull(this.usernameLastModifiedDate, this.createdDate, Instant.MIN);
  }
}
