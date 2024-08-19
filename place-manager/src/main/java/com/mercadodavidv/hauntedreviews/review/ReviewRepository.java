package com.mercadodavidv.hauntedreviews.review;

import com.mercadodavidv.hauntedreviews.review.projection.ReviewDetail;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

interface ReviewRepository extends JpaRepository<Review, ReviewKey> {

  <T> Optional<T> findById_PlaceIdAndId_UserId(Long placeId, Long userId, Class<T> type);

  Page<ReviewDetail> findAllById_PlaceId(Long placeId, Pageable pageable);

}
