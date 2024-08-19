package com.mercadodavidv.hauntedreviews.review;

import static com.mercadodavidv.hauntedreviews.review.ReviewController.REVIEW_QUERY_MAX_PAGE_SIZE;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.afford;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

@Component
class ReviewModelAssembler implements
    RepresentationModelAssembler<ReviewModelBase, EntityModel<ReviewModelBase>> {

  @Override
  @NonNull
  public EntityModel<ReviewModelBase> toModel(@NonNull ReviewModelBase review) {

    return EntityModel.of(review, //
        linkTo(methodOn(ReviewController.class).one(review.getPlaceId(),
            review.getUserId())).withSelfRel() //
            .andAffordance(afford(methodOn(ReviewController.class).deleteReview(review.getPlaceId(),
                review.getUserId()))) //
            .andAffordance(afford(
                methodOn(ReviewController.class).replaceReview(review.getPlaceId(),
                    review.getUserId(), null))), //
        linkTo(methodOn(ReviewController.class).allByPlaceId(review.getPlaceId(),
            Pageable.ofSize(REVIEW_QUERY_MAX_PAGE_SIZE))).withRel("place-reviews"));
  }
}
