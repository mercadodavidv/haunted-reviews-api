package com.mercadodavidv.hauntedreviews.review;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.mercadodavidv.hauntedreviews.place.PlaceService;
import com.mercadodavidv.hauntedreviews.review.projection.ReviewDetail;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
class ReviewServiceTest {

  @Mock
  private ReviewRepository reviewRepository;

  @Mock
  private RatingCategoryRepository ratingCategoryRepository;

  @Mock
  private PlaceService placeService;

  @InjectMocks
  private ReviewService reviewService;

  @Test
  void testGetPlaceReviews_Success() {

    Long placeId = 1L;
    Pageable pageable = mock(Pageable.class);
    Page<ReviewDetail> expectedPage = new PageImpl<>(List.of(mock(ReviewDetail.class)));

    when(reviewRepository.findAllById_PlaceId(placeId, pageable)).thenReturn(expectedPage);

    Page<ReviewDetail> result = reviewService.getPlaceReviews(placeId, pageable);

    assertEquals(expectedPage, result);
    assertEquals(1, result.getTotalElements());
    verify(reviewRepository, times(1)).findAllById_PlaceId(placeId, pageable);
  }

  @Test
  void testReplaceReview_SaveNewReview_Success() {

    Long placeId = 1L;
    Long userId = 1L;
    ReviewInput reviewInput = new ReviewInput("Title", "Body...",
        Map.ofEntries(Map.entry(1L, (short) 0), Map.entry(2L, (short) 10)));

    when(placeService.placeExists(placeId)).thenReturn(true);
    when(reviewRepository.findById(any())).thenReturn(Optional.empty());
    when(reviewRepository.findById_PlaceIdAndId_UserId(placeId, userId,
        ReviewDetail.class)).thenReturn(Optional.of(mock(ReviewDetail.class)));
    when(ratingCategoryRepository.existsById(any())).thenReturn(true);

    ReviewDetail result = reviewService.replaceReview(placeId, userId, reviewInput);

    assertNotNull(result);
    verify(reviewRepository, times(1)).save(any(Review.class));
  }

  @Test
  void testReplaceReview_SaveNewReview_Failure_InvalidCategoryId() {

    Long placeId = 1L;
    Long userId = 1L;
    Long invalidCategoryId = 15L;
    ReviewInput reviewInput = new ReviewInput("Title", "Body...",
        Map.ofEntries(Map.entry(invalidCategoryId, (short) 4)));

    when(placeService.placeExists(placeId)).thenReturn(true);
    when(ratingCategoryRepository.existsById(invalidCategoryId)).thenReturn(false);

    assertThrows(InvalidReviewException.class,
        () -> reviewService.replaceReview(placeId, userId, reviewInput));
    verify(reviewRepository, times(0)).save(any(Review.class));
  }

  @Test
  void testReplaceReview_SaveNewReview_Failure_InvalidCategoryScore() {

    Long placeId = 1L;
    Long userId = 1L;
    Long validCategoryId1 = 1L;
    Long validCategoryId2 = 2L;
    ReviewInput reviewInput = new ReviewInput("Title", "Body...",
        Map.ofEntries(Map.entry(validCategoryId1, (short) 5),
            Map.entry(validCategoryId2, (short) 12)));

    when(placeService.placeExists(placeId)).thenReturn(true);
    when(ratingCategoryRepository.existsById(validCategoryId1)).thenReturn(true);
    when(ratingCategoryRepository.existsById(validCategoryId2)).thenReturn(true);

    assertThrows(InvalidReviewException.class,
        () -> reviewService.replaceReview(placeId, userId, reviewInput));
    verify(reviewRepository, times(0)).save(any(Review.class));
  }

  @Test
  void testReplaceReview_UpdateExistingReview_Success() {

    Long placeId = 1L;
    Long userId = 1L;
    ReviewInput reviewInput = mock(ReviewInput.class);
    Review existingReview = mock(Review.class);

    when(placeService.placeExists(placeId)).thenReturn(true);
    when(reviewRepository.findById(any())).thenReturn(Optional.of(existingReview));
    when(reviewRepository.findById_PlaceIdAndId_UserId(placeId, userId,
        ReviewDetail.class)).thenReturn(Optional.of(mock(ReviewDetail.class)));

    ReviewDetail result = reviewService.replaceReview(placeId, userId, reviewInput);

    assertNotNull(result);
    verify(existingReview, times(1)).setTitle(any());
    verify(existingReview, times(1)).setBody(any());
    verify(existingReview, times(1)).setScoresByRatingCategoryId(any());
    verify(reviewRepository, times(1)).save(existingReview);
  }

  @Test
  void testReplaceReview_Failure_PlaceDoesNotExist() {

    Long placeId = 1L;
    Long userId = 1L;
    ReviewInput reviewInput = mock(ReviewInput.class);

    when(placeService.placeExists(placeId)).thenReturn(false);

    assertThrows(InvalidReviewException.class,
        () -> reviewService.replaceReview(placeId, userId, reviewInput));
  }

  @Test
  void testGetPlaceReviewDetail_Success() {

    Long placeId = 1L;
    Long userId = 1L;
    ReviewDetail reviewDetail = mock(ReviewDetail.class);

    when(reviewRepository.findById_PlaceIdAndId_UserId(placeId, userId,
        ReviewDetail.class)).thenReturn(Optional.of(reviewDetail));

    ReviewDetail result = reviewService.getPlaceReviewDetail(placeId, userId);

    assertNotNull(result);
    verify(reviewRepository, times(1)).findById_PlaceIdAndId_UserId(placeId, userId,
        ReviewDetail.class);
  }

  @Test
  void testGetPlaceReviewDetail_Failure_ReviewNotFound() {

    Long placeId = 1L;
    Long userId = 1L;

    when(reviewRepository.findById_PlaceIdAndId_UserId(placeId, userId,
        ReviewDetail.class)).thenReturn(Optional.empty());

    assertThrows(ReviewNotFoundException.class,
        () -> reviewService.getPlaceReviewDetail(placeId, userId));
  }

  @Test
  void testDeleteReview_Success() {

    Long placeId = 1L;
    Long userId = 1L;
    ReviewKey reviewKey = new ReviewKey(placeId, userId);

    when(reviewRepository.existsById(reviewKey)).thenReturn(true);

    reviewService.deleteReview(placeId, userId);

    verify(reviewRepository, times(1)).deleteById(reviewKey);
  }

  @Test
  void testDeleteReview_Failure_ReviewNotFound() {

    Long placeId = 1L;
    Long userId = 1L;
    ReviewKey reviewKey = new ReviewKey(placeId, userId);

    when(reviewRepository.existsById(reviewKey)).thenReturn(false);

    assertThrows(ReviewNotFoundException.class, () -> reviewService.deleteReview(placeId, userId));
  }

  @Test
  void testSaveRatingCategory_Success() {

    RatingCategoryInput ratingCategoryInput = mock(RatingCategoryInput.class);
    RatingCategory ratingCategory = mock(RatingCategory.class);

    when(ratingCategoryRepository.save(any(RatingCategory.class))).thenReturn(ratingCategory);

    RatingCategory result = reviewService.saveRatingCategory(ratingCategoryInput);

    assertNotNull(result);
    verify(ratingCategoryRepository, times(1)).save(any(RatingCategory.class));
  }

  @Test
  void testSaveMultipleRatingCategories_Success() {

    RatingCategoryInput ratingCategoryInput1 = mock(RatingCategoryInput.class);
    RatingCategoryInput ratingCategoryInput2 = mock(RatingCategoryInput.class);
    List<RatingCategoryInput> inputs = List.of(ratingCategoryInput1, ratingCategoryInput2);
    List<RatingCategory> savedCategories = List.of(mock(RatingCategory.class),
        mock(RatingCategory.class));

    when(ratingCategoryRepository.saveAll(any())).thenReturn(savedCategories);

    List<RatingCategory> result = reviewService.saveRatingCategories(inputs);

    assertNotNull(result);
    assertEquals(2, result.size());
    verify(ratingCategoryRepository, times(1)).saveAll(any());
  }

  @Test
  void testGetAllRatingCategories_Success() {

    List<RatingCategory> ratingCategories = List.of(mock(RatingCategory.class),
        mock(RatingCategory.class));

    when(ratingCategoryRepository.findAll()).thenReturn(ratingCategories);

    List<RatingCategory> result = reviewService.getAllRatingCategories();

    assertNotNull(result);
    assertEquals(2, result.size());
    verify(ratingCategoryRepository, times(1)).findAll();
  }

  @Test
  void testGetRatingCategory_Success() {

    Long categoryId = 1L;
    RatingCategory ratingCategory = mock(RatingCategory.class);

    when(ratingCategoryRepository.findById(categoryId)).thenReturn(Optional.of(ratingCategory));

    RatingCategory result = reviewService.getRatingCategory(categoryId);

    assertNotNull(result);
    verify(ratingCategoryRepository, times(1)).findById(categoryId);
  }

  @Test
  void testGetRatingCategory_Failure_RatingCategoryNotFound() {

    Long categoryId = 1L;

    when(ratingCategoryRepository.findById(categoryId)).thenReturn(Optional.empty());

    assertThrows(RatingCategoryNotFoundException.class,
        () -> reviewService.getRatingCategory(categoryId));
  }

  @Test
  void testFindInvalidRatingCategories_Success() {

    Long validId = 1L;
    Long invalidId = 2L;
    when(ratingCategoryRepository.existsById(validId)).thenReturn(true);
    when(ratingCategoryRepository.existsById(invalidId)).thenReturn(false);

    Set<Long> result = reviewService.findInvalidRatingCategories(Set.of(validId, invalidId));

    assertNotNull(result);
    assertEquals(1, result.size());
    assertTrue(result.contains(invalidId));
  }

  @Test
  void testValidRatingScore_Valid() {
    assertTrue(reviewService.validRatingScore((short) 5));
  }

  @Test
  void testValidRatingScore_Valid_Max() {
    assertTrue(reviewService.validRatingScore((short) 10));
  }

  @Test
  void testValidRatingScore_Valid_Min() {
    assertTrue(reviewService.validRatingScore((short) 0));
  }

  @Test
  void testValidRatingScore_Invalid_High() {
    assertFalse(reviewService.validRatingScore((short) 11));
  }

  @Test
  void testValidRatingScore_Invalid_Low() {
    assertFalse(reviewService.validRatingScore((short) -1));
  }
}
