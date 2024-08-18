package com.mercadodavidv.hauntedreviews.common;

import java.time.Instant;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.springframework.hateoas.RepresentationModel;

@Getter
@EqualsAndHashCode(callSuper = true)
public class ErrorResponse extends RepresentationModel<ErrorResponse> {

  // Unique identifier of the type of error, in english. e.g. "New Review is invalid".
  private final String error;

  // Further elaboration in english. Not machine-readable. e.g. "... with ID=1", or "X is required".
  private final String message;

  private final Instant timestamp;

  public ErrorResponse(String error, String message) {

    this.error = error;
    this.message = message;
    this.timestamp = Instant.now();
  }
}
