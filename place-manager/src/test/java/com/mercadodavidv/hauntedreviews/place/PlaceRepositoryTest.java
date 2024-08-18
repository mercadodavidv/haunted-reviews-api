package com.mercadodavidv.hauntedreviews.place;

import static org.assertj.core.api.Assertions.assertThat;

import com.mercadodavidv.hauntedreviews.common.GeographicCoordinates;
import com.mercadodavidv.hauntedreviews.place.projection.PlaceDetail;
import com.mercadodavidv.hauntedreviews.place.projection.PlaceSearchResult;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@DataJpaTest
class PlaceRepositoryTest {

  @Autowired
  private TestEntityManager entityManager;

  @Autowired
  private PlaceRepository repository;

  private Long publicPlaceId;
  private final String publicPlaceTitle = "Goat Man's Bridge";

  private Long linkOnlyPlaceId;
  private final String linkOnlyPlaceTitle = "Williams Creek Cemetery";

  private Long privatePlaceId;
  private final String privatePlaceTitle = "Fort Worth Water Gardens";

  @BeforeEach
  void setUp() {

    Place publicPlace = Place.builder() //
        .title(publicPlaceTitle) //
        .description("Description of a scary place.") //
        .location(new PlaceLocation(null, "South of Denton, Texas, US",
            new GeographicCoordinates(33.129311, -97.104150))) //
        .accessLevel(PlaceAccessLevel.PUBLIC) //
        .build();
    entityManager.persist(publicPlace);
    publicPlaceId = entityManager.getId(publicPlace, Long.class);

    Place linkOnlyPlace = Place.builder() //
        .title(linkOnlyPlaceTitle) //
        .description("Description of a scary place.") //
        .location(new PlaceLocation(null, "South of La Grange, TX, US",
            new GeographicCoordinates(29.829307, -96.877810))) //
        .accessLevel(PlaceAccessLevel.LINK_ONLY_PUBLIC) //
        .build();
    entityManager.persist(linkOnlyPlace);
    linkOnlyPlaceId = entityManager.getId(linkOnlyPlace, Long.class);

    Place privatePlace = Place.builder() //
        .title(privatePlaceTitle) //
        .description(
            "Tranquil Philip Johnson-designed urban park featuring 4.3 acres of waterfalls, pools & fountains.") // source: Google Maps
        .location(new PlaceLocation(null, "Downtown Fort Worth, Texas, US",
            new GeographicCoordinates(32.747728, -97.326607))) //
        .accessLevel(PlaceAccessLevel.PRIVATE) //
        .build();
    entityManager.persist(privatePlace);
    privatePlaceId = entityManager.getId(privatePlace, Long.class);
  }

  @Test
  void testFindById_PublicEntity() {

    Optional<Place> placeOptional = repository.findById(publicPlaceId);

    assertThat(placeOptional).isPresent().get().hasNoNullFieldsOrProperties();
    assertThat(placeOptional).get().extracting(PlaceModelBase::getId).isEqualTo(publicPlaceId);
    assertThat(placeOptional).get().extracting(Place::getTitle).isEqualTo(publicPlaceTitle);
  }

  @Test
  void testFindById_PublicDetail() {

    Optional<PlaceDetail> placeOptional = repository.findById(publicPlaceId, PlaceDetail.class);

    assertThat(placeOptional).isPresent().get().hasNoNullFieldsOrProperties();
    assertThat(placeOptional).get().extracting(PlaceModelBase::getId).isEqualTo(publicPlaceId);
    assertThat(placeOptional).get().extracting(PlaceDetail::getTitle).isEqualTo(publicPlaceTitle);
  }

  @Test
  void testFindById_PrivateEntity() {

    Optional<Place> placeOptional = repository.findById(privatePlaceId);

    assertThat(placeOptional).isPresent().get().hasNoNullFieldsOrProperties();
    assertThat(placeOptional).get().extracting(PlaceModelBase::getId).isEqualTo(privatePlaceId);
    assertThat(placeOptional).get().extracting(Place::getTitle).isEqualTo(privatePlaceTitle);
  }

  @Test
  void testFindById_LinkOnlyEntity() {

    Optional<Place> placeOptional = repository.findById(linkOnlyPlaceId);

    assertThat(placeOptional).isPresent().get().hasNoNullFieldsOrProperties();
    assertThat(placeOptional).get().extracting(PlaceModelBase::getId).isEqualTo(linkOnlyPlaceId);
    assertThat(placeOptional).get().extracting(Place::getTitle).isEqualTo(linkOnlyPlaceTitle);
  }

  @Test
  void testFindById_LinkOnlySearchResult() {

    Optional<PlaceSearchResult> placeOptional = repository.findById(publicPlaceId,
        PlaceSearchResult.class);

    assertThat(placeOptional).isPresent().get().hasNoNullFieldsOrProperties();
    assertThat(placeOptional).get().extracting(PlaceModelBase::getId).isEqualTo(publicPlaceId);
    assertThat(placeOptional).get().extracting(PlaceSearchResult::getTitle)
        .isEqualTo(publicPlaceTitle);
  }

  @Test
  void testSearchAllByAccessLevel_PUBLIC() {

    Page<PlaceSearchResult> page = repository.searchAllByAccessLevel(PlaceAccessLevel.PUBLIC,
        Pageable.ofSize(10));

    assertThat(page).extracting(PlaceSearchResult::getId).contains(publicPlaceId);
    assertThat(page).extracting(PlaceSearchResult::getId).doesNotContain(privatePlaceId);
    assertThat(page).extracting(PlaceSearchResult::getId).doesNotContain(linkOnlyPlaceId);
  }
}