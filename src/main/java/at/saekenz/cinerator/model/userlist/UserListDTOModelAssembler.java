package at.saekenz.cinerator.model.userlist;

import at.saekenz.cinerator.controller.UserListController;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class UserListDTOModelAssembler implements RepresentationModelAssembler<UserListDTO, EntityModel<UserListDTO>> {

    @Override
    public EntityModel<UserListDTO> toModel(UserListDTO userListDTO) {
        Long userId = userListDTO.getUserId();

        return EntityModel.of(userListDTO,
                linkTo(methodOn(UserListController.class).findById(userId)).withSelfRel(),
                linkTo(methodOn(UserListController.class).findUserByUserList(userId)).withRel("user"),
                linkTo(methodOn(UserListController.class).findMoviesByUserList(userId)).withRel("movies"));
    }

}
