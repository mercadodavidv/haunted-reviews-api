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
@Table(name = "place_award_count")
@IdClass(PlaceAwardCountKey.class)
@View(query = """
    SELECT
      place_id,
      award_type_id,
      COUNT(*) AS total_count
    FROM
      place_award
    GROUP BY
      award_type_id,
      place_id
    """)
@Synchronize("place")
@Getter
public class PlaceAwardCount {

  @Id
  @Column(name = "place_id")
  private Long placeId;

  @Id
  @Column(name = "award_type_id")
  private Long awardTypeId;

  @Column(name = "total_count")
  private Long totalCount;

}
