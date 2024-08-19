package com.mercadodavidv.hauntedreviews.review;

import static com.mercadodavidv.hauntedreviews.review.ReviewController.REVIEW_QUERY_MAX_PAGE_SIZE;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import com.mercadodavidv.hauntedreviews.common.ErrorResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
class ReviewControllerAdvice {

  @ExceptionHandler(InvalidReviewException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  ErrorResponse handleInvalidReview(InvalidReviewException ex) {

    ErrorResponse errorResponse = new ErrorResponse("Invalid Review", ex.getMessage());
    errorResponse.add(linkTo(
        methodOn(ReviewController.class).replaceReview(ex.getPlaceId(), ex.getUserId(),
            null)).withRel("submit-review"));

    return errorResponse;
  }

  @ExceptionHandler(ReviewNotFoundException.class)
  @ResponseStatus(HttpStatus.NOT_FOUND)
  ErrorResponse handleReviewNotFound(ReviewNotFoundException ex) {

    ErrorResponse errorResponse = new ErrorResponse("Review Not Found", ex.getMessage());
    errorResponse.add(
        linkTo(methodOn(ReviewController.class).one(ex.getPlaceId(), ex.getUserId())).withRel(
            "review"));

    return errorResponse;
  }

  @ExceptionHandler(ReviewSearchException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  ErrorResponse handleInvalidReviewSearch(ReviewSearchException ex) {

    ErrorResponse errorResponse = new ErrorResponse("Invalid Review Search", ex.getMessage());
    errorResponse.add(linkTo(methodOn(ReviewController.class).allByPlaceId(ex.getPlaceId(),
        Pageable.ofSize(REVIEW_QUERY_MAX_PAGE_SIZE))).withRel("place-reviews"));

    return errorResponse;
  }
}
