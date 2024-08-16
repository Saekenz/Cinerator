package at.saekenz.cinerator.controller;

import at.saekenz.cinerator.model.movie.Movie;
import at.saekenz.cinerator.model.movie.MovieModelAssembler;
import at.saekenz.cinerator.model.movie.MovieNotFoundException;
import at.saekenz.cinerator.service.IMovieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @RequestMapping("/test")
    public String testHtml() {
        return "movie_year";
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

    @GetMapping("/title/{title}")
    public CollectionModel<EntityModel<Movie>> findByTitle(@PathVariable String title) {
        List<EntityModel<Movie>> movies = movieService.findByTitle(title).stream()
                .map(assembler::toModel)
                .toList();

        return CollectionModel.of(movies, linkTo(methodOn(MovieController.class).findByTitle(title)).withSelfRel());
    }

    @GetMapping("/director/{director}")
    public CollectionModel<EntityModel<Movie>> findByDirector(@PathVariable String director) {
        List<EntityModel<Movie>> movies = movieService.findByDirector(director).stream()
                .map(assembler::toModel)
                .toList();

        return CollectionModel.of(movies, linkTo(methodOn(MovieController.class).findByDirector(director)).withSelfRel());
    }

    @GetMapping("/genre/{genre}")
    public CollectionModel<EntityModel<Movie>> findByGenre(@PathVariable String genre) {
        List<EntityModel<Movie>> movies = movieService.findByGenre(genre).stream()
                .map(assembler::toModel)
                .toList();

        return CollectionModel.of(movies, linkTo(methodOn(MovieController.class).findByGenre(genre)).withSelfRel());
    }

    @GetMapping("/country/{country}")
    public CollectionModel<EntityModel<Movie>> findByCountry(@PathVariable String country) {
        List<EntityModel<Movie>> movies = movieService.findByCountry(country).stream()
                .map(assembler::toModel)
                .toList();

        return CollectionModel.of(movies, linkTo(methodOn(MovieController.class).findByCountry(country)).withSelfRel());
    }

    @GetMapping("/year/{year}")
    public CollectionModel<EntityModel<Movie>> findByYearReleased(@PathVariable int year) {
        List<EntityModel<Movie>> movies = movieService.findByYear(year).stream()
                .map(assembler::toModel)
                .toList();

        return CollectionModel.of(movies, linkTo(methodOn(MovieController.class).findByYearReleased(year)).withSelfRel());
    }

    @GetMapping("/imdb_id/{imdb_id}")
    public EntityModel<Movie> findByImdbId(@PathVariable String imdb_id) {
        Movie movie = movieService.findByImdb_id(imdb_id).orElseThrow(() -> new MovieNotFoundException(imdb_id));
        return assembler.toModel(movie);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateMovie(@PathVariable Long id, @RequestBody Movie newMovie) {
        Movie updatedMovie = movieService.findById(id).map(
                movie -> {
                    movie.setTitle(newMovie.getTitle());
                    movie.setDirector(newMovie.getDirector());
                    movie.setRelease_date(newMovie.getRelease_date());
                    movie.setGenre(newMovie.getGenre());
                    movie.setCountry(newMovie.getCountry());
                    movie.setImdb_id(newMovie.getImdb_id());
                    movie.setReviews(newMovie.getReviews());
                    return movieService.save(movie);
                })
                .orElseGet(() -> movieService.save(newMovie));
            EntityModel<Movie> entityModel = assembler.toModel(updatedMovie);

            return ResponseEntity
                    .created(entityModel.getRequiredLink(IanaLinkRelations.SELF).toUri())
                    .body(entityModel);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteMovie(@PathVariable Long id) {
        movieService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping()
    public ResponseEntity<?> createMovie(@RequestBody Movie newMovie) {
        EntityModel<Movie> entityModel = assembler.toModel(movieService.save(newMovie));

        return ResponseEntity
                .created(entityModel.getRequiredLink(IanaLinkRelations.SELF).toUri())
                .body(entityModel);
    }

}
