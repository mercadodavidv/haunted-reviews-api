package com.mercadodavidv.hauntedreviews.review;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"title"}))
@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
class RatingCategory {

  static final String DEFAULT_LOW_SCORE_LABEL = "Worst";

  static final String DEFAULT_HIGH_SCORE_LABEL = "Best";

  @Id
  @GeneratedValue
  private Long id;

  private String title;

  private String description;

  private String lowScoreLabel;

  private String highScoreLabel;

  private boolean overallScoreAffected; // e.g. some might prefer danger to safety, so it should not factor into score

  @PrePersist @PreUpdate
  void applyDefaults() {
    if (this.lowScoreLabel == null) {
      this.lowScoreLabel = DEFAULT_LOW_SCORE_LABEL;
    }
    if (this.highScoreLabel == null) {
      this.highScoreLabel = DEFAULT_HIGH_SCORE_LABEL;
    }
  }
}
