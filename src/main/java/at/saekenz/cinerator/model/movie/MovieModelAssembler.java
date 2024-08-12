package at.saekenz.cinerator.model.movie;

import at.saekenz.cinerator.controller.MovieController;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class MovieModelAssembler implements RepresentationModelAssembler<Movie, EntityModel<Movie>> {

    @Override
    public EntityModel<Movie> toModel(Movie movie) {

        return EntityModel.of(movie,
                linkTo(methodOn(MovieController.class).findById(movie.getMovie_id())).withSelfRel(),
                linkTo(methodOn(MovieController.class).findByDirector(movie.getDirector())).withRel("director"),
                linkTo(methodOn(MovieController.class).findByGenre(movie.getGenre())).withRel("genre"),
                linkTo(methodOn(MovieController.class).findByCountry(movie.getCountry())).withRel("country"),
                linkTo(methodOn(MovieController.class).findByYearReleased(movie.getReleaseYear())).withRel("year"),
                linkTo(methodOn(MovieController.class).findAll()).withRel("movies"));
    }
}
