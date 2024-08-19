package com.mercadodavidv.hauntedreviews.review;

import static org.assertj.core.api.Assertions.assertThat;
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

@ExtendWith(MockitoExtension.class)
class RatingCategoryControllerTest {

  @Mock
  private ReviewService reviewService;

  @Spy
  private RatingCategoryModelAssembler assembler;

  @InjectMocks
  private RatingCategoryController ratingCategoryController;

  @Test
  void testAll_Success() {

    Long categoryId1 = 1L;
    Long categoryId2 = 2L;
    RatingCategory category1 = mock(RatingCategory.class);
    RatingCategory category2 = mock(RatingCategory.class);

    when(category1.getId()).thenReturn(categoryId1);
    when(category2.getId()).thenReturn(categoryId2);
    when(reviewService.getAllRatingCategories()).thenReturn(List.of(category1, category2));
    CollectionModel<EntityModel<RatingCategory>> result = ratingCategoryController.all();

    // Collection model
    assertThat(result.getLink(IanaLinkRelations.SELF)).get().extracting(Link::getHref)
        .isEqualTo("/rating-categories");

    // Rating category item 1
    assertThat(result.getContent().stream().toList().get(0).getLink(IanaLinkRelations.SELF)).get()
        .extracting(Link::getHref).isEqualTo("/rating-category/" + categoryId1);
    assertThat(result.getContent().stream().toList().get(0).getLink("rating-categories")).get()
        .extracting(Link::getHref).isEqualTo("/rating-categories");

    // Rating category item 2
    assertThat(result.getContent().stream().toList().get(1).getLink(IanaLinkRelations.SELF)).get()
        .extracting(Link::getHref).isEqualTo("/rating-category/" + categoryId2);
    assertThat(result.getContent().stream().toList().get(1).getLink("rating-categories")).get()
        .extracting(Link::getHref).isEqualTo("/rating-categories");

    verify(reviewService).getAllRatingCategories();
    verify(assembler).toModel(category1);
    verify(assembler).toModel(category2);
  }

  @Test
  void testOne_Success() {

    Long categoryId = 1L;
    RatingCategory category = mock(RatingCategory.class);

    when(category.getId()).thenReturn(categoryId);
    when(reviewService.getRatingCategory(categoryId)).thenReturn(category);

    EntityModel<RatingCategory> result = ratingCategoryController.one(categoryId);

    assertThat(result).extracting(EntityModel::getContent).isEqualTo(category);
    assertThat(result.getLink(IanaLinkRelations.SELF)).get().extracting(Link::getHref)
        .isEqualTo("/rating-category/" + categoryId);
    assertThat(result.getLink("rating-categories")).get().extracting(Link::getHref)
        .isEqualTo("/rating-categories");
    verify(reviewService).getRatingCategory(categoryId);
    verify(assembler).toModel(category);
  }
}