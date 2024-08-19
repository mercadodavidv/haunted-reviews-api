package com.mercadodavidv.hauntedreviews.place.view;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import lombok.Getter;
import org.hibernate.annotations.Immutable;
import org.hibernate.annotations.Synchronize;
import org.hibernate.annotations.View;

@Immutable
@Entity
@Table(name = "place_review_rating_category_info")
@IdClass(PlaceReviewRatingCategoryInfoKey.class)
@View(query = """
    SELECT
      catxjoin.place_id AS place_id,
      catxjoin.rating_category_id AS rating_category_id,
      COALESCE(AVG(rating.score), 0) AS average_category_score
    FROM
      (
        SELECT
          place.id AS place_id,
          category.id AS rating_category_id,
        FROM
          place,
          rating_category category
      ) catxjoin
      LEFT JOIN review_rating rating ON catxjoin.place_id = rating.place_id
      AND catxjoin.rating_category_id = rating.rating_category_id
    GROUP BY
      catxjoin.place_id,
      catxjoin.rating_category_id
    """)
@Synchronize("place")
@Getter
public class PlaceReviewRatingCategoryInfo {

  @Id
  @Column(name = "place_id")
  private Long placeId;

  @Id
  @Column(name = "rating_category_id")
  private Long ratingCategoryId;

  @Column(name = "average_category_score")
  private float averageCategoryScore;

}
