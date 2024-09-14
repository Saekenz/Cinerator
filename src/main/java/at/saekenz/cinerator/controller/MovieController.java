package at.saekenz.cinerator.controller;

import at.saekenz.cinerator.model.actor.Actor;
import at.saekenz.cinerator.model.actor.ActorModelAssembler;
import at.saekenz.cinerator.model.actor.ActorNotFoundException;
import at.saekenz.cinerator.model.country.Country;
import at.saekenz.cinerator.model.country.CountryDTO;
import at.saekenz.cinerator.model.country.CountryDTOModelAssembler;
import at.saekenz.cinerator.model.country.CountryMapper;
import at.saekenz.cinerator.model.genre.Genre;
import at.saekenz.cinerator.model.genre.GenreDTOModelAssembler;
import at.saekenz.cinerator.model.genre.GenreMapper;
import at.saekenz.cinerator.model.movie.*;
import at.saekenz.cinerator.model.review.*;
import at.saekenz.cinerator.model.user.User;
import at.saekenz.cinerator.model.user.UserNotFoundException;
import at.saekenz.cinerator.service.*;
import at.saekenz.cinerator.util.CollectionModelBuilderService;
import at.saekenz.cinerator.util.ResponseBuilderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.web.HateoasPageableHandlerMethodArgumentResolver;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

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
    IGenreService genreService;

    @Autowired
    IUserService userService;

    @Autowired
    MovieMapper movieMapper;

    @Autowired
    ReviewMapper reviewMapper;

    @Autowired
    GenreMapper genreMapper;

    @Autowired
    CountryMapper countryMapper;

    @Autowired
    ResponseBuilderService responseBuilderService;

    @Autowired
    CollectionModelBuilderService collectionModelBuilderService;

    private final MovieDTOModelAssembler movieDTOAssembler;
    private final ActorModelAssembler actorAssembler;
    private final ReviewModelAssembler reviewAssembler;
    private final GenreDTOModelAssembler genreDTOAssembler;
    private final CountryDTOModelAssembler countryDTOAssembler;

    // Workaround since using the @Autowired annotation causes Intellij to report an error
    private final PagedResourcesAssembler<MovieDTO> pagedResourcesAssembler = new PagedResourcesAssembler<>(
            new HateoasPageableHandlerMethodArgumentResolver(), null);

    public MovieController(MovieDTOModelAssembler movieDTOAssembler, ReviewModelAssembler reviewAssembler,
                           ActorModelAssembler actorAssembler, GenreDTOModelAssembler genreDTOAssembler,
                           CountryDTOModelAssembler countryDTOAssembler) {
        this.movieDTOAssembler = movieDTOAssembler;
        this.reviewAssembler = reviewAssembler;
        this.actorAssembler = actorAssembler;
        this.genreDTOAssembler = genreDTOAssembler;
        this.countryDTOAssembler = countryDTOAssembler;
    }

    /**
     *
     * @return HTTP code 200 and a list of {@link Movie} objects currently stored in the database
     */
    @GetMapping
    public ResponseEntity<CollectionModel<EntityModel<MovieDTO>>> findAll() {
        List<Movie> movies = movieService.findAll();

        if (movies.isEmpty()) { return ResponseEntity.ok().build(); }

        CollectionModel<EntityModel<MovieDTO>> collectionModel = collectionModelBuilderService
                .createCollectionModelFromList(movies, linkTo(methodOn(MovieController.class).findAll()).withSelfRel());

        return ResponseEntity.ok(collectionModel);
    }

    /**
     *
     * @param id number of the {@link Movie} that is to be retrieved
     * @return HTTP code 200 and the {@link Movie} object if it was found. HTTP code 404 otherwise
     */
    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<MovieDTO>> findById(@PathVariable Long id) {
        Movie movie = movieService.findById(id).orElseThrow(() -> new MovieNotFoundException(id));
        return ResponseEntity.ok(movieDTOAssembler.toModel(movieMapper.toDTO(movie)));
    }

    /**
     *
     * @param newMovie information about the {@link Movie} that is to be added to the database
     * @return HTTP code 201 and the {@link Movie} object that was created
     */
    @PostMapping()
    public ResponseEntity<?> createMovie(@RequestBody Movie newMovie) {
//        Set<Genre> genres = newMovie.getGenres();
//        if (genres != null && !genres.isEmpty()) {
//            Set<Genre> attachedGenres = genres.stream()
//                    .map(genre -> genreService.findById(genre.getId())
//                            .orElseThrow(() -> new ObjectNotFoundException(genre.getId(), "Genre")))
//                    .collect(Collectors.toSet());
//            newMovie.setGenres(attachedGenres);
//        }

        EntityModel<MovieDTO> entityModel = movieDTOAssembler
                .toModel(movieMapper.toDTO(movieService.save(newMovie)));

        return responseBuilderService.buildCreatedResponseWithBody(entityModel);
    }

    /**
     *
     * @param id number of the {@link Movie} that is to be updated (or added if the number does not exist in the database yet)
     * @param newMovie information about the to be updated/added {@link Movie} object
     * @return HTTP code 201 and the created {@link Movie} object (or HTTP code 204 if an existing {@link Movie} object was updated)
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateMovie(@PathVariable Long id, @RequestBody Movie newMovie) {
        Optional<Movie> existingMovie = movieService.findById(id);

        Movie updatedMovie = existingMovie.map(
                        movie -> {
                            movie.setTitle(newMovie.getTitle());
                            movie.setDirector(newMovie.getDirector());
                            movie.setReleaseDate(newMovie.getReleaseDate());
                            movie.setRuntime(newMovie.getRuntime());
                            movie.setCountry(newMovie.getCountry());
                            movie.setImdbId(newMovie.getImdbId());
                            movie.setPosterUrl(newMovie.getPosterUrl());
                            movie.setReviews(newMovie.getReviews());
                            return movieService.save(movie);
                        })
                .orElseGet(() -> movieService.save(newMovie));

        EntityModel<MovieDTO> entityModel = movieDTOAssembler
                .toModel(movieMapper.toDTO(updatedMovie));

        if (existingMovie.isPresent()) {
            return responseBuilderService.buildNoContentResponseWithLocation(entityModel);
        }
        else {
            return responseBuilderService.buildCreatedResponseWithBody(entityModel);
        }
    }

    /**
     *
     * @param id number of {@link Movie} that is to be removed from the database
     * @return HTTP code 204 if {@link Movie} was deleted. HTTP code 404 if {@link Movie} was not found
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteMovie(@PathVariable Long id) {
        movieService.findById(id).orElseThrow(() -> new MovieNotFoundException(id));
        movieService.deleteById(id);

        return ResponseEntity.noContent().build();
    }

// ----------------------------------- SEARCH ------------------------------------------------------------------------

    /**
     *
     * @param title title of searched for {@link Movie} objects
     * @return list of {@link Movie} objects and HTTP code 200 if any movies were found. HTTP code 404 otherwise
     */
    @GetMapping("/title/{title}")
    public ResponseEntity<CollectionModel<EntityModel<MovieDTO>>> findByTitle(@PathVariable String title) {
        List<Movie> movies = movieService.findByTitle(title);

        if (movies.isEmpty()) { throw new MovieNotFoundException(EMovieSearchParam.TITLE, title); }

        CollectionModel<EntityModel<MovieDTO>> collectionModel = collectionModelBuilderService
                .createCollectionModelFromList(movies,
                        linkTo(methodOn(MovieController.class).findByTitle(title)).withSelfRel());

        return ResponseEntity.ok(collectionModel);
    }

    /**
     *
     * @param director person that directed searched for {@link Movie} objects
     * @return list of {@link Movie} objects and HTTP code 200 if any movies were found. HTTP code 404 otherwise
     */
    @GetMapping("/director/{director}")
    public ResponseEntity<CollectionModel<EntityModel<MovieDTO>>> findByDirector(@PathVariable String director) {
        List<Movie> movies = movieService.findByDirector(director);

        if (movies.isEmpty()) { throw new MovieNotFoundException(EMovieSearchParam.DIRECTOR, director); }

        CollectionModel<EntityModel<MovieDTO>> collectionModel = collectionModelBuilderService
                .createCollectionModelFromList(movies,
                        linkTo(methodOn(MovieController.class).findByDirector(director)).withSelfRel());

        return ResponseEntity.ok(collectionModel);
    }

    /**
     *
     * @param genre genre with which searched for {@link Movie} objects are associated
     * @return list of {@link Movie} objects and HTTP code 200 if any movies were found. HTTP code 404 otherwise
     */
    @GetMapping("/genre/{genre}")
    public ResponseEntity<CollectionModel<EntityModel<MovieDTO>>> findByGenre(@PathVariable String genre) {
        List<Movie> movies = movieService.findByGenre(genre);

        if (movies.isEmpty()) { return ResponseEntity.ok(CollectionModel.empty()); }

        CollectionModel<EntityModel<MovieDTO>> collectionModel = collectionModelBuilderService
                .createCollectionModelFromList(movies,
                        linkTo(methodOn(MovieController.class).findByGenre(genre)).withSelfRel());

        return ResponseEntity.ok(collectionModel);
    }

    /**
     *
     * @param country country in which searched for {@link Movie} objects were created
     * @return list of {@link Movie} objects and HTTP code 200 if any movies were found. HTTP code 404 otherwise
     */
    @GetMapping("/country/{country}")
    public ResponseEntity<CollectionModel<EntityModel<MovieDTO>>> findByCountry(@PathVariable String country) {
        List<Movie> movies = movieService.findByCountry(country);

        if (movies.isEmpty()) {
            throw new MovieNotFoundException(EMovieSearchParam.COUNTRY, country);
        }

        CollectionModel<EntityModel<MovieDTO>> collectionModel = collectionModelBuilderService
                .createCollectionModelFromList(movies,
                        linkTo(methodOn(MovieController.class).findByCountry(country)).withSelfRel());

        return ResponseEntity.ok(collectionModel);
    }

    /**
     *
     * @param year year in which searched for {@link Movie} objects were released
     * @return ResponseEntity containing a 200 Ok status and a list of {@link Movie}
     * resources
     */
    @GetMapping("/year/{year}")
    public ResponseEntity<?> findByYearReleased(@PathVariable int year) {
        List<Movie> movies = movieService.findByYear(year);

        if (movies.isEmpty()) { return ResponseEntity.ok(CollectionModel.empty()); }

        CollectionModel<EntityModel<MovieDTO>> collectionModel = collectionModelBuilderService
                .createCollectionModelFromList(movies,
                linkTo(methodOn(MovieController.class).findByYearReleased(year)).withSelfRel());

        return ResponseEntity.ok(collectionModel);
    }

    /**
     *
     * @param imdbId IMDb ID of the {@link Movie} that shall be retrieved
     * @return {@link Movie} and HTTP code 200 if a match was found. HTTP code 404 otherwise
     */
    @GetMapping("/imdbId/{imdbId}")
    public ResponseEntity<EntityModel<MovieDTO>> findByImdbId(@PathVariable String imdbId) {
        Movie movie = movieService.findByImdbId(imdbId).orElseThrow(() -> new MovieNotFoundException(imdbId));
        return ResponseEntity
                .ok(movieDTOAssembler.toModel(movieMapper.toDTO(movie)));
    }

// -------------------------------------- REVIEWS ---------------------------------------------------------------------

    /**
     *
     * @param id number of the {@link Movie} for which all reviews shall be retrieved
     * @return list of all {@link Review} objects associated with the given {@link Movie}
     */
    @GetMapping("/{id}/reviews")
    public ResponseEntity<CollectionModel<EntityModel<Review>>> findReviewsByMovie(@PathVariable Long id) {
        Movie movie = movieService.findById(id).orElseThrow(() -> new MovieNotFoundException(id));
        List<Review> reviews = movie.getReviews();

        if (reviews.isEmpty()) { return ResponseEntity.ok().build(); }

        List<EntityModel<Review>> reviewModels = reviews.stream()
                .map(reviewAssembler::toModel)
                .toList();

        CollectionModel<EntityModel<Review>> collectionModel = CollectionModel.of(reviewModels,
                linkTo(methodOn(MovieController.class).findReviewsByMovie(id)).withSelfRel());

        return ResponseEntity.ok(collectionModel);
    }

    /**
     *
     * @param movieId number of the {@link Movie} from which the review is to be retrieved
     * @param reviewId number of the review that is to be retrieved from the {@link Movie}
     * @return {@link Review} and HTTP code 200 if the review was found. HTTP code 404 otherwise
     */
    @GetMapping("/{movieId}/reviews/{reviewId}")
    public ResponseEntity<EntityModel<Review>> findReviewById(@PathVariable Long movieId, @PathVariable Long reviewId) {
        Movie movie = movieService.findById(movieId).orElseThrow(() -> new MovieNotFoundException(movieId));
        Review review = movie.getReviews().stream().filter(r -> Objects.equals(r.getId(), reviewId)).findFirst()
                .orElseThrow(() -> new ReviewNotFoundException(reviewId));

        return ResponseEntity.ok(reviewAssembler.toModel(review));
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

        return responseBuilderService.buildCreatedResponseWithBody(entityModel);
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
        EntityModel<Review> entityModel = reviewAssembler.toModel(reviewService.save(reviewService.save(review)));

        return responseBuilderService.buildNoContentResponseWithLocation(entityModel);
    }

    /**
     *
     * @param movieId identifies the {@link Movie} from which the review is to be removed
     * @param reviewId identifies the review that is to be removed
     * @return HTTP code 204 if the review was successfully removed or HTTP code 404 if the {@link Movie} was not found
     */
    @DeleteMapping("/{movieId}/reviews/{reviewId}")
    public ResponseEntity<?> deleteReviewById(@PathVariable Long movieId, @PathVariable Long reviewId) {
        Movie movie = movieService.findById(movieId).orElseThrow(() -> new MovieNotFoundException(movieId));

        if (movie.getReviews().stream().anyMatch(r -> Objects.equals(r.getId(), reviewId))) {
            reviewService.deleteById(reviewId);
            return ResponseEntity.noContent().build();
        }
        else {
            throw new ReviewNotFoundException(reviewId);
        }
    }

// ---------------------------------------- ACTORS --------------------------------------------------------------------

    /**
     *
     * @param id number of the {@link Movie} for which all actors shall be retrieved
     * @return list of all {@link Actor} objects associated with the given {@link Movie}
     */
    @GetMapping("/{id}/actors")
    public ResponseEntity<CollectionModel<EntityModel<Actor>>> findActorsByMovie(@PathVariable Long id) {
        Movie movie = movieService.findById(id).orElseThrow(() -> new MovieNotFoundException(id));
        List<Actor> actors = movie.getActors();

        if (actors.isEmpty()) { return ResponseEntity.ok().build(); }

        List<EntityModel<Actor>> actorModels = actors.stream()
                .map(actorAssembler::toModel)
                .toList();

        CollectionModel<EntityModel<Actor>> collectionModel = CollectionModel.of(actorModels,
                linkTo(methodOn(MovieController.class).findActorsByMovie(id)).withSelfRel());

        return ResponseEntity.ok(collectionModel);
    }

    /**
     *
     * @param movieId number of the {@link Movie} from which the {@link Actor} is to be retrieved
     * @param actorId number of the {@link Actor} that is to be retrieved from the {@link Movie}
     * @return {@link Actor} and HTTP code 200 if the {@link Actor} was found. HTTP code 404 otherwise
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
     * @param movieId identifies the {@link Movie} to which the {@link Actor} is to be added
     * @param actorId identifies the {@link Actor} which is to be added to the {@link Movie}
     * @return HTTP code 204 if the {@link Actor} was successfully added
     * or HTTP code 404 if the {@link Movie}/{@link Actor} was not found
     */
    @PutMapping("/{movieId}/actors")
    public ResponseEntity<?> addActorToMovie(@PathVariable Long movieId, @RequestBody Long actorId) {
        Movie movie = movieService.findById(movieId).orElseThrow(() -> new MovieNotFoundException(movieId));
        Actor actor = actorService.findById(actorId).orElseThrow(() -> new ActorNotFoundException(actorId));
//        Actor actor = actorService.getReferenceById(actorId);

        movie.addActor(actor);
        EntityModel<MovieDTO> entityModel = movieDTOAssembler
                .toModel(movieMapper.toDTO(movieService.save(movie)));

        return responseBuilderService.buildNoContentResponseWithLocation(entityModel);
    }

    /**
     *
     * @param movieId identifies the {@link Movie} from which the {@link Actor} is to be removed
     * @param actorId identifies the {@link Actor} that is to be removed
     * @return HTTP code 204 if the {@link Actor} was successfully removed or HTTP code 404 if the {@link Movie} was not found
     */
    @DeleteMapping("/{movieId}/actors/{actorId}")
    public ResponseEntity<?> removeActorFromMovie(@PathVariable Long movieId, @PathVariable Long actorId) {
        Movie movie = movieService.findById(movieId).orElseThrow(() -> new MovieNotFoundException(movieId));

        movie.removeActor(actorId);
        movieService.save(movie);

        return ResponseEntity.noContent().build();
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
    public ResponseEntity<PagedModel<EntityModel<MovieDTO>>> allMovies(@RequestParam(name = "page", defaultValue = "0") int page,
                                                                    @RequestParam(name = "size", defaultValue = "5") int size,
                                                                    @RequestParam(name = "sortBy", defaultValue = "id") String sortBy,
                                                                    @RequestParam(name = "sortDirection", defaultValue = "ASC") String sortDirection) {

        Page<MovieDTO> movies = movieService.findAll(page, size, sortBy, sortDirection).map(movieMapper::toDTO);

        return ResponseEntity.ok(pagedResourcesAssembler.toModel(movies,movieDTOAssembler));
    }

// --------------------------------------------------- GENRES --------------------------------------------------------

    /**
     *
     * @param movieId the id of the {@link Movie} for which genres are fetched
     * @return ResponseEntity containing a 200 Ok status and the genres associated
     * with that {@link Movie}. (Returns a 404 Not Found status if the {@link Movie}
     * does not exist.)
     */
    @GetMapping("/{movieId}/genres")
    public ResponseEntity<?> findGenresByMovie(@PathVariable Long movieId) {
        Movie movie = movieService.findById(movieId).orElseThrow(() -> new MovieNotFoundException(movieId));

        Set<Genre> genres = movie.getGenres();

        if (genres.isEmpty()) { return ResponseEntity.ok().build(); }

        CollectionModel<?> collectionModel = collectionModelBuilderService
                .createCollectionModelFromList(genres, genreMapper, genreDTOAssembler,
                        linkTo(methodOn(MovieController.class).findGenresByMovie(movieId)).withSelfRel());

        return ResponseEntity.ok(collectionModel);
    }

// ------------------------------------------------- COUNTRIES --------------------------------------------------------

    /**
     *
     * @param movieId the id of the {@link Movie} for which countries are fetched
     * @return ResponseEntity containing a 200 Ok status and the countries associated
     * with that {@link Movie}. (Returns a 404 Not Found status if the {@link Movie}
     * does not exist.)
     */
    @GetMapping("/{movieId}/countries")
    public ResponseEntity<?> findCountriesByMovie(@PathVariable Long movieId) {
        Movie movie = movieService.findById(movieId).orElseThrow(() -> new MovieNotFoundException(movieId));

        Set<Country> countries = movie.getCountries();

        if (countries.isEmpty()) {
            return ResponseEntity.ok().build();
        }

        CollectionModel<EntityModel<CountryDTO>> collectionModel = collectionModelBuilderService
                .createCollectionModelFromList(countries, countryMapper, countryDTOAssembler,
                        linkTo(methodOn(MovieController.class).findCountriesByMovie(movieId)).withSelfRel());

        return ResponseEntity.ok(collectionModel);
    }
}
