package com.mercadodavidv.hauntedreviews.place;

import static com.mercadodavidv.hauntedreviews.place.PlaceController.PLACE_QUERY_MAX_PAGE_SIZE;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import com.mercadodavidv.hauntedreviews.common.ErrorResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
class PlaceControllerAdvice {

  @ExceptionHandler(PlaceNotFoundException.class)
  @ResponseStatus(HttpStatus.NOT_FOUND)
  ErrorResponse handlePlaceNotFound(PlaceNotFoundException ex) {

    ErrorResponse errorResponse = new ErrorResponse("Place Not Found", ex.getMessage());
    errorResponse.add(
        linkTo(methodOn(PlaceController.class).one(ex.getPlaceId())).withRel("place"));

    return errorResponse;
  }

  @ExceptionHandler(PlaceSearchException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  ErrorResponse handleInvalidPlaceSearch(PlaceSearchException ex) {

    ErrorResponse errorResponse = new ErrorResponse("Invalid Place Search", ex.getMessage());
    errorResponse.add(linkTo(
        methodOn(PlaceController.class).all(Pageable.ofSize(PLACE_QUERY_MAX_PAGE_SIZE))).withRel(
        "places"));

    return errorResponse;
  }
}
