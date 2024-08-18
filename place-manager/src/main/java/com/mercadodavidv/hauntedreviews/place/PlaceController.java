package com.mercadodavidv.hauntedreviews.place;

import com.mercadodavidv.hauntedreviews.place.projection.PlaceDetail;
import com.mercadodavidv.hauntedreviews.place.projection.PlaceSearchResult;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
class PlaceController {

  static final int PLACE_QUERY_MAX_PAGE_SIZE = 20;

  private final PlaceService placeService;

  private final PlaceModelAssembler<PlaceModelBase> assembler;

  private final PagedResourcesAssembler<PlaceModelBase> pagedResourcesAssembler;

  PlaceController(PlaceService placeService, //
      PlaceModelAssembler<PlaceModelBase> assembler, //
      PagedResourcesAssembler<PlaceModelBase> pagedResourcesAssembler) {

    this.placeService = placeService;
    this.assembler = assembler;
    this.pagedResourcesAssembler = pagedResourcesAssembler;
  }

  @GetMapping("/places")
  PagedModel<EntityModel<PlaceModelBase>> all(Pageable pageable) {

    if (pageable.getPageSize() > PLACE_QUERY_MAX_PAGE_SIZE) {
      throw new PlaceSearchException("Size must be less than or equal to 20.");
    }
    Page<PlaceSearchResult> placePage = placeService.searchPublicPlaces(pageable);

    return pagedResourcesAssembler.toModel(placePage.map(PlaceModelBase.class::cast), assembler);
  }

  @PostMapping("/place")
  ResponseEntity<EntityModel<PlaceModelBase>> newPlace(@RequestBody PlaceInput newPlace) {

    PlaceDetail place = placeService.savePlace(newPlace);

    EntityModel<PlaceModelBase> entityModel = assembler.toModel(place);
    return ResponseEntity.created(entityModel.getRequiredLink(IanaLinkRelations.SELF).toUri())
        .body(entityModel);
  }

  @GetMapping("/place/{placeId}")
  EntityModel<PlaceModelBase> one(@PathVariable Long placeId) {

    PlaceDetail place = placeService.getPlaceDetail(placeId);

    return assembler.toModel(place);
  }

  @DeleteMapping("/place/{placeId}")
  ResponseEntity<String> deletePlace(@PathVariable Long placeId) {

    placeService.deletePlace(placeId);

    return ResponseEntity.noContent().build();
  }
}
