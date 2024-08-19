package com.mercadodavidv.hauntedreviews.review;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.mercadodavidv.hauntedreviews.review.projection.ReviewDetail;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.LinkRelation;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.util.UriComponentsBuilder;

@ExtendWith(MockitoExtension.class)
class ReviewControllerTest {

  @Mock
  private ReviewService reviewService;

  @Spy
  private ReviewModelAssembler assembler;

  @Spy
  private PagedResourcesAssembler<ReviewModelBase> pagedResourcesAssembler = new PagedResourcesAssembler<>(
      null, UriComponentsBuilder.fromUriString("/place/1/reviews").build());

  @InjectMocks
  private ReviewController reviewController;

  @Test
  void testAllByPlaceId_Success() {

    Long placeId = 1L;
    Pageable pageable = PageRequest.of(0, 10);
    ReviewDetail review = mock(ReviewDetail.class);
    Page<ReviewDetail> reviewPage = new PageImpl<>(List.of(review));

    when(reviewService.getPlaceReviews(placeId, pageable)).thenReturn(reviewPage);

    PagedModel<EntityModel<ReviewModelBase>> result = reviewController.allByPlaceId(placeId,
        pageable);

    assertNotNull(result.getContent());
    assertThat(result.getLinks().toList()).extracting(Link::getHref)
        .containsOnly("/place/" + placeId + "/reviews");
    assertThat(result.getLinks().toList()).extracting(Link::getRel)
        .containsOnly(IanaLinkRelations.SELF);
    assertThat(result.getContent()).extracting(EntityModel::getContent).containsOnly(review);
    verify(reviewService).getPlaceReviews(placeId, pageable);
    verify(pagedResourcesAssembler).toModel(any(), Mockito.<ReviewModelAssembler>any());
  }

  @Test
  void testAllByPlaceId_PageSizeExceedsLimit() {

    Long placeId = 1L;
    Pageable oversizedPageable = PageRequest.of(0, 25);

    assertThrows(ReviewSearchException.class,
        () -> reviewController.allByPlaceId(placeId, oversizedPageable));
  }

  @Test
  void testReplaceReview_Success() {

    Long placeId = 1L;
    Long userId = 1L;
    ReviewInput reviewInput = mock(ReviewInput.class);
    ReviewDetail reviewDetail = mock(ReviewDetail.class);

    when(reviewDetail.getPlaceId()).thenReturn(placeId);
    when(reviewDetail.getUserId()).thenReturn(userId);
    when(reviewService.replaceReview(placeId, userId, reviewInput)).thenReturn(reviewDetail);

    ResponseEntity<EntityModel<ReviewModelBase>> response = reviewController.replaceReview(placeId,
        userId, reviewInput);

    assertEquals(HttpStatus.CREATED, response.getStatusCode());
    assertNotNull(response.getBody());
    assertThat(response.getBody().getLinks().toList()).extracting(Link::getHref).containsAll(
        Arrays.asList("/place/" + placeId + "/review/" + userId, "/place/" + placeId + "/reviews"));
    assertThat(response.getBody().getLinks().toList()).extracting(Link::getRel)
        .containsAll(Arrays.asList(IanaLinkRelations.SELF, LinkRelation.of("place-reviews")));
    verify(reviewService).replaceReview(placeId, userId, reviewInput);
    verify(assembler).toModel(reviewDetail);
  }

  @Test
  void testOne_Success() {

    Long placeId = 1L;
    Long userId = 1L;
    ReviewDetail reviewDetail = mock(ReviewDetail.class);

    when(reviewDetail.getPlaceId()).thenReturn(placeId);
    when(reviewDetail.getUserId()).thenReturn(userId);
    when(reviewService.getPlaceReviewDetail(placeId, userId)).thenReturn(reviewDetail);

    EntityModel<ReviewModelBase> result = reviewController.one(placeId, userId);

    assertThat(result).extracting(EntityModel::getContent).isEqualTo(reviewDetail);
    assertThat(result.getLinks().toList()).extracting(Link::getHref).containsAll(
        Arrays.asList("/place/" + placeId + "/review/" + userId, "/place/" + placeId + "/reviews"));
    assertThat(result.getLinks().toList()).extracting(Link::getRel)
        .containsAll(Arrays.asList(IanaLinkRelations.SELF, LinkRelation.of("place-reviews")));
    verify(reviewService).getPlaceReviewDetail(placeId, userId);
    verify(assembler).toModel(reviewDetail);
  }

  @Test
  void testDeletePlace_Success() {

    Long placeId = 1L;
    Long userId = 1L;
    ResponseEntity<String> response = reviewController.deleteReview(placeId, userId);

    assertEquals(ResponseEntity.noContent().build(), response);
    verify(reviewService).deleteReview(placeId, userId);
  }
}
