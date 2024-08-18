package com.mercadodavidv.hauntedreviews.place;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.mercadodavidv.hauntedreviews.place.projection.PlaceDetail;
import com.mercadodavidv.hauntedreviews.place.projection.PlaceSearchResult;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.LinkRelation;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.util.UriComponentsBuilder;

@ExtendWith(MockitoExtension.class)
class PlaceControllerTest {

  @Mock
  private PlaceService placeService;

  @Spy
  private PlaceModelAssembler<PlaceModelBase> assembler;

  @Spy
  private PagedResourcesAssembler<PlaceModelBase> pagedResourcesAssembler = new PagedResourcesAssembler<>(
      null, UriComponentsBuilder.fromUriString("/places").build());

  @InjectMocks
  private PlaceController placeController;

  @Test
  void testAll_Success() {

    Pageable pageable = PageRequest.of(0, 10);
    PlaceSearchResult place = mock(PlaceSearchResult.class);
    Page<PlaceSearchResult> placePage = new PageImpl<>(List.of(place));

    when(placeService.searchPublicPlaces(pageable)).thenReturn(placePage);

    PagedModel<EntityModel<PlaceModelBase>> result = placeController.all(pageable);

    assertNotNull(result.getContent());
    assertThat(result.getLinks().toList()).extracting(Link::getHref).containsOnly("/places");
    assertThat(result.getLinks().toList()).extracting(Link::getRel)
        .containsOnly(IanaLinkRelations.SELF);
    assertThat(result.getContent()).extracting(EntityModel::getContent).containsOnly(place);
    verify(placeService).searchPublicPlaces(pageable);
    verify(pagedResourcesAssembler).toModel(any(),
        Mockito.<PlaceModelAssembler<PlaceModelBase>>any());
  }

  @Test
  void testAll_PageSizeExceedsLimit() {

    Pageable oversizedPageable = PageRequest.of(0, 25);

    assertThrows(PlaceSearchException.class, () -> placeController.all(oversizedPageable));
  }

  @Test
  void testNewPlace_Success() {

    PlaceInput placeInput = mock(PlaceInput.class);
    PlaceDetail placeDetail = mock(PlaceDetail.class);

    when(placeDetail.getId()).thenReturn(1L);
    when(placeService.savePlace(placeInput)).thenReturn(placeDetail);

    ResponseEntity<EntityModel<PlaceModelBase>> response = placeController.newPlace(placeInput);

    assertEquals(HttpStatus.CREATED, response.getStatusCode());
    assertNotNull(response.getBody());
    assertThat(response.getBody().getLinks().toList()).extracting(Link::getHref)
        .containsAll(Arrays.asList("/place/1", "/places"));
    assertThat(response.getBody().getLinks().toList()).extracting(Link::getRel)
        .containsAll(Arrays.asList(IanaLinkRelations.SELF, LinkRelation.of("places")));
    verify(placeService).savePlace(placeInput);
    verify(assembler).toModel(placeDetail);
  }

  @Test
  void testOne_Success() {

    PlaceDetail placeDetail = mock(PlaceDetail.class);

    when(placeDetail.getId()).thenReturn(1L);
    when(placeService.getPlaceDetail(anyLong())).thenReturn(placeDetail);

    EntityModel<PlaceModelBase> result = placeController.one(1L);

    assertThat(result).extracting(EntityModel::getContent).isEqualTo(placeDetail);
    assertThat(result.getLinks().toList()).extracting(Link::getHref)
        .containsAll(Arrays.asList("/place/1", "/places"));
    assertThat(result.getLinks().toList()).extracting(Link::getRel)
        .containsAll(Arrays.asList(IanaLinkRelations.SELF, LinkRelation.of("places")));
    verify(placeService).getPlaceDetail(1L);
    verify(assembler).toModel(placeDetail);
  }

  @Test
  void testDeletePlace_Success() {

    ResponseEntity<String> response = placeController.deletePlace(1L);

    assertEquals(ResponseEntity.noContent().build(), response);
    verify(placeService).deletePlace(1L);
  }
}
