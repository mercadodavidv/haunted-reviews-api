package com.mercadodavidv.hauntedreviews.common;

import jakarta.persistence.Embeddable;
import java.time.Instant;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class AuditMetadata {

  @CreatedDate
  private Instant createdDate;

  @LastModifiedDate
  private Instant lastModifiedDate;

  public static AuditMetadata now() {
    return new AuditMetadata(Instant.now(), Instant.now());
  }

  //@CreatedBy
  //User createdBy

  //@LastModifiedBy
  //User lastModifiedBy

}
