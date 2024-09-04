package at.saekenz.cinerator.model.user;

import at.saekenz.cinerator.controller.UserController;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class UserDTOAssembler implements RepresentationModelAssembler<UserDTO, EntityModel<UserDTO>> {

    @Override
    public EntityModel<UserDTO> toModel(UserDTO userDTO) {

        return EntityModel.of(userDTO,
                linkTo(methodOn(UserController.class).findById(userDTO.getId())).withSelfRel(),
                linkTo(methodOn(UserController.class).findWatchlistByUser(userDTO.getId())).withRel("watchlist"),
                linkTo(methodOn(UserController.class).findReviewsByUser(userDTO.getId())).withRel("reviews"),
                linkTo(methodOn(UserController.class).findMoviesLikedByUser(userDTO.getId())).withRel("likedMovies"));
    }
}
