package com.mercadodavidv.hauntedreviews.user;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import com.mercadodavidv.hauntedreviews.common.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
class UserControllerAdvice {

  @ExceptionHandler(UserProfileNotFoundException.class)
  @ResponseStatus(HttpStatus.NOT_FOUND)
  ErrorResponse handleUserNotFound(UserProfileNotFoundException ex) {

    ErrorResponse errorResponse = new ErrorResponse("User Not Found", ex.getMessage());
    errorResponse.add( //
        linkTo(methodOn(UserController.class).one(ex.getUsername())) //
            .withRel("user-profile"));

    return errorResponse;
  }

  @ExceptionHandler(UserPrivateProfileNotFoundException.class)
  @ResponseStatus(HttpStatus.NOT_FOUND)
  ErrorResponse handleUserNotFound(UserPrivateProfileNotFoundException ex) {

    ErrorResponse errorResponse = new ErrorResponse("User Not Found", ex.getMessage());
    errorResponse.add( //
        linkTo(methodOn(UserController.class).privateProfile(ex.getUserId())) //
            .withRel("user-account"));

    return errorResponse;
  }
}
