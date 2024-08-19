package com.mercadodavidv.hauntedreviews.review.projection;

import com.mercadodavidv.hauntedreviews.review.ReviewModelBase;
import java.util.Map;

public interface ReviewDetail extends ReviewModelBase {

  Long getPlaceId();

  Long getUserId();

  String getTitle();

  String getBody();

  Map<Long, Short>  getScoresByRatingCategoryId();

}
