package com.mercadodavidv.hauntedreviews.user.projection;

import com.mercadodavidv.hauntedreviews.user.UserModelBase;
import java.time.Instant;

public interface UserProfile extends UserModelBase {

  Long getId();

  Instant getCreatedDate();

  String getUsername();

  String getProfileImageUrl();

}
