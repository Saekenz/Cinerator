package at.saekenz.cinerator.controller;

import at.saekenz.cinerator.model.actor.Actor;
import at.saekenz.cinerator.model.actor.ActorModelAssembler;
import at.saekenz.cinerator.model.actor.ActorNotFoundException;
import at.saekenz.cinerator.model.movie.EMovieSearchParam;
import at.saekenz.cinerator.model.movie.Movie;
import at.saekenz.cinerator.model.movie.MovieModelAssembler;
import at.saekenz.cinerator.model.movie.MovieNotFoundException;
import at.saekenz.cinerator.model.review.Review;
import at.saekenz.cinerator.model.review.ReviewModelAssembler;
import at.saekenz.cinerator.service.IActorService;
import at.saekenz.cinerator.service.IMovieService;
import at.saekenz.cinerator.service.IReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.hateoas.Link;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/movies")
public class MovieController {

    @Autowired
    IMovieService movieService;

    @Autowired
    IActorService actorService;

    @Autowired
    IReviewService reviewService;

    private final MovieModelAssembler movieAssembler;
    private final ReviewModelAssembler reviewAssembler;
    private final ActorModelAssembler actorAssembler;

    public MovieController(MovieModelAssembler movieAssembler, ReviewModelAssembler reviewAssembler,
                           ActorModelAssembler actorAssembler) {
        this.movieAssembler = movieAssembler;
        this.reviewAssembler = reviewAssembler;
        this.actorAssembler = actorAssembler;
    }

    @GetMapping
    public ResponseEntity<CollectionModel<EntityModel<Movie>>> findAll() {
        List<Movie> movies = movieService.findAll();

        if (movies.isEmpty()) { return ResponseEntity.ok().build(); }

        List<EntityModel<Movie>> movieModels = movies.stream()
                .map(movieAssembler::toModel)
                .toList();

        CollectionModel<EntityModel<Movie>> collectionModel = CollectionModel.of(movieModels,
                linkTo(methodOn(MovieController.class).findAll()).withSelfRel());

        return ResponseEntity.ok(collectionModel);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<Movie>> findById(@PathVariable Long id) {
        Movie movie = movieService.findById(id).orElseThrow(() -> new MovieNotFoundException(id));
        return ResponseEntity.ok(movieAssembler.toModel(movie));
    }

    @GetMapping("/title/{title}")
    public ResponseEntity<CollectionModel<EntityModel<Movie>>> findByTitle(@PathVariable String title) {
        List<Movie> movies = movieService.findByTitle(title);

        if (movies.isEmpty()) { throw new MovieNotFoundException(EMovieSearchParam.TITLE, title); }

        CollectionModel<EntityModel<Movie>> collectionModel = createCollectionModelFromList(movies,
                linkTo(methodOn(MovieController.class).findByTitle(title)).withSelfRel());

        return ResponseEntity.ok(collectionModel);
    }

    @GetMapping("/director/{director}")
    public ResponseEntity<CollectionModel<EntityModel<Movie>>> findByDirector(@PathVariable String director) {
        List<Movie> movies = movieService.findByDirector(director);

        if (movies.isEmpty()) { throw new MovieNotFoundException(EMovieSearchParam.DIRECTOR, director); }

        CollectionModel<EntityModel<Movie>> collectionModel = createCollectionModelFromList(movies,
                linkTo(methodOn(MovieController.class).findByDirector(director)).withSelfRel());

        return ResponseEntity.ok(collectionModel);
    }

    @GetMapping("/genre/{genre}")
    public ResponseEntity<CollectionModel<EntityModel<Movie>>> findByGenre(@PathVariable String genre) {
        List<Movie> movies = movieService.findByGenre(genre);

        if (movies.isEmpty()) {
            throw new MovieNotFoundException(EMovieSearchParam.GENRE, genre);
        }

        CollectionModel<EntityModel<Movie>> collectionModel = createCollectionModelFromList(movies,
                linkTo(methodOn(MovieController.class).findByGenre(genre)).withSelfRel());

        return ResponseEntity.ok(collectionModel);
    }

    @GetMapping("/country/{country}")
    public ResponseEntity<CollectionModel<EntityModel<Movie>>> findByCountry(@PathVariable String country) {
        List<Movie> movies = movieService.findByCountry(country);

        if (movies.isEmpty()) {
            throw new MovieNotFoundException(EMovieSearchParam.COUNTRY, country);
        }

        CollectionModel<EntityModel<Movie>> collectionModel = createCollectionModelFromList(movies,
                linkTo(methodOn(MovieController.class).findByCountry(country)).withSelfRel());

        return ResponseEntity.ok(collectionModel);
    }

    @GetMapping("/year/{year}")
    public ResponseEntity<CollectionModel<EntityModel<Movie>>> findByYearReleased(@PathVariable int year) {
        List<Movie> movies = movieService.findByYear(year);

        if (movies.isEmpty()) {
            throw new MovieNotFoundException(EMovieSearchParam.YEAR, year+"");
        }

        CollectionModel<EntityModel<Movie>> collectionModel = createCollectionModelFromList(movies,
                linkTo(methodOn(MovieController.class).findByYearReleased(year)).withSelfRel());

        return ResponseEntity.ok(collectionModel);
    }

    @GetMapping("/imdb_id/{imdb_id}")
    public ResponseEntity<EntityModel<Movie>> findByImdbId(@PathVariable String imdb_id) {
        Movie movie = movieService.findByImdb_id(imdb_id).orElseThrow(() -> new MovieNotFoundException(imdb_id));
        return ResponseEntity.ok(movieAssembler.toModel(movie));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateMovie(@PathVariable Long id, @RequestBody Movie newMovie) {
        Movie updatedMovie = movieService.findById(id).map(
                movie -> {
                    movie.setTitle(newMovie.getTitle());
                    movie.setDirector(newMovie.getDirector());
                    movie.setRelease_date(newMovie.getRelease_date());
                    movie.setRuntime(newMovie.getRuntime());
                    movie.setGenre(newMovie.getGenre());
                    movie.setCountry(newMovie.getCountry());
                    movie.setImdb_id(newMovie.getImdb_id());
                    movie.setPoster_url(newMovie.getPoster_url());
                    movie.setReviews(newMovie.getReviews());
                    return movieService.save(movie);
                })
                .orElseGet(() -> movieService.save(newMovie));
            EntityModel<Movie> entityModel = movieAssembler.toModel(updatedMovie);

            return ResponseEntity
                    .created(entityModel.getRequiredLink(IanaLinkRelations.SELF).toUri())
                    .body(entityModel);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteMovie(@PathVariable Long id) {
        if (movieService.findById(id).isPresent()) {
            movieService.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        else {
            throw new MovieNotFoundException(id);
        }
    }

    @PostMapping()
    public ResponseEntity<?> createMovie(@RequestBody Movie newMovie) {
        EntityModel<Movie> entityModel = movieAssembler.toModel(movieService.save(newMovie));

        return ResponseEntity
                .created(entityModel.getRequiredLink(IanaLinkRelations.SELF).toUri())
                .body(entityModel);
    }

    private CollectionModel<EntityModel<Movie>> createCollectionModelFromList(
            List<Movie> movies, Link selfLink) {

        List<EntityModel<Movie>> movieModels = movies.stream()
                .map(movieAssembler::toModel)
                .toList();

        return CollectionModel.of(movieModels,selfLink);
    }

    @GetMapping("/{id}/reviews")
    public ResponseEntity<CollectionModel<EntityModel<Review>>> findReviewsById(@PathVariable Long id) {
        Movie movie = movieService.findById(id).orElseThrow(() -> new MovieNotFoundException(id));

        List<EntityModel<Review>> reviews = movie.getReviews().stream()
                .map(reviewAssembler::toModel)
                .toList();

        CollectionModel<EntityModel<Review>> collectionModel = CollectionModel.of(reviews,
                linkTo(methodOn(MovieController.class).findReviewsById(id)).withSelfRel());

        return ResponseEntity.ok(collectionModel);
    }

    @GetMapping("/{id}/actors")
    public ResponseEntity<CollectionModel<EntityModel<Actor>>> findActorsById(@PathVariable Long id) {
        Movie movie = movieService.findById(id).orElseThrow(() -> new MovieNotFoundException(id));

        List<Actor> actors = movie.getActors();
        if (actors.isEmpty()) { return ResponseEntity.ok().build(); }

        List<EntityModel<Actor>> actorModels = actors.stream()
                .map(actorAssembler::toModel)
                .toList();

        CollectionModel<EntityModel<Actor>> collectionModel = CollectionModel.of(actorModels,
                linkTo(methodOn(MovieController.class).findActorsById(id)).withSelfRel());

        return ResponseEntity.ok(collectionModel);
    }

    @GetMapping("/{movie_id}/actors/{actor_id}")
    public ResponseEntity<EntityModel<Actor>> findActorById(@PathVariable Long movie_id, @PathVariable Long actor_id) {
        Movie movie = movieService.findById(movie_id).orElseThrow(() -> new MovieNotFoundException(movie_id));
        Actor actor = movie.getActors().stream()
                .filter(a -> Objects.equals(a.getActor_id(), actor_id)).findFirst().orElseThrow(() -> new ActorNotFoundException(actor_id));

        return ResponseEntity.ok(actorAssembler.toModel(actor));

    }

    @PostMapping("/{movie_id}/actors")
    public ResponseEntity<EntityModel<Movie>> addActorToMovie(@PathVariable Long movie_id, @RequestBody Long actor_id) {
        Movie movie = movieService.findById(movie_id).orElseThrow(() -> new MovieNotFoundException(movie_id));
        Actor actor = actorService.findById(actor_id).orElseThrow(() -> new ActorNotFoundException(actor_id));

        movie.getActors().add(actor);
        movieService.save(movie);

        return ResponseEntity.ok(movieAssembler.toModel(movie));
    }

    @DeleteMapping("/{movie_id}/actors/{actor_id}")
    public ResponseEntity<EntityModel<Movie>> removeActorFromMovie(@PathVariable Long movie_id, @PathVariable Long actor_id) {
        Movie movie = movieService.findById(movie_id).orElseThrow(() -> new MovieNotFoundException(movie_id));

        movie.removeActor(actor_id);
        movieService.save(movie);

        return ResponseEntity.ok(movieAssembler.toModel(movie));
    }

    @PostMapping("/{id}/reviews")
    public ResponseEntity<?> addReviewToMovie(@PathVariable Long id, @RequestBody Review review) {
        movieService.findById(id).orElseThrow(() -> new MovieNotFoundException(id));
        EntityModel<Review> entityModel = reviewAssembler.toModel(reviewService.save(review));

        return ResponseEntity
                .created(entityModel.getRequiredLink(IanaLinkRelations.SELF).toUri())
                .body(entityModel);
    }

}
