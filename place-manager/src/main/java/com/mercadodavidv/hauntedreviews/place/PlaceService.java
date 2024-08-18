package com.mercadodavidv.hauntedreviews.place;

import com.mercadodavidv.hauntedreviews.place.projection.PlaceDetail;
import com.mercadodavidv.hauntedreviews.place.projection.PlaceSearchResult;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class PlaceService {

  private final PlaceRepository repository;

  PlaceService(PlaceRepository repository) {
    this.repository = repository;
  }

  public Page<PlaceSearchResult> searchPublicPlaces(Pageable pageable) {
    return repository.searchAllByAccessLevel(PlaceAccessLevel.PUBLIC, pageable);
  }

  PlaceDetail savePlace(PlaceInput newPlace) {

    PlaceLocation newPlaceLocation = new PlaceLocation(null, newPlace.locationDisplayName(),
        newPlace.geoCoordinates());
    Place newPlaceEntity = Place.builder() //
        .title(newPlace.title()) //
        .description(newPlace.description()) //
        .location(newPlaceLocation) //
        .accessLevel(newPlace.accessLevel()) //
        .build();
    Long placeId = repository.saveAndFlush(newPlaceEntity).getId();
    return repository.findById(placeId, PlaceDetail.class)
        .orElseThrow(() -> new InvalidPlaceException("Failed to save new place."));
  }

  public PlaceDetail getPlaceDetail(Long placeId) {

    return repository.findById(placeId, PlaceDetail.class)
        .orElseThrow(() -> new PlaceNotFoundException(placeId));
  }

  public void deletePlace(Long placeId) {

    if (!repository.existsById(placeId)) {
      throw new PlaceNotFoundException(placeId);
    }
    repository.deleteById(placeId);
  }

  public boolean placeExists(Long placeId) {
    return repository.existsById(placeId);
  }
}
