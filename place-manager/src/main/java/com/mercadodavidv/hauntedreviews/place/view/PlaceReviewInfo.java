package com.mercadodavidv.hauntedreviews.place.view;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import org.hibernate.annotations.Immutable;
import org.hibernate.annotations.Synchronize;
import org.hibernate.annotations.View;

@Immutable
@Entity
@Table(name = "place_review_info")
@View(query = """
    SELECT
      place.id AS place_id,
      COALESCE(AVG(rating.score), 0) AS average_score,
      COUNT(DISTINCT rating.user_id) AS total_reviews
    FROM
      place
      LEFT JOIN (
        review_rating rating
        INNER JOIN rating_category category ON category.id = rating.rating_category_id
        AND category.affects_overall_score = true
      ) rating ON place.id = rating.place_id
      LEFT JOIN review ON place.id = review.place_id
      AND review.user_id = rating.user_id
    GROUP BY
      place.id
    """)
@Synchronize("place")
@Getter
public class PlaceReviewInfo {

  @Id
  @Column(name = "place_id")
  private Long placeId;

  @Column(name = "average_score")
  private float averageScore;

  @Column(name = "total_reviews")
  private int totalReviews;

}
