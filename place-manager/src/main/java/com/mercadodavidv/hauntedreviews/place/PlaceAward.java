package com.mercadodavidv.hauntedreviews.place;

import com.mercadodavidv.hauntedreviews.common.AuditMetadata;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
class PlaceAward {

  @Id
  @GeneratedValue
  private Long id;

  @Embedded
  private AuditMetadata auditMetadata = AuditMetadata.now();

  @Column(name = "place_id")
  private Long placeId;

  @Column(name = "user_id")
  private Long userId;

  private Long awardTypeId;

}
