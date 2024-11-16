package com.mercadodavidv.hauntedreviews.user;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import java.time.Instant;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.NaturalId;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"principal_name", "provider_id"}))
@Getter
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(access = AccessLevel.PACKAGE)
class SocialAccount {

  @Id
  @GeneratedValue
  private Long id;

  private String principalName;

  @NaturalId
  private String providerId;

  @CreatedDate
  private Instant createdDate;

  @LastModifiedDate
  private Instant lastModifiedDate;

  @Column(nullable = false)
  @NotBlank(message = "{validator.email.notblank.message}")
  @Email(message = "{validator.email.pattern.message}")
  private String email;

  @NaturalId
  @ManyToOne(optional = false)
  @JoinColumn(name = "user_id")
  private User user;

  SocialAccount(String principalName, String providerId, String email, User user) {
    this.principalName = principalName;
    this.providerId = providerId;
    this.email = email;
    this.user = user;
  }
}
