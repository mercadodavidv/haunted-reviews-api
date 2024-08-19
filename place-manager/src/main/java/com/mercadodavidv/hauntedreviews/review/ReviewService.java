package com.mercadodavidv.hauntedreviews.review;

import com.mercadodavidv.hauntedreviews.common.AuditMetadata;
import com.mercadodavidv.hauntedreviews.place.PlaceService;
import com.mercadodavidv.hauntedreviews.review.projection.ReviewDetail;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ReviewService {

  private final ReviewRepository reviewRepository;

  private final RatingCategoryRepository ratingCategoryRepository;

  private final PlaceService placeService;

  ReviewService(ReviewRepository reviewRepository,
      RatingCategoryRepository ratingCategoryRepository, PlaceService placeService) {

    this.reviewRepository = reviewRepository;
    this.ratingCategoryRepository = ratingCategoryRepository;
    this.placeService = placeService;
  }

  public Page<ReviewDetail> getPlaceReviews(Long placeId, Pageable pageable) {
    return reviewRepository.findAllById_PlaceId(placeId, pageable);
  }

  ReviewDetail replaceReview(Long placeId, Long userId, ReviewInput newReview) {

    // Validation
    if (!placeService.placeExists(placeId)) {
      throw new InvalidReviewException(placeId, userId, "Place does not exist.");
    }
    Set<Long> invalidCategoryIds = this.findInvalidRatingCategories(
        newReview.scoresByRatingCategoryId().keySet());
    if (!invalidCategoryIds.isEmpty()) {
      throw new InvalidReviewException(placeId, userId,
          "One or more rating categories do not exist: " + invalidCategoryIds + ".");
    }
    if (!newReview.scoresByRatingCategoryId().values().stream().allMatch(this::validRatingScore)) {
      throw new InvalidReviewException(placeId, userId,
          "One or more rating category scores are not valid.");
    }
    // TODO - Validate user ID

    ReviewKey key = new ReviewKey(placeId, userId);
    Optional<Review> existingReviewEntity = reviewRepository.findById(key);
    if (existingReviewEntity.isPresent()) {
      Review review = existingReviewEntity.get();
      review.setTitle(newReview.title());
      review.setBody(newReview.body());
      review.setScoresByRatingCategoryId(newReview.scoresByRatingCategoryId());
      reviewRepository.save(review);
    } else {
      Review newReviewEntity = new Review(key, AuditMetadata.now(), newReview.title(),
          newReview.body(), newReview.scoresByRatingCategoryId());
      reviewRepository.save(newReviewEntity);
    }
    return reviewRepository.findById_PlaceIdAndId_UserId(placeId, userId, ReviewDetail.class)
        .orElseThrow(() -> new InvalidReviewException(placeId, userId, "Failed to save review."));
  }

  public ReviewDetail getPlaceReviewDetail(Long placeId, Long userId) {

    return reviewRepository.findById_PlaceIdAndId_UserId(placeId, userId, ReviewDetail.class)
        .orElseThrow(() -> new ReviewNotFoundException(placeId, userId));
  }

  public void deleteReview(Long placeId, Long userId) {

    ReviewKey key = new ReviewKey(placeId, userId);
    if (!reviewRepository.existsById(key)) {
      throw new ReviewNotFoundException(key);
    }
    reviewRepository.deleteById(key);
  }

  RatingCategory saveRatingCategory(RatingCategoryInput newRatingCategory) {

    return ratingCategoryRepository.save(RatingCategory.builder() //
        .title(newRatingCategory.title()) //
        .description(newRatingCategory.description()) //
        .lowScoreLabel(newRatingCategory.lowScoreLabel()) //
        .highScoreLabel(newRatingCategory.highScoreLabel()) //
        .overallScoreAffected(newRatingCategory.affectsOverallScore()) //
        .build());
  }

  List<RatingCategory> saveRatingCategories(Collection<RatingCategoryInput> newRatingCategories) {

    return ratingCategoryRepository.saveAll(
        newRatingCategories.parallelStream().map(newRatingCategory -> RatingCategory.builder() //
            .title(newRatingCategory.title()) //
            .description(newRatingCategory.description()) //
            .lowScoreLabel(newRatingCategory.lowScoreLabel()) //
            .highScoreLabel(newRatingCategory.highScoreLabel()) //
            .overallScoreAffected(newRatingCategory.affectsOverallScore()) //
            .build()).toList());
  }

  List<RatingCategory> getAllRatingCategories() {
    return ratingCategoryRepository.findAll();
  }

  RatingCategory getRatingCategory(Long ratingCategoryId) {

    return ratingCategoryRepository.findById(ratingCategoryId)
        .orElseThrow(() -> new RatingCategoryNotFoundException(ratingCategoryId));
  }

  Set<Long> findInvalidRatingCategories(Collection<Long> ratingCategoryIds) {
    return ratingCategoryIds.stream().filter(id -> !ratingCategoryRepository.existsById(id))
        .collect(Collectors.toSet());
  }

  boolean validRatingScore(short score) {
    return score >= 0 && score <= 10;
  }
}
