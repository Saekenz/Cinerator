package at.saekenz.cinerator.model.castinfo;

import at.saekenz.cinerator.controller.CastInfoController;
import at.saekenz.cinerator.controller.MovieController;
import at.saekenz.cinerator.controller.PersonController;
import at.saekenz.cinerator.controller.RoleController;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class CastInfoDTOModelAssembler implements RepresentationModelAssembler<CastInfoDTO, EntityModel<CastInfoDTO>> {

    @Override
    public EntityModel<CastInfoDTO> toModel(CastInfoDTO castInfoDTO) {
        return EntityModel.of(castInfoDTO,
                linkTo(methodOn(CastInfoController.class).findCastInfoById(castInfoDTO.getId())).withSelfRel(),
                linkTo(methodOn(MovieController.class).findById(castInfoDTO.getMovieId())).withRel("movie"),
                linkTo(methodOn(PersonController.class).findPersonById(castInfoDTO.getPersonId())).withRel("person"),
                linkTo(methodOn(RoleController.class).findRoleById(castInfoDTO.getRoleId())).withRel("role"));
    }
}
