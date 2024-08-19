package com.mercadodavidv.hauntedreviews.review;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Embeddable
@Getter
@Setter
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
class ReviewKey {

  @Column(name = "place_id")
  private Long placeId;

  @Column(name = "user_id")
  private Long userId;

}
