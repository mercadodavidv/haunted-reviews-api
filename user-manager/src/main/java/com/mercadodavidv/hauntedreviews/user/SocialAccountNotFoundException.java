package com.mercadodavidv.hauntedreviews.user;

import java.io.Serial;
import lombok.Getter;

@Getter
class SocialAccountNotFoundException extends RuntimeException {

  @Serial
  private static final long serialVersionUID = 4398222995746573341L;

  private final String principalName;

  private final String providerId;

  SocialAccountNotFoundException(String principalName, String providerId) {

    super("Could not find social account with provider ID=" + providerId + " and principal name="
        + principalName + ".");
    this.principalName = principalName;
    this.providerId = providerId;
  }
}
