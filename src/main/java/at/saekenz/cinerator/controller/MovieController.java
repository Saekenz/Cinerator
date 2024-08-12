package at.saekenz.cinerator.controller;

import at.saekenz.cinerator.model.movie.Movie;
import at.saekenz.cinerator.model.movie.MovieModelAssembler;
import at.saekenz.cinerator.model.movie.MovieNotFoundException;
import at.saekenz.cinerator.service.IMovieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/movies")
public class MovieController {

    @Autowired
    IMovieService movieService;

    private final MovieModelAssembler assembler;

    public MovieController(MovieModelAssembler assembler) {
        this.assembler = assembler;
    }

    @GetMapping
    public CollectionModel<EntityModel<Movie>> findAll() {
        List<EntityModel<Movie>> movies = movieService.findAll().stream()
                .map(assembler::toModel)
                .toList();

        return CollectionModel.of(movies, linkTo(methodOn(MovieController.class).findAll()).withSelfRel());
    }

    @GetMapping("/{id}")
    public EntityModel<Movie> findById(@PathVariable Long id) {
        Movie movie = movieService.findById(id).orElseThrow(() -> new MovieNotFoundException(id));

        return assembler.toModel(movie);
    }

}
