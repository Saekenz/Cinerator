package at.saekenz.cinerator.controller;

import at.saekenz.cinerator.model.actor.Actor;
import at.saekenz.cinerator.model.actor.ActorModelAssembler;
import at.saekenz.cinerator.model.actor.ActorNotFoundException;
import at.saekenz.cinerator.model.movie.EMovieSearchParam;
import at.saekenz.cinerator.model.movie.Movie;
import at.saekenz.cinerator.model.movie.MovieModelAssembler;
import at.saekenz.cinerator.model.movie.MovieNotFoundException;
import at.saekenz.cinerator.model.review.*;
import at.saekenz.cinerator.model.user.User;
import at.saekenz.cinerator.model.user.UserNotFoundException;
import at.saekenz.cinerator.service.IActorService;
import at.saekenz.cinerator.service.IMovieService;
import at.saekenz.cinerator.service.IReviewService;
import at.saekenz.cinerator.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.web.HateoasPageableHandlerMethodArgumentResolver;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.*;
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

    @Autowired
    IUserService userService;

    private final MovieModelAssembler movieAssembler;
    private final ReviewModelAssembler reviewAssembler;
    private final ActorModelAssembler actorAssembler;
    private final ReviewMapper reviewMapper;

    // Workaround since using the @Autowired annotation causes Intellij to report an error
    private final PagedResourcesAssembler<Movie> pagedResourcesAssembler = new PagedResourcesAssembler<>(
            new HateoasPageableHandlerMethodArgumentResolver(), null);

    public MovieController(MovieModelAssembler movieAssembler, ReviewModelAssembler reviewAssembler,
                           ActorModelAssembler actorAssembler) {
        this.movieAssembler = movieAssembler;
        this.reviewAssembler = reviewAssembler;
        this.actorAssembler = actorAssembler;
        this.reviewMapper = new ReviewMapper();
    }

    /**
     *
     * @return HTTP code 200 and a list of {@link Movie} objects currently stored in the database
     */
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

    /**
     *
     * @param id number of the {@link Movie} that is to be retrieved
     * @return HTTP code 200 and the {@link Movie} object if it was found. HTTP code 404 otherwise
     */
    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<Movie>> findById(@PathVariable Long id) {
        Movie movie = movieService.findById(id).orElseThrow(() -> new MovieNotFoundException(id));
        return ResponseEntity.ok(movieAssembler.toModel(movie));
    }

    /**
     *
     * @param newMovie information about the movie that is to be added to the database
     * @return HTTP code 201 and the {@link Movie} object that was created
     */
    @PostMapping()
    public ResponseEntity<?> createMovie(@RequestBody Movie newMovie) {
        EntityModel<Movie> entityModel = movieAssembler.toModel(movieService.save(newMovie));

        return ResponseEntity
                .created(entityModel.getRequiredLink(IanaLinkRelations.SELF).toUri())
                .body(entityModel);
    }

    /**
     *
     * @param id number of the {@link Movie} that is to be updated (or added if the number does not exist in the database yet)
     * @param newMovie information about the to be updated/added {@link Movie} object
     * @return HTTP code 201 and the updated {@link Movie} object
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateMovie(@PathVariable Long id, @RequestBody Movie newMovie) {
        Movie updatedMovie = movieService.findById(id).map(
                        movie -> {
                            movie.setTitle(newMovie.getTitle());
                            movie.setDirector(newMovie.getDirector());
                            movie.setReleaseDate(newMovie.getReleaseDate());
                            movie.setRuntime(newMovie.getRuntime());
                            movie.setGenre(newMovie.getGenre());
                            movie.setCountry(newMovie.getCountry());
                            movie.setImdbId(newMovie.getImdbId());
                            movie.setPosterUrl(newMovie.getPosterUrl());
                            movie.setReviews(newMovie.getReviews());
                            return movieService.save(movie);
                        })
                .orElseGet(() -> movieService.save(newMovie));
        EntityModel<Movie> entityModel = movieAssembler.toModel(updatedMovie);

        return ResponseEntity
                .created(entityModel.getRequiredLink(IanaLinkRelations.SELF).toUri())
                .body(entityModel);
    }

    /**
     *
     * @param id number of {@link Movie} that is to be removed from the database
     * @return HTTP code 204 if {@link Movie} was deleted. HTTP code 404 if {@link Movie} was not found
     */
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

// ----------------------------------- SEARCH ------------------------------------------------------------------------

    /**
     *
     * @param title title of searched for {@link Movie} objects
     * @return list of {@link Movie} objects and HTTP code 200 if any movies were found. HTTP code 404 otherwise
     */
    @GetMapping("/title/{title}")
    public ResponseEntity<CollectionModel<EntityModel<Movie>>> findByTitle(@PathVariable String title) {
        List<Movie> movies = movieService.findByTitle(title);

        if (movies.isEmpty()) { throw new MovieNotFoundException(EMovieSearchParam.TITLE, title); }

        CollectionModel<EntityModel<Movie>> collectionModel = createCollectionModelFromList(movies,
                linkTo(methodOn(MovieController.class).findByTitle(title)).withSelfRel());

        return ResponseEntity.ok(collectionModel);
    }

    /**
     *
     * @param director person that directed searched for {@link Movie} objects
     * @return list of {@link Movie} objects and HTTP code 200 if any movies were found. HTTP code 404 otherwise
     */
    @GetMapping("/director/{director}")
    public ResponseEntity<CollectionModel<EntityModel<Movie>>> findByDirector(@PathVariable String director) {
        List<Movie> movies = movieService.findByDirector(director);

        if (movies.isEmpty()) { throw new MovieNotFoundException(EMovieSearchParam.DIRECTOR, director); }

        CollectionModel<EntityModel<Movie>> collectionModel = createCollectionModelFromList(movies,
                linkTo(methodOn(MovieController.class).findByDirector(director)).withSelfRel());

        return ResponseEntity.ok(collectionModel);
    }

    /**
     *
     * @param genre genre with which searched for {@link Movie} objects are associated
     * @return list of {@link Movie} objects and HTTP code 200 if any movies were found. HTTP code 404 otherwise
     */
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

    /**
     *
     * @param country country in which searched for {@link Movie} objects were created
     * @return list of {@link Movie} objects and HTTP code 200 if any movies were found. HTTP code 404 otherwise
     */
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

    /**
     *
     * @param year year in which searched for {@link Movie} objects were released
     * @return list of {@link Movie} objects and HTTP code 200 if any movies were found. HTTP code 404 otherwise
     */
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

    /**
     *
     * @param imdbId IMDb ID of the {@link Movie} that shall be retrieved
     * @return {@link Movie} and HTTP code 200 if a match was found. HTTP code 404 otherwise
     */
    @GetMapping("/imdbId/{imdbId}")
    public ResponseEntity<EntityModel<Movie>> findByImdbId(@PathVariable String imdbId) {
        Movie movie = movieService.findByImdbId(imdbId).orElseThrow(() -> new MovieNotFoundException(imdbId));
        return ResponseEntity.ok(movieAssembler.toModel(movie));
    }

    private CollectionModel<EntityModel<Movie>> createCollectionModelFromList(
            List<Movie> movies, Link selfLink) {

        List<EntityModel<Movie>> movieModels = movies.stream()
                .map(movieAssembler::toModel)
                .toList();

        return CollectionModel.of(movieModels,selfLink);
    }

// -------------------------------------- REVIEWS ---------------------------------------------------------------------

    /**
     *
     * @param id number of the {@link Movie} for which all reviews shall be retrieved
     * @return list of all {@link Review} objects associated with the given {@link Movie}
     */
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

    /**
     *
     * @param id number of the {@link Movie} to which the new {@link Review} will be added
     * @param reviewDTO {@link ReviewDTO} object that will be created and added to the {@link Movie}
     * @return HTTP code 201 and the created {@link Review}. HTTP code 404 if the {@link Movie} was not found
     */
    @PostMapping("/{id}/reviews")
    public ResponseEntity<?> addReviewToMovie(@PathVariable Long id, @RequestBody ReviewDTO reviewDTO) {
        Movie reviewedMovie = movieService.findById(id).orElseThrow(() -> new MovieNotFoundException(id));
        User reviewingUser = userService.findById(reviewDTO.getUserId()).orElseThrow(() -> new UserNotFoundException(reviewDTO.getUserId()));
        Review newReview = reviewMapper.toReview(reviewDTO, reviewingUser, reviewedMovie);

        EntityModel<Review> entityModel = reviewAssembler.toModel(reviewService.save(newReview));
        return ResponseEntity
                .created(entityModel.getRequiredLink(IanaLinkRelations.SELF).toUri())
                .body(entityModel);
    }

    /**
     *
     * @param movieId number of the {@link Movie} for which the to be edited {@link Review} was created
     * @param reviewId number of the {@link Review} which will be edited
     * @param reviewDTO {@link ReviewUpdateDTO} object containing the new information
     * @return HTTP code 204 if the {@link Review} was updated. HTTP code 404 if the {@link Movie}/{@link Review} was not found
     */
    @PutMapping("/{movieId}/reviews/{reviewId}")
    public ResponseEntity<?> editReviewById(@PathVariable Long movieId, @PathVariable Long reviewId,
                                            @RequestBody ReviewUpdateDTO reviewDTO) {
        movieService.findById(movieId).orElseThrow(() -> new MovieNotFoundException(movieId));
        Review review = reviewService.findById(reviewId).orElseThrow(() -> new ReviewNotFoundException(reviewId));

        review.updateFromDTO(reviewDTO);
        reviewService.save(review);

        return ResponseEntity.noContent().build();
    }

// ---------------------------------------- ACTORS --------------------------------------------------------------------

    /**
     *
     * @param id number of the {@link Movie} for which all actors shall be retrieved
     * @return list of all {@link Actor} objects associated with the given {@link Movie}
     */
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

    /**
     *
     * @param movieId number of the movie from which the actor is to be retrieved
     * @param actorId number of the actor that is to be retrieved from the movie
     * @return {@link Actor} and HTTP code 200 if the actor was found. HTTP code 404 otherwise
     */
    @GetMapping("/{movieId}/actors/{actorId}")
    public ResponseEntity<EntityModel<Actor>> findActorById(@PathVariable Long movieId, @PathVariable Long actorId) {
        Movie movie = movieService.findById(movieId).orElseThrow(() -> new MovieNotFoundException(movieId));
        Actor actor = movie.getActors().stream()
                .filter(a -> Objects.equals(a.getId(), actorId)).findFirst().orElseThrow(() -> new ActorNotFoundException(actorId));

        return ResponseEntity.ok(actorAssembler.toModel(actor));

    }

    /**
     *
     * @param movieId identifies the movie to which the actor is to be added
     * @param actorId identifies the actor which is to be added to the movie
     * @return {@link Movie} and HTTP code 200 if the actor was successfully added or HTTP code 404 if the movie/actor was not found
     */
    @PostMapping("/{movieId}/actors")
    public ResponseEntity<EntityModel<Movie>> addActorToMovie(@PathVariable Long movieId, @RequestBody Long actorId) {
        Movie movie = movieService.findById(movieId).orElseThrow(() -> new MovieNotFoundException(movieId));
        Actor actor = actorService.findById(actorId).orElseThrow(() -> new ActorNotFoundException(actorId));

        movie.getActors().add(actor);
        movieService.save(movie);

        return ResponseEntity.ok(movieAssembler.toModel(movie));
    }

    /**
     *
     * @param movieId identifies the movie from which the actor is to be removed
     * @param actorId identifies the actor that is to be removed
     * @return {@link Movie} and HTTP code 200 if the actor was successfully removed or HTTP code 404 if the movie was not found
     */
    @DeleteMapping("/{movieId}/actors/{actorId}")
    public ResponseEntity<EntityModel<Movie>> removeActorFromMovie(@PathVariable Long movieId, @PathVariable Long actorId) {
        Movie movie = movieService.findById(movieId).orElseThrow(() -> new MovieNotFoundException(movieId));

        movie.removeActor(actorId);
        movieService.save(movie);

        return ResponseEntity.ok(movieAssembler.toModel(movie));
    }

    /**
     *
     * @param page number of the page returned
     * @param size number of movies listed in every page
     * @param sortBy attribute which determines how movies will be sorted
     * @param sortDirection order of sorting. Can be ASC or DESC
     * @return {@link PagedModel} object with sorted/filtered movies wrapped in {@link ResponseEntity<>}
     */
    @GetMapping("/all")
    public ResponseEntity<PagedModel<EntityModel<Movie>>> allMovies(@RequestParam(name = "page", defaultValue = "0") int page,
                                                                    @RequestParam(name = "size", defaultValue = "5") int size,
                                                                    @RequestParam(name = "sortBy", defaultValue = "id") String sortBy,
                                                                    @RequestParam(name = "sortDirection", defaultValue = "ASC") String sortDirection) {

        Page<Movie> movies = movieService.findAll(page, size, sortBy, sortDirection);
        return ResponseEntity.ok(pagedResourcesAssembler.toModel(movies,movieAssembler));
    }

}
