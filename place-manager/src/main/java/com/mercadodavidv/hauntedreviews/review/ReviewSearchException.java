package com.mercadodavidv.hauntedreviews.review;

import lombok.Getter;

@Getter
class ReviewSearchException extends RuntimeException {

  private final Long placeId;

  ReviewSearchException(Long placeId, String details) {

    super("Invalid review search query for place ID=" + placeId + ". " + details);
    this.placeId = placeId;
  }
}
