package com.mercadodavidv.hauntedreviews.place;

import lombok.Getter;

@Getter
class InvalidPlaceException extends RuntimeException {

  InvalidPlaceException(String details) {
    super("Invalid place submission. " + details);
  }
}
