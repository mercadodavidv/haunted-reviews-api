package com.mercadodavidv.hauntedreviews.review;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

@Component
class RatingCategoryModelAssembler implements
    RepresentationModelAssembler<RatingCategory, EntityModel<RatingCategory>> {

  @Override
  @NonNull
  public EntityModel<RatingCategory> toModel(@NonNull RatingCategory ratingCategory) {

    return EntityModel.of(ratingCategory, //
        linkTo(methodOn(RatingCategoryController.class).one(ratingCategory.getId())).withSelfRel(),
        linkTo(methodOn(RatingCategoryController.class).all()).withRel("rating-categories"));
  }
}
