package com.mercadodavidv.hauntedreviews.review;

class RatingCategoryNotFoundException extends RuntimeException {

  RatingCategoryNotFoundException(Long ratingCategoryId) {
    super("Could not find rating category with ID=" + ratingCategoryId);
  }
}
