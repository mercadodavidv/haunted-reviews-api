package com.mercadodavidv.hauntedreviews.place;

import com.mercadodavidv.hauntedreviews.place.projection.PlaceSearchResult;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

interface PlaceRepository extends JpaRepository<Place, Long> {

  <T> Optional<T> findById(Long id, Class<T> type);

  Page<PlaceSearchResult> searchAllByAccessLevel(PlaceAccessLevel accessLevel, Pageable pageable);

}