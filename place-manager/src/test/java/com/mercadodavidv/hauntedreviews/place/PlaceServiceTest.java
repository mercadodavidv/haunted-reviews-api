package com.mercadodavidv.hauntedreviews.place;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.mercadodavidv.hauntedreviews.common.GeographicCoordinates;
import com.mercadodavidv.hauntedreviews.place.projection.PlaceDetail;
import com.mercadodavidv.hauntedreviews.place.projection.PlaceSearchResult;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
class PlaceServiceTest {

  @Mock
  private PlaceRepository repository;

  @InjectMocks
  private PlaceService placeService;

  @Test
  void testSearchPublicPlaces_Success() {

    Pageable pageable = mock(Pageable.class);
    Page<PlaceSearchResult> expectedPage = mock(Page.class);

    when(repository.searchAllByAccessLevel(PlaceAccessLevel.PUBLIC, pageable)).thenReturn(
        expectedPage);

    Page<PlaceSearchResult> result = placeService.searchPublicPlaces(pageable);

    assertEquals(expectedPage, result);
    verify(repository).searchAllByAccessLevel(PlaceAccessLevel.PUBLIC, pageable);
  }

  @Test
  void testSavePlace_Success() {

    PlaceInput placeInput = mock(PlaceInput.class);
    Place savedPlace = mock(Place.class);
    PlaceDetail expectedDetail = mock(PlaceDetail.class);

    when(placeInput.title()).thenReturn("Title");
    when(placeInput.description()).thenReturn("Description");
    when(placeInput.locationDisplayName()).thenReturn("Location");
    when(placeInput.geoCoordinates()).thenReturn(mock(GeographicCoordinates.class));
    when(placeInput.accessLevel()).thenReturn(PlaceAccessLevel.PUBLIC);

    when(repository.saveAndFlush(any(Place.class))).thenReturn(savedPlace);
    when(savedPlace.getId()).thenReturn(1L);
    when(repository.findById(1L, PlaceDetail.class)).thenReturn(Optional.of(expectedDetail));

    PlaceDetail result = placeService.savePlace(placeInput);

    assertEquals(expectedDetail, result);
    verify(repository).saveAndFlush(any(Place.class));
    verify(repository).findById(1L, PlaceDetail.class);
  }

  @Test
  void testSavePlace_Failure() {

    PlaceInput placeInput = mock(PlaceInput.class);
    Place savedPlace = mock(Place.class);

    when(placeInput.title()).thenReturn("Title");
    when(placeInput.description()).thenReturn("Description");
    when(placeInput.locationDisplayName()).thenReturn("Location");
    when(placeInput.geoCoordinates()).thenReturn(mock(GeographicCoordinates.class));
    when(placeInput.accessLevel()).thenReturn(PlaceAccessLevel.PUBLIC);

    when(repository.saveAndFlush(any(Place.class))).thenReturn(savedPlace);
    when(savedPlace.getId()).thenReturn(1L);
    when(repository.findById(1L, PlaceDetail.class)).thenReturn(Optional.empty());

    assertThrows(InvalidPlaceException.class, () -> placeService.savePlace(placeInput));
    verify(repository).saveAndFlush(any(Place.class));
    verify(repository).findById(1L, PlaceDetail.class);
  }

  @Test
  void testGetPlaceDetail_Success() {

    Long placeId = 1L;
    PlaceDetail expectedDetail = mock(PlaceDetail.class);

    when(repository.findById(placeId, PlaceDetail.class)).thenReturn(Optional.of(expectedDetail));

    PlaceDetail result = placeService.getPlaceDetail(placeId);

    assertEquals(expectedDetail, result);
    verify(repository).findById(placeId, PlaceDetail.class);
  }

  @Test
  void testGetPlaceDetail_NotFound() {

    Long placeId = 1L;

    when(repository.findById(placeId, PlaceDetail.class)).thenReturn(Optional.empty());

    assertThrows(PlaceNotFoundException.class, () -> placeService.getPlaceDetail(placeId));
    verify(repository).findById(placeId, PlaceDetail.class);
  }

  @Test
  void testDeletePlace_Success() {

    Long placeId = 1L;

    when(repository.existsById(placeId)).thenReturn(true);

    placeService.deletePlace(placeId);

    verify(repository).existsById(placeId);
    verify(repository).deleteById(placeId);
  }

  @Test
  void testDeletePlace_NotFound() {

    Long placeId = 1L;

    when(repository.existsById(placeId)).thenReturn(false);

    assertThrows(PlaceNotFoundException.class, () -> placeService.deletePlace(placeId));
    verify(repository).existsById(placeId);
    verify(repository, never()).deleteById(placeId);
  }

  @Test
  void testPlaceExists_True() {

    Long placeId = 1L;

    when(repository.existsById(placeId)).thenReturn(true);

    boolean exists = placeService.placeExists(placeId);

    assertTrue(exists);
    verify(repository).existsById(placeId);
  }

  @Test
  void testPlaceExists_False() {

    Long placeId = 1L;

    when(repository.existsById(placeId)).thenReturn(false);

    boolean exists = placeService.placeExists(placeId);

    assertFalse(exists);
    verify(repository).existsById(placeId);
  }
}
