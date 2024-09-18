package at.saekenz.cinerator.util;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class ResponseBuilderService {

    /**
     *
     * @param entityModel the {@link EntityModel} containing the resource
     *  and its associated links, including the self link
     * @return a {@link ResponseEntity} with a 204 No Content status and
     *  a Location header set to the self link of the entity
     */
    public <T> ResponseEntity<T> buildNoContentResponseWithLocation(EntityModel<?> entityModel) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Location",entityModel.getRequiredLink(IanaLinkRelations.SELF).toUri().toString());

        return ResponseEntity.status(HttpStatus.NO_CONTENT).headers(headers).build();
    }

    /**
     *
     * @param entityModel the {@link EntityModel} containing the resource
     * and its associated links, including the self link
     * @return a {@link ResponseEntity} with a 201 Created status and
     * a body containing the created resource
     */
    public <T> ResponseEntity<EntityModel<T>> buildCreatedResponseWithBody(EntityModel<T> entityModel) {
        return ResponseEntity
                .created(entityModel.getRequiredLink(IanaLinkRelations.SELF).toUri())
                .body(entityModel);
    }
}
