package com.mercadodavidv.hauntedreviews.place;

import static com.mercadodavidv.hauntedreviews.place.PlaceController.PLACE_QUERY_MAX_PAGE_SIZE;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.afford;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

@Component
class PlaceModelAssembler<T extends PlaceModelBase> implements
    RepresentationModelAssembler<T, EntityModel<T>> {

  @Override
  @NonNull
  public EntityModel<T> toModel(@NonNull T place) {

    return EntityModel.of(place, //
        linkTo(methodOn(PlaceController.class).one(place.getId())).withSelfRel()
            .andAffordance(afford(methodOn(PlaceController.class).deletePlace(place.getId()))),
        linkTo(methodOn(PlaceController.class).all(
            Pageable.ofSize(PLACE_QUERY_MAX_PAGE_SIZE))).withRel("places"));
  }
}
