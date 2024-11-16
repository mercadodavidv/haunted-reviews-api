package com.mercadodavidv.hauntedreviews.user;

import java.io.Serial;
import lombok.Getter;

@Getter
class UserProfileNotFoundException extends RuntimeException {

  @Serial
  private static final long serialVersionUID = 4398222995746573341L;

  private final String username;

  UserProfileNotFoundException(String username) {

    super("Could not find user with username=" + username + ".");
    this.username = username;
  }
}
