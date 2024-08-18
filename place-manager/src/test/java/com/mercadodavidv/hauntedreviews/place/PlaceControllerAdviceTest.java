package com.mercadodavidv.hauntedreviews.place;

import static com.mercadodavidv.hauntedreviews.place.PlaceController.PLACE_QUERY_MAX_PAGE_SIZE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import com.mercadodavidv.hauntedreviews.common.ErrorResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Pageable;

class PlaceControllerAdviceTest {

  private PlaceControllerAdvice placeControllerAdvice;

  @BeforeEach
  void setup() {
    placeControllerAdvice = new PlaceControllerAdvice();
  }

  @Test
  void testAdvice_PlaceNotFound() {

    Long placeId = 29675L;
    PlaceNotFoundException ex = new PlaceNotFoundException(placeId);

    ErrorResponse response = placeControllerAdvice.handlePlaceNotFound(ex);

    assertEquals("Place Not Found", response.getError());
    assertThat(response.getMessage()).contains(placeId.toString());
    assertThat(response.getLink("place")).isPresent();
    assertThat(response.getLink("place")).get()
        .isEqualTo(linkTo(methodOn(PlaceController.class).one(placeId)).withRel("place"));
  }

  @Test
  void testAdvice_InvalidPlaceSearch() {

    String details = "Page size must not exceed 20.";
    PlaceSearchException ex = new PlaceSearchException(details);

    ErrorResponse response = placeControllerAdvice.handleInvalidPlaceSearch(ex);

    assertEquals("Invalid Place Search", response.getError());
    assertThat(response.getMessage()).contains(details);
    assertThat(response.getLink("places")).isPresent();
    assertThat(response.getLink("places")).get().isEqualTo(linkTo(
        methodOn(PlaceController.class).all(Pageable.ofSize(PLACE_QUERY_MAX_PAGE_SIZE))).withRel(
        "places"));
  }
}
