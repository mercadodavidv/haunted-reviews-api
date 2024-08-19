package com.mercadodavidv.hauntedreviews.review;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
class RatingCategoryAdminControllerTest {

  @Mock
  private ReviewService reviewService;

  @Spy
  private RatingCategoryModelAssembler assembler;

  @InjectMocks
  private RatingCategoryAdminController ratingCategoryAdminController;

  @Test
  void testNewRatingCategory_Success() {

    Long categoryId = 1L;
    RatingCategoryInput categoryInput = mock(RatingCategoryInput.class);
    RatingCategory category = mock(RatingCategory.class);

    when(category.getId()).thenReturn(categoryId);
    when(reviewService.saveRatingCategory(categoryInput)).thenReturn(category);

    ResponseEntity<EntityModel<RatingCategory>> response = ratingCategoryAdminController.newRatingCategory(
        categoryInput);

    assertEquals(HttpStatus.CREATED, response.getStatusCode());
    assertNotNull(response.getBody());
    assertThat(response.getBody().getLink(IanaLinkRelations.SELF)).get().extracting(Link::getHref)
        .isEqualTo("/rating-category/" + categoryId);
    assertThat(response.getBody().getLink("rating-categories")).get().extracting(Link::getHref)
        .isEqualTo("/rating-categories");
    verify(reviewService).saveRatingCategory(categoryInput);
    verify(assembler).toModel(category);
  }

  @Test
  void testMultipleNewRatingCategories_Success() {

    Long categoryId1 = 1L;
    Long categoryId2 = 2L;
    RatingCategoryInput categoryInput1 = mock(RatingCategoryInput.class);
    RatingCategoryInput categoryInput2 = mock(RatingCategoryInput.class);
    List<RatingCategoryInput> categoryInputs = List.of(categoryInput1, categoryInput2);
    RatingCategory category1 = mock(RatingCategory.class);
    RatingCategory category2 = mock(RatingCategory.class);

    when(category1.getId()).thenReturn(categoryId1);
    when(category2.getId()).thenReturn(categoryId2);
    when(reviewService.saveRatingCategories(categoryInputs)).thenReturn(
        List.of(category1, category2));

    ResponseEntity<CollectionModel<EntityModel<RatingCategory>>> response = ratingCategoryAdminController.newRatingCategories(
        categoryInputs);

    assertEquals(HttpStatus.CREATED, response.getStatusCode());
    assertNotNull(response.getBody());

    // Collection model
    assertThat(response.getBody().getLink(IanaLinkRelations.SELF)).get().extracting(Link::getHref)
        .isEqualTo("/rating-categories");

    // Rating category item 1
    assertThat(response.getBody().getContent().stream().toList().get(0)
        .getLink(IanaLinkRelations.SELF)).get().extracting(Link::getHref)
        .isEqualTo("/rating-category/" + categoryId1);
    assertThat(
        response.getBody().getContent().stream().toList().get(0).getLink("rating-categories")).get()
        .extracting(Link::getHref).isEqualTo("/rating-categories");

    // Rating category item 2
    assertThat(response.getBody().getContent().stream().toList().get(1)
        .getLink(IanaLinkRelations.SELF)).get().extracting(Link::getHref)
        .isEqualTo("/rating-category/" + categoryId2);
    assertThat(
        response.getBody().getContent().stream().toList().get(1).getLink("rating-categories")).get()
        .extracting(Link::getHref).isEqualTo("/rating-categories");

    verify(reviewService).saveRatingCategories(categoryInputs);
    verify(assembler).toModel(category1);
    verify(assembler).toModel(category2);
  }
}