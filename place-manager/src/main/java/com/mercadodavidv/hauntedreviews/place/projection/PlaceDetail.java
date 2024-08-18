package com.mercadodavidv.hauntedreviews.place.projection;

import com.mercadodavidv.hauntedreviews.common.GeographicCoordinates;
import com.mercadodavidv.hauntedreviews.place.PlaceModelBase;
import java.time.Instant;
import java.util.List;
import java.util.Map;

public interface PlaceDetail extends PlaceModelBase {

  Long getId();

  PlaceDetail.AuditMetadata getAuditMetadata();

  String getTitle();

  String getDescription();

  List<PlaceDetail.PlaceAward> getAwards();

  PlaceDetail.PlaceLocation getLocation();

  float getAverageScore();

  int getTotalReviews();

  Map<Long, Float> getAverageCategoryScoresByCategoryId();

  interface PlaceAward {

    Long getId();

    Long getUserId();

    Long getAwardTypeId();

  }

  interface AuditMetadata {

    Instant getCreatedDate();

    Instant getLastModifiedDate();

  }

  interface PlaceLocation {

    String getLocationDisplayName();

    GeographicCoordinates getGeoCoordinates();

  }
}
