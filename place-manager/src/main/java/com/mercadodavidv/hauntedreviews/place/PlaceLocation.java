package com.mercadodavidv.hauntedreviews.place;

import com.mercadodavidv.hauntedreviews.common.GeographicCoordinates;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
class PlaceLocation {

  @Id
  @GeneratedValue
  private Long id;

  private String locationDisplayName;

  @Embedded
  private GeographicCoordinates geoCoordinates;

}
