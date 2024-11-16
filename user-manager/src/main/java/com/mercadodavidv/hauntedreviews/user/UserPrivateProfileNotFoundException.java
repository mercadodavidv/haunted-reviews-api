package com.mercadodavidv.hauntedreviews.user;

import java.io.Serial;
import lombok.Getter;

@Getter
class UserPrivateProfileNotFoundException extends RuntimeException {

  @Serial
  private static final long serialVersionUID = 2904913743830113372L;

  private final Long userId;

  UserPrivateProfileNotFoundException(Long userId) {

    super("Could not find user with ID=" + userId + ".");
    this.userId = userId;
  }
}
