package at.saekenz.cinerator.model.genre;

import at.saekenz.cinerator.controller.GenreController;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class GenreDTOModelAssembler implements RepresentationModelAssembler<GenreDTO, EntityModel<GenreDTO>> {

    @Override
    public EntityModel<GenreDTO> toModel(GenreDTO genre) {
        return EntityModel.of(genre,
                linkTo(methodOn(GenreController.class).findGenreById(genre.id())).withSelfRel());
    }
}
