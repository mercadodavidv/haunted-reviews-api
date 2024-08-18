package com.mercadodavidv.hauntedreviews.place;

class PlaceSearchException extends RuntimeException {

  PlaceSearchException(String details) {
    super("Invalid place search query. " + details);
  }
}
