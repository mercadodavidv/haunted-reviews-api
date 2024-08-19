package com.mercadodavidv.hauntedreviews.review;

import java.util.Map;

public record ReviewInput
    (String title, //
     String body, //
     Map<Long, Short> scoresByRatingCategoryId) {

}
