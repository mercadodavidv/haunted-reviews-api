package com.mercadodavidv.hauntedreviews.auth.federation;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SocialAccountCreateInput {

  private final String principalName;

  private final String providerId;

  private final String email;

  private final Long userId;

}
