package com.mercadodavidv.hauntedreviews.place;

import lombok.Getter;

@Getter
class PlaceNotFoundException extends RuntimeException {

  private final Long placeId;

  PlaceNotFoundException(Long placeId) {

    super("Could not find place with ID=" + placeId);
    this.placeId = placeId;
  }
}
