package com.mercadodavidv.hauntedreviews.user;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

@Component
class UserPrivateProfileModelAssembler<T extends UserModelBase> implements
    RepresentationModelAssembler<T, EntityModel<T>> {

  @Override
  @NonNull
  public EntityModel<T> toModel(@NonNull T user) {

    return EntityModel.of(user, //
        linkTo(methodOn(UserController.class).privateProfile(user.getId())).withSelfRel(), //
        linkTo(methodOn(UserController.class).one(user.getUsername())).withRel("user-profile"));
  }
}
