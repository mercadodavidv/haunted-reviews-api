package com.mercadodavidv.hauntedreviews.review;

import com.mercadodavidv.hauntedreviews.review.projection.ReviewDetail;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
class ReviewController {

  static final int REVIEW_QUERY_MAX_PAGE_SIZE = 20;

  private final ReviewService reviewService;

  private final ReviewModelAssembler assembler;

  private final PagedResourcesAssembler<ReviewModelBase> pagedResourcesAssembler;

  ReviewController(ReviewService reviewService, //
      ReviewModelAssembler assembler, //
      PagedResourcesAssembler<ReviewModelBase> pagedResourcesAssembler) {

    this.reviewService = reviewService;
    this.assembler = assembler;
    this.pagedResourcesAssembler = pagedResourcesAssembler;
  }

  @GetMapping("/place/{placeId}/reviews")
  PagedModel<EntityModel<ReviewModelBase>> allByPlaceId(@PathVariable Long placeId,
      Pageable pageable) {

    if (pageable.getPageSize() > REVIEW_QUERY_MAX_PAGE_SIZE) {
      throw new ReviewSearchException(placeId, "Size must be less than or equal to 20.");
    }
    Page<ReviewDetail> reviewPage = reviewService.getPlaceReviews(placeId, pageable);

    return pagedResourcesAssembler.toModel(reviewPage.map(ReviewModelBase.class::cast), assembler);
  }

  @PutMapping("/place/{placeId}/review/{userId}")
  ResponseEntity<EntityModel<ReviewModelBase>> replaceReview(
      @PathVariable(name = "placeId") Long placeId, @PathVariable(name = "userId") Long userId,
      @RequestBody ReviewInput newReview) {

    ReviewDetail updatedReview = reviewService.replaceReview(placeId, userId, newReview);

    EntityModel<ReviewModelBase> entityModel = assembler.toModel(updatedReview);
    return ResponseEntity.created(entityModel.getRequiredLink(IanaLinkRelations.SELF).toUri())
        .body(entityModel);
  }

  @GetMapping("/place/{placeId}/review/{userId}")
  EntityModel<ReviewModelBase> one(@PathVariable Long placeId, @PathVariable Long userId) {

    ReviewDetail review = reviewService.getPlaceReviewDetail(placeId, userId);

    return assembler.toModel(review);
  }

  @DeleteMapping("/place/{placeId}/review/{userId}")
  ResponseEntity<String> deleteReview(@PathVariable Long placeId, @PathVariable Long userId) {

    reviewService.deleteReview(placeId, userId);

    return ResponseEntity.noContent().build();
  }
}
