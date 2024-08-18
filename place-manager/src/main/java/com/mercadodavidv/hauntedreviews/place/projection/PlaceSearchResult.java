package com.mercadodavidv.hauntedreviews.place.projection;

import com.mercadodavidv.hauntedreviews.common.GeographicCoordinates;
import com.mercadodavidv.hauntedreviews.place.PlaceModelBase;
import java.util.List;

public interface PlaceSearchResult extends PlaceModelBase {

  Long getId();

  String getTitle();

  List<PlaceSearchResult.PlaceAward> getAwards();

  PlaceSearchResult.PlaceLocation getLocation();

  float getAverageScore();

  int getTotalReviews();

  interface PlaceAward {

    Long getId();

    Long getAwardTypeId();

  }

  interface PlaceLocation {

    String getLocationDisplayName();

    GeographicCoordinates getGeoCoordinates();

  }
}
