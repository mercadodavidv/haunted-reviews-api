package com.mercadodavidv.hauntedreviews.review;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@DataJpaTest
class RatingCategoryTest {

  @Autowired
  private TestEntityManager entityManager;

  @Test
  void testRatingCategoryEntity_DefaultLowScoreLabel() {

    RatingCategory category = RatingCategory.builder() //
        .title("Thrill") //
        .description("The excitement of experiencing something paranormal.") //
        .highScoreLabel("HighHighHigh") //
        .overallScoreAffected(true) //
        .build();

    entityManager.persistAndFlush(category);

    RatingCategory retrievedCategory = entityManager.find(RatingCategory.class, category.getId());

    assertNotNull(retrievedCategory);
    assertEquals("Thrill", retrievedCategory.getTitle());
    assertEquals("The excitement of experiencing something paranormal.",
        retrievedCategory.getDescription());
    assertEquals("Worst", retrievedCategory.getLowScoreLabel()); // Default
    assertEquals("HighHighHigh", retrievedCategory.getHighScoreLabel());
    assertTrue(retrievedCategory.isOverallScoreAffected());
  }

  @Test
  void testRatingCategoryEntity_DefaultHighScoreLabel() {

    RatingCategory category = RatingCategory.builder() //
        .title("Paranormal Activity") //
        .description("Amount of actual paranormal activity that one can experience here.") //
        .lowScoreLabel("LowLowLow") //
        .overallScoreAffected(true) //
        .build();

    entityManager.persistAndFlush(category);

    RatingCategory retrievedCategory = entityManager.find(RatingCategory.class, category.getId());

    assertNotNull(retrievedCategory);
    assertEquals("Paranormal Activity", retrievedCategory.getTitle());
    assertEquals("Amount of actual paranormal activity that one can experience here.",
        retrievedCategory.getDescription());
    assertEquals("LowLowLow", retrievedCategory.getLowScoreLabel());
    assertEquals("Best", retrievedCategory.getHighScoreLabel()); // Default
    assertTrue(retrievedCategory.isOverallScoreAffected());
  }
}