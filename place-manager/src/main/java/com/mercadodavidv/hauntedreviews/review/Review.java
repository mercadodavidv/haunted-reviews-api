package com.mercadodavidv.hauntedreviews.review;

import com.mercadodavidv.hauntedreviews.common.AuditMetadata;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Embedded;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapKeyColumn;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Transient;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"place_id", "user_id"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
class Review implements ReviewModelBase {

  @EmbeddedId
  private ReviewKey id;

  @Embedded
  private AuditMetadata auditMetadata = AuditMetadata.now();

  private String title;

  private String body;

  @ElementCollection
  @CollectionTable( //
      name = "review_rating", //
      joinColumns = { //
          @JoinColumn(name = "place_id", referencedColumnName = "place_id"), //
          @JoinColumn(name = "user_id", referencedColumnName = "user_id") //
      } //
  )
  @Column(name = "score")
  @MapKeyColumn(name = "rating_category_id")
  private Map<Long, Short> scoresByRatingCategoryId;

  @Override
  @Transient
  public Long getPlaceId() {
    return this.id.getPlaceId();
  }

  @Override
  @Transient
  public Long getUserId() {
    return this.id.getUserId();
  }
}
