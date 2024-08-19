package com.mercadodavidv.hauntedreviews.review;

import lombok.Getter;

@Getter
class ReviewNotFoundException extends RuntimeException {

  private final Long placeId;

  private final Long userId;

  ReviewNotFoundException(Long placeId, Long userId) {
    super("Could not find review with place ID=" + placeId + " and user ID=" + userId);
    this.placeId = placeId;
    this.userId = userId;
  }

  ReviewNotFoundException(ReviewKey reviewKey) {
    super("Could not find review with place ID=" + reviewKey.getPlaceId() + " and user ID="
        + reviewKey.getUserId());
    this.placeId = reviewKey.getPlaceId();
    this.userId = reviewKey.getUserId();
  }
}
