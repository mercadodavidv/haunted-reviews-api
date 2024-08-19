package com.mercadodavidv.hauntedreviews.review;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.util.List;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin")
class RatingCategoryAdminController {

  private final ReviewService reviewService;

  private final RatingCategoryModelAssembler assembler;

  RatingCategoryAdminController(ReviewService reviewService,
      RatingCategoryModelAssembler assembler) {

    this.reviewService = reviewService;
    this.assembler = assembler;
  }

  @PostMapping("/rating-category")
  ResponseEntity<EntityModel<RatingCategory>> newRatingCategory(
      @RequestBody RatingCategoryInput newRatingCategory) {

    RatingCategory ratingCategory = reviewService.saveRatingCategory(newRatingCategory);

    EntityModel<RatingCategory> entityModel = assembler.toModel(ratingCategory);
    return ResponseEntity.created(entityModel.getRequiredLink(IanaLinkRelations.SELF).toUri())
        .body(entityModel);
  }

  @PostMapping("/rating-categories")
  ResponseEntity<CollectionModel<EntityModel<RatingCategory>>> newRatingCategories(
      @RequestBody List<RatingCategoryInput> newRatingCategories) {

    List<EntityModel<RatingCategory>> ratingCategories = reviewService.saveRatingCategories(
        newRatingCategories).stream().map(assembler::toModel).toList();

    CollectionModel<EntityModel<RatingCategory>> collectionModel = CollectionModel.of(
        ratingCategories, linkTo(methodOn(RatingCategoryController.class).all()).withSelfRel());
    return ResponseEntity.created(collectionModel.getRequiredLink(IanaLinkRelations.SELF).toUri())
        .body(collectionModel);
  }
}
