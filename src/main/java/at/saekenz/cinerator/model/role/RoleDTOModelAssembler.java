package at.saekenz.cinerator.model.role;

import at.saekenz.cinerator.controller.RoleController;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class RoleDTOModelAssembler implements RepresentationModelAssembler<RoleDTO, EntityModel<RoleDTO>> {

    @Override
    public EntityModel<RoleDTO> toModel(RoleDTO roleDTO) {
        return EntityModel.of(roleDTO,
                linkTo(methodOn(RoleController.class).findRoleById(roleDTO.id())).withSelfRel());
    }
}
