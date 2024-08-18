package com.mercadodavidv.hauntedreviews.common;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import org.junit.jupiter.api.Test;

class GeographicCoordinatesTest {

  @Test
  void testGeoCoordinates_AllArgsConstructor() {

    double latitude = 20.1234;
    double longitude = -10.1234;

    GeographicCoordinates coordinates = new GeographicCoordinates(latitude, longitude);

    assertEquals(latitude, coordinates.getLatitude());
    assertEquals(longitude, coordinates.getLongitude());
  }

  @Test
  void testGeoCoordinates_EqualsAndHashCode() {

    double latitude = 10.1234;
    double longitude = -20.1234;

    GeographicCoordinates coordinates1 = new GeographicCoordinates(latitude, longitude);
    GeographicCoordinates coordinates2 = new GeographicCoordinates(latitude, longitude);

    // Equality
    assertEquals(coordinates1, coordinates2);
    assertEquals(coordinates1.hashCode(), coordinates2.hashCode());

    // Inequality
    GeographicCoordinates coordinates3 = new GeographicCoordinates(latitude + 1, longitude + 1);
    assertNotEquals(coordinates1, coordinates3);
    assertNotEquals(coordinates1.hashCode(), coordinates3.hashCode());
  }
}
