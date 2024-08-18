package com.mercadodavidv.hauntedreviews.place;

import com.mercadodavidv.hauntedreviews.common.GeographicCoordinates;

record PlaceInput
    (String title, //
     String description, //
     String locationDisplayName, //
     GeographicCoordinates geoCoordinates, //
     PlaceAccessLevel accessLevel) {

}
