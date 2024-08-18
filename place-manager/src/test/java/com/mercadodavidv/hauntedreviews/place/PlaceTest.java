package com.mercadodavidv.hauntedreviews.place;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.mercadodavidv.hauntedreviews.common.AuditMetadata;
import com.mercadodavidv.hauntedreviews.common.GeographicCoordinates;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@DataJpaTest
class PlaceTest {

  @Autowired
  private TestEntityManager entityManager;

  @Test
  void testPlaceEntity_Mapping() {

    GeographicCoordinates coordinates = new GeographicCoordinates(34.0522, -118.2437);
    PlaceLocation location = new PlaceLocation(null, "Los Angeles, CA", coordinates);
    Place place = Place.builder() //
        .title("Haunted Mansion") //
        .description("A spooky mansion with a dark history.") //
        .location(location) //
        .accessLevel(PlaceAccessLevel.PUBLIC) //
        .build();

    entityManager.persistAndFlush(place);

    Place retrievedPlace = entityManager.find(Place.class, place.getId());

    assertNotNull(retrievedPlace);
    assertEquals("Haunted Mansion", retrievedPlace.getTitle());
    assertEquals("A spooky mansion with a dark history.", retrievedPlace.getDescription());
    assertEquals("Los Angeles, CA", retrievedPlace.getLocation().getLocationDisplayName());
    assertEquals(coordinates, retrievedPlace.getLocation().getGeoCoordinates());
    assertEquals(PlaceAccessLevel.PUBLIC, retrievedPlace.getAccessLevel());
    assertEquals(0.0f, retrievedPlace.getAverageScore());
    assertEquals(0, retrievedPlace.getTotalReviews());
    assertTrue(retrievedPlace.getAwards().isEmpty()); // Default empty list
    assertTrue(
        retrievedPlace.getAverageCategoryScoresByCategoryId().isEmpty()); // Default empty map
  }

  @Test
  void testPlaceEntity_DefaultValues() {

    GeographicCoordinates coordinates = new GeographicCoordinates(42.519548, -70.895861);
    PlaceLocation location = new PlaceLocation(null, "Salem, MA", coordinates);
    Place place = Place.builder() //
        .title("Ghost House") //
        .description("A house full of ghosts.") //
        .location(location) //
        .accessLevel(PlaceAccessLevel.PRIVATE) //
        .build();

    entityManager.persistAndFlush(place);

    Place retrievedPlace = entityManager.find(Place.class, place.getId());

    assertNotNull(retrievedPlace);
    assertNotNull(retrievedPlace.getAuditMetadata()); // AuditMetadata should not be null
    assertNotNull(retrievedPlace.getAuditMetadata().getCreatedDate());
    assertNotNull(retrievedPlace.getAuditMetadata().getLastModifiedDate());
    assertTrue(retrievedPlace.getAwards().isEmpty()); // Default empty list
    assertTrue(
        retrievedPlace.getAverageCategoryScoresByCategoryId().isEmpty()); // Default empty map
    assertEquals(0.0f, retrievedPlace.getAverageScore()); // Default immutable value
    assertEquals(0, retrievedPlace.getTotalReviews()); // Default immutable value
  }

  @Test
  void testPlaceEntity_Relationships() {

    GeographicCoordinates coordinates = new GeographicCoordinates(51.5074, -0.1278);
    PlaceLocation location = new PlaceLocation(null, "London, UK", coordinates);
    Place place = Place.builder() //
        .title("London Ghost Tour") //
        .description("Explore the haunted sites of London.") //
        .location(location) //
        .accessLevel(PlaceAccessLevel.PUBLIC) //
        .build();
    entityManager.persistAndFlush(place);

    Place retrievedPlace = entityManager.find(Place.class, place.getId());

    // First, verify awards list is empty
    assertNotNull(retrievedPlace);
    assertNotNull(retrievedPlace.getAwards());
    assertEquals(0, retrievedPlace.getAwards().size());

    // Then, add the award
    PlaceAward award = new PlaceAward(null, AuditMetadata.now(), place.getId(), 10L, 10L);
    entityManager.persistAndFlush(award);

    entityManager.refresh(retrievedPlace);

    assertNotNull(retrievedPlace);
    assertNotNull(retrievedPlace.getAwards());
    assertEquals(1, retrievedPlace.getAwards().size());
    assertEquals(1L, retrievedPlace.getAwards().getFirst().getId());
  }

  @Test
  void testPlaceEntity_Immutability() {

    PlaceLocation location = new PlaceLocation(null, "Scotland", null);
    Place place = Place.builder() //
        .title("Cursed Castle") //
        .description("A castle with a cursed history.") //
        .location(location) //
        .accessLevel(PlaceAccessLevel.PUBLIC) //
        .build();

    entityManager.persistAndFlush(place);

    // Verify immutable fields cannot be changed
    Place retrievedPlace = entityManager.find(Place.class, place.getId());
    PlaceAward newPlaceAward = new PlaceAward();
    List<PlaceAward> awardList = retrievedPlace.getAwards();
    Map<Long, Float> scoresMap = retrievedPlace.getAverageCategoryScoresByCategoryId();
    assertThrows(UnsupportedOperationException.class, () -> awardList.add(newPlaceAward));
    assertThrows(UnsupportedOperationException.class, () -> scoresMap.put(1L, 5.0f));
  }
}
