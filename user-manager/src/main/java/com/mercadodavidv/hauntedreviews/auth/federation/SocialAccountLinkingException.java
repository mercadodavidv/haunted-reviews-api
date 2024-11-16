package com.mercadodavidv.hauntedreviews.auth.federation;

import java.io.Serial;

public class SocialAccountLinkingException extends RuntimeException {

  @Serial
  private static final long serialVersionUID = 5324087465355913264L;

  public SocialAccountLinkingException(String message, String providerId) {
    super(message + " provider_id=" + providerId);
  }
}
