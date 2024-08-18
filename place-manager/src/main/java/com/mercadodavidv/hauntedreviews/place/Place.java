package com.mercadodavidv.hauntedreviews.place;

import com.mercadodavidv.hauntedreviews.common.AuditMetadata;
import jakarta.persistence.CascadeType;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ConstraintMode;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapKeyColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.OrderBy;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.SecondaryTable;
import java.util.List;
import java.util.Map;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Immutable;
import org.hibernate.annotations.SecondaryRow;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@EntityListeners(AuditingEntityListener.class)
@SecondaryTable(name = "place_review_info", pkJoinColumns = @PrimaryKeyJoinColumn(name = "place_id"), foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
@SecondaryRow(table = "place_review_info", owned = false)
@Getter
@Setter
@NoArgsConstructor
class Place implements PlaceModelBase {

  @Id
  @GeneratedValue
  private Long id;

  @Embedded
  private AuditMetadata auditMetadata = AuditMetadata.now();

  private String title;

  private String description;

  @OneToOne(optional = false, orphanRemoval = true, cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  private PlaceLocation location;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private PlaceAccessLevel accessLevel;

  @OneToMany
  @JoinColumn(name = "place_id", insertable = false, updatable = false)
  @OrderBy("auditMetadata.createdDate ASC")
  private List<PlaceAward> awards = List.of();

  @Immutable
  @Column(table = "place_review_info", name = "average_score", insertable = false, updatable = false)
  private float averageScore;

  @Immutable
  @Column(table = "place_review_info", name = "total_reviews", insertable = false, updatable = false)
  private int totalReviews;

  @Immutable
  @ElementCollection
  @CollectionTable(name = "place_review_rating_category_info", joinColumns = @JoinColumn(name = "place_id", insertable = false, updatable = false), foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
  @Column(name = "average_category_score", insertable = false, updatable = false)
  @MapKeyColumn(name = "rating_category_id", insertable = false, updatable = false)
  private Map<Long, Float> averageCategoryScoresByCategoryId = Map.of();

  @Builder
  private Place(String title, String description, PlaceLocation location,
      PlaceAccessLevel accessLevel) {

    this.title = title;
    this.description = description;
    this.location = location;
    this.accessLevel = accessLevel;
  }
}
