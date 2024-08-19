package com.mercadodavidv.hauntedreviews.review;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.util.List;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
class RatingCategoryController {

  private final ReviewService reviewService;

  private final RatingCategoryModelAssembler assembler;

  RatingCategoryController(ReviewService reviewService, RatingCategoryModelAssembler assembler) {

    this.reviewService = reviewService;
    this.assembler = assembler;
  }

  @GetMapping("/rating-categories")
  CollectionModel<EntityModel<RatingCategory>> all() {

    List<EntityModel<RatingCategory>> ratingCategories = reviewService.getAllRatingCategories()
        .stream() //
        .map(assembler::toModel) //
        .toList();

    return CollectionModel.of(ratingCategories,
        linkTo(methodOn(RatingCategoryController.class).all()).withSelfRel());
  }

  @GetMapping("/rating-category/{ratingCategoryId}")
  EntityModel<RatingCategory> one(@PathVariable Long ratingCategoryId) {

    RatingCategory ratingCategory = reviewService.getRatingCategory(ratingCategoryId);

    return assembler.toModel(ratingCategory);
  }
}
