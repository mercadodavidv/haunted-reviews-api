package com.mercadodavidv.hauntedreviews.review;

import lombok.Getter;

@Getter
class InvalidReviewException extends RuntimeException {

  private final Long placeId;

  private final Long userId;

  InvalidReviewException(Long placeId, Long userId, String details) {

    super("Invalid review submission for place ID=" + placeId + " and user ID=" + userId + ". "
        + details);
    this.placeId = placeId;
    this.userId = userId;
  }
}
