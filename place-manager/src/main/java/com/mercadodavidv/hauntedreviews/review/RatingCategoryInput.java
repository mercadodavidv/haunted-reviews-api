package com.mercadodavidv.hauntedreviews.review;

record RatingCategoryInput
    (String title, //
     String description, //
     String lowScoreLabel, //
     String highScoreLabel, //
     boolean affectsOverallScore) {

}
