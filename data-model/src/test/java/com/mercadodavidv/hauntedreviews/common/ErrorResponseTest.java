package com.mercadodavidv.hauntedreviews.common;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import org.junit.jupiter.api.Test;

class ErrorResponseTest {

  @Test
  void testErrorResponse_Constructor() {

    String error = "Not Found";
    String message = "The requested resource was not found.";

    ErrorResponse response = new ErrorResponse(error, message);

    assertEquals(error, response.getError());
    assertEquals(message, response.getMessage());
    assertNotNull(response.getTimestamp());
    assertThat(response.getTimestamp()).isCloseTo(Instant.now(), within(59, ChronoUnit.MINUTES));
  }

  @Test
  void testErrorResponse_EqualsAndHashCode() {

    String error1a = "Error1";
    String message1a = "Message1";
    ErrorResponse response1a = new ErrorResponse(error1a, message1a);

    String error1b = "Error1";
    String message1b = "Message1";
    ErrorResponse response1b = new ErrorResponse(error1b, message1b);

    String error2 = "Error2";
    String message2 = "Message2";
    ErrorResponse response2 = new ErrorResponse(error2, message2);

    assertEquals(response1a, response1b); // Same properties
    assertNotEquals(response1a, response2); // Different properties
    assertEquals(response1a.hashCode(), response1b.hashCode()); // Same properties
    assertNotEquals(response1a.hashCode(), response2.hashCode()); // Different properties
  }
}
