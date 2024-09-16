package at.saekenz.cinerator.model.movie;

import at.saekenz.cinerator.controller.MovieController;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class MovieDTOModelAssembler implements RepresentationModelAssembler<MovieDTO, EntityModel<MovieDTO>> {

    @Override
    public EntityModel<MovieDTO> toModel(MovieDTO movieDTO) {
        return EntityModel.of(movieDTO,
                linkTo(methodOn(MovieController.class).findById(movieDTO.getId())).withSelfRel(),
                linkTo(methodOn(MovieController.class).findReviewsByMovie(movieDTO.getId())).withRel("reviews"),
                linkTo(methodOn(MovieController.class).findActorsByMovie(movieDTO.getId())).withRel("actors"),
                linkTo(methodOn(MovieController.class).findGenresByMovie(movieDTO.getId())).withRel("genres"),
                linkTo(methodOn(MovieController.class).findCountriesByMovie(movieDTO.getId())).withRel("countries"));
    }
}
