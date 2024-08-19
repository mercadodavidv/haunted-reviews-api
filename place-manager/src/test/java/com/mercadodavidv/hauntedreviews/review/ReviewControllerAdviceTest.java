package com.mercadodavidv.hauntedreviews.review;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.mercadodavidv.hauntedreviews.common.ErrorResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.hateoas.Link;

class ReviewControllerAdviceTest {

  private ReviewControllerAdvice reviewControllerAdvice;

  @BeforeEach
  void setup() {
    reviewControllerAdvice = new ReviewControllerAdvice();
  }

  @Test
  void testAdvice_InvalidReview() {

    Long placeId = 1L;
    Long userId = 1L;
    String details = "Place does not exist.";
    InvalidReviewException ex = new InvalidReviewException(placeId, userId, details);

    ErrorResponse response = reviewControllerAdvice.handleInvalidReview(ex);

    assertEquals("Invalid Review", response.getError());
    assertThat(response.getMessage()).contains(placeId.toString());
    assertThat(response.getLink("submit-review")).isPresent();
    assertThat(response.getLink("submit-review")).get().extracting(Link::getHref)
        .isEqualTo("/place/" + placeId + "/review/" + userId);
  }

  @Test
  void testAdvice_ReviewNotFound() {

    Long placeId = 29675L;
    Long userId = 1L;
    ReviewNotFoundException ex = new ReviewNotFoundException(new ReviewKey(placeId, userId));

    ErrorResponse response = reviewControllerAdvice.handleReviewNotFound(ex);

    assertEquals("Review Not Found", response.getError());
    assertThat(response.getMessage()).contains(placeId.toString());
    assertThat(response.getLink("review")).isPresent();
    assertThat(response.getLink("review")).get().extracting(Link::getHref)
        .isEqualTo("/place/" + placeId + "/review/" + userId);
  }

  @Test
  void testAdvice_InvalidReviewSearch() {

    Long placeId = 1L;
    String details = "Page size must not exceed 20.";
    ReviewSearchException ex = new ReviewSearchException(placeId, details);

    ErrorResponse response = reviewControllerAdvice.handleInvalidReviewSearch(ex);

    assertEquals("Invalid Review Search", response.getError());
    assertThat(response.getMessage()).contains(details);
    assertThat(response.getLink("place-reviews")).isPresent();
    assertThat(response.getLink("place-reviews")).get().extracting(Link::getHref)
        .isEqualTo("/place/" + placeId + "/reviews");
  }
}
