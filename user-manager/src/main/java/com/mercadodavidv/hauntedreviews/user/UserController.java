package com.mercadodavidv.hauntedreviews.user;

import com.mercadodavidv.hauntedreviews.user.projection.UserPrivateProfile;
import com.mercadodavidv.hauntedreviews.user.projection.UserProfile;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
class UserController {

  private final UserProfileService userProfileService;

  private final UserProfileModelAssembler<UserProfile> userProfileAssembler;

  private final UserPrivateProfileModelAssembler<UserPrivateProfile> userPrivateProfileAssembler;

  UserController(UserProfileService userProfileService,
      UserProfileModelAssembler<UserProfile> userProfileAssembler,
      UserPrivateProfileModelAssembler<UserPrivateProfile> userPrivateProfileAssembler) {

    this.userProfileService = userProfileService;
    this.userProfileAssembler = userProfileAssembler;
    this.userPrivateProfileAssembler = userPrivateProfileAssembler;
  }

  @GetMapping("/user/{username}/profile")
  EntityModel<UserProfile> one(@PathVariable String username) {

    UserProfile userProfile = userProfileService.getUserProfile(username);

    return userProfileAssembler.toModel(userProfile);
  }

  @GetMapping("/user-account/{userId}")
  EntityModel<UserPrivateProfile> privateProfile(@PathVariable Long userId) {

    UserPrivateProfile userPrivateProfile = userProfileService.getUserPrivateProfile(userId);

    return userPrivateProfileAssembler.toModel(userPrivateProfile);
  }

  @GetMapping("/validation/password")
  ResponseEntity<String> validatePassword(@RequestBody String password) {

    userProfileService.validatePassword(password);
    return ResponseEntity.ok().build();
  }
}
