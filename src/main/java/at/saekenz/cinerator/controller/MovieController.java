package at.saekenz.cinerator.controller;

import at.saekenz.cinerator.model.country.Country;
import at.saekenz.cinerator.model.country.CountryDTO;
import at.saekenz.cinerator.model.country.CountryDTOModelAssembler;
import at.saekenz.cinerator.model.country.CountryMapper;
import at.saekenz.cinerator.model.genre.Genre;
import at.saekenz.cinerator.model.genre.GenreDTO;
import at.saekenz.cinerator.model.genre.GenreDTOModelAssembler;
import at.saekenz.cinerator.model.genre.GenreMapper;
import at.saekenz.cinerator.model.movie.*;
import at.saekenz.cinerator.model.person.Person;
import at.saekenz.cinerator.model.person.PersonDTO;
import at.saekenz.cinerator.model.person.PersonDTOModelAssembler;
import at.saekenz.cinerator.model.person.PersonMapper;
import at.saekenz.cinerator.model.review.*;
import at.saekenz.cinerator.model.role.Role;
import at.saekenz.cinerator.service.*;
import at.saekenz.cinerator.util.CollectionModelBuilderService;
import at.saekenz.cinerator.util.ResponseBuilderService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Range;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.web.HateoasPageableHandlerMethodArgumentResolver;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/movies")
public class MovieController {
    private final MovieDTOModelAssembler movieDTOAssembler;
    private final ReviewDTOModelAssembler reviewAssembler;
    private final GenreDTOModelAssembler genreDTOAssembler;
    private final CountryDTOModelAssembler countryDTOAssembler;
    private final PersonDTOModelAssembler personDTOAssembler;

    @Autowired
    IMovieService movieService;

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
    PersonMapper personMapper;

    @Autowired
    ResponseBuilderService responseBuilderService;

    @Autowired
    CollectionModelBuilderService collectionModelBuilderService;

    // Workaround since using the @Autowired annotation causes Intellij to report an error
    private final PagedResourcesAssembler<MovieDTO> pagedResourcesAssembler = new PagedResourcesAssembler<>(
            new HateoasPageableHandlerMethodArgumentResolver(), null);

    public MovieController(MovieDTOModelAssembler movieDTOAssembler, ReviewDTOModelAssembler reviewAssembler,
                           GenreDTOModelAssembler genreDTOAssembler, CountryDTOModelAssembler countryDTOAssembler,
                           PersonDTOModelAssembler personDTOAssembler) {
        this.movieDTOAssembler = movieDTOAssembler;
        this.reviewAssembler = reviewAssembler;
        this.genreDTOAssembler = genreDTOAssembler;
        this.countryDTOAssembler = countryDTOAssembler;
        this.personDTOAssembler = personDTOAssembler;
    }

    /**
     * Fetch every {@link Movie} resource from the database (in a paged format).
     *
     * @param page number of the page returned
     * @param size number of movies listed in every page
     * @param sortBy attribute which determines how movies will be sorted
     * @param sortDirection order of sorting. Can be ASC or DESC
     * @return {@link PagedModel} object with sorted/filtered movies wrapped in {@link ResponseEntity<>}
     */
    @GetMapping()
    public ResponseEntity<PagedModel<EntityModel<MovieDTO>>> findAllMovies(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "5") int size,
            @RequestParam(name = "sortBy", defaultValue = "id") String sortBy,
            @RequestParam(name = "sortDirection", defaultValue = "ASC") String sortDirection) {
        Page<MovieDTO> movies = movieService.findAllPaged(page, size, sortBy, sortDirection)
                .map(movieMapper::toDTO);

        return ResponseEntity.ok(pagedResourcesAssembler.toModel(movies,movieDTOAssembler));
    }

    /**
     * Fetch a specific {@link Movie} by its {@code id}.
     *
     * @param id the ID of the {@link Movie} that will be retrieved.
     * @return {@link ResponseEntity<>} containing 200 Ok status and the {@link Movie} resource.
     * (Returns 404 Not Found if the {@link Movie} does not exist for this {@code id}.)
     */
    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<MovieDTO>> findMovieById(@PathVariable Long id) {
        Movie movie = movieService.findMovieById(id);
        return ResponseEntity.ok(movieDTOAssembler.toModel(movieMapper.toDTO(movie)));
    }

// ------------------------------ CREATE/UPDATE/DELETE --------------------------------------------------------------

    /**
     * Creates a new {@link Movie}.
     *
     * @param creationDTO a DTO containing data of the new {@link Movie}
     * @return {@link ResponseEntity<>} containing a 201 Created status and the created {@link Movie}.
     */
    @PostMapping()
    public ResponseEntity<EntityModel<MovieDTO>> createMovie(@Valid @RequestBody MovieCreationDTO creationDTO) {
        Movie createdMovie = movieService.createMovie(creationDTO);
        EntityModel<MovieDTO> createdMovieModel = movieDTOAssembler.toModel(movieMapper.toDTO(createdMovie));

        return responseBuilderService.buildCreatedResponseWithBody(createdMovieModel);
    }

    /**
     * Updates a {@link Movie} based on its {@code id}.
     *
     * @param id the ID of the {@link Movie} that is to be updated
     * @param movieDTO a DTO containing the needed data
     * @return {@link ResponseEntity<>} containing a 204 No Content status
     * (or a 404 Not Found if the to be updated {@link Movie} does not exist in the database).
     */
    @PutMapping("/{id}")
    public ResponseEntity<Object> updateMovie(@NotNull @Range(min = 1) @PathVariable Long id,
                                         @Valid @RequestBody MovieCreationDTO movieDTO) {
        Movie updatedMovie = movieService.updateMovie(id, movieDTO);
        EntityModel<MovieDTO> updatedMovieModel = movieDTOAssembler.toModel(movieMapper.toDTO(updatedMovie));

        return responseBuilderService.buildNoContentResponseWithLocation(updatedMovieModel);
    }

    /**
     * Deletes a {@link Movie} by its {@code id}.
     *
     * @param id the ID of the {@link Movie} that is to be removed from the database
     * @return {@link ResponseEntity<>} containing a 204 No Content status(or a
     * 404 Not Found status if no {@link Movie} exists with the specified {@code id}.)
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteMovie(@PathVariable Long id) {
        movieService.deleteById(id);

        return ResponseEntity.noContent().build();
    }

// ----------------------------------- SEARCH ------------------------------------------------------------------------

    /**
     * Fetches {@link Movie} resources based on search parameters.
     *
     * @param title title of the searched for movie(s)
     * @param releaseDate initial date of release of the searched for movie(s)
     * @param releaseYear year of release of the searched for movie(s)
     * @param runtime runtime of the searched for movie(s)
     * @param imdbId imdbId of the searched for movie(s)
     * @param genre genre of the searched for movie(s)
     * @param country country of origin of the searched for movie(s)
     * @return {@link ResponseEntity<>}  containing a 200 Ok status and a collection of the found
     * {@link Movie} resources.
     */
    @GetMapping("/search")
    public ResponseEntity<CollectionModel<EntityModel<MovieDTO>>> searchMovies(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) LocalDate releaseDate,
            @RequestParam(required = false) Integer releaseYear,
            @RequestParam(required = false) String runtime,
            @RequestParam(required = false) String imdbId,
            @RequestParam(required = false) String genre,
            @RequestParam(required = false) String country) {
       List<Movie> foundMovies = movieService.findMoviesBySearchParams(title, releaseDate,
               releaseYear, runtime, imdbId, genre, country);

       if (foundMovies.isEmpty()) { return ResponseEntity.ok(CollectionModel.empty()); }

       CollectionModel<EntityModel<MovieDTO>> collectionModel = collectionModelBuilderService
               .createCollectionModelFromList(foundMovies, movieMapper, movieDTOAssembler,
                       linkTo(methodOn(MovieController.class).searchMovies(title, releaseDate, releaseYear,
                               runtime, imdbId, genre, country)).withSelfRel());

       return ResponseEntity.ok(collectionModel);
    }

    /**
     * Fetches {@link Movie} resources based on their {@code title}.
     *
     * @param title title of the searched for {@link Movie} resources
     * @return {@link ResponseEntity<>}  containing a 200 Ok status and a collection of the found
     * {@link Movie} resources.
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
     * Fetches {@link Movie} resources based on their
     *
     * @param genre genre with which the searched for {@link Movie} resources are associated
     * @return {@link ResponseEntity<>}  containing a 200 Ok status and a collection of the found
     * {@link Movie} resources.
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
     * Fetches {@link Movie} resources based on their
     *
     * @param country country in which the searched for {@link Movie} resources were created
     * @return {@link ResponseEntity<>}  containing a 200 Ok status and a collection of the found
     * {@link Movie} resources.
     */
    @GetMapping("/country/{country}")
    public ResponseEntity<CollectionModel<EntityModel<MovieDTO>>> findByCountry(@PathVariable String country) {
        List<Movie> movies = movieService.findByCountry(country);

        if (movies.isEmpty()) { return ResponseEntity.ok(CollectionModel.empty()); }

        CollectionModel<EntityModel<MovieDTO>> collectionModel = collectionModelBuilderService
                .createCollectionModelFromList(movies,
                        linkTo(methodOn(MovieController.class).findByCountry(country)).withSelfRel());

        return ResponseEntity.ok(collectionModel);
    }

    /**
     * Fetches {@link Movie} resources based on their
     *
     * @param year year in which the searched for {@link Movie} resources were released
     * @return {@link ResponseEntity<>}  containing a 200 Ok status and a collection of the found
     * {@link Movie} resources.
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

// -------------------------------------- REVIEWS ---------------------------------------------------------------------

    /**
     * Fetches {@link Review} resources associated with the {@link Movie} identified by {@code id}.
     *
     * @param id the ID of the {@link Movie} for which the {@link Review} resources are to be fetched
     * @return {@link ResponseEntity<>} containing a 200 Ok status and the requested {@link Review} resources
     * (or a 404 Not Found if no {@link Movie} exists for this {@code id}).
     */
    @GetMapping("/{id}/reviews")
    public ResponseEntity<CollectionModel<EntityModel<ReviewDTO>>> findReviewsByMovie(
            @NotNull @Range(min = 1) @PathVariable Long id) {
        List<Review> reviews = movieService.findReviewsByMovieId(id);

        if (reviews.isEmpty()) { return ResponseEntity.ok().build(); }

        CollectionModel<EntityModel<ReviewDTO>> collectionModel = collectionModelBuilderService
                .createCollectionModelFromList(reviews, reviewMapper, reviewAssembler,
                        linkTo(methodOn(MovieController.class).findReviewsByMovie(id)).withSelfRel());

        return ResponseEntity.ok(collectionModel);
    }

    /**
     * Fetches a specific {@link Review} resource by its {@code id} if it exists for the {@link Movie}
     * identified by {@code movieId}.
     *
     * @param movieId the ID of the {@link Movie} from which the review is to be retrieved
     * @param reviewId the ID of the {@link Review} that is to be retrieved from the {@link Movie}
     * @return {@link ResponseEntity<>} containing a 200 Ok status and the {@link Review} resource (or a
     * 404 Not Found status if no {@link Movie}/{@link Review} exists with the specified {@code movieId/reviewId}).
     */
    @GetMapping("/{movieId}/reviews/{reviewId}")
    public ResponseEntity<EntityModel<ReviewDTO>> findReviewById(@NotNull @Range(min = 1) @PathVariable Long movieId,
                                                              @NotNull @Range(min = 1) @PathVariable Long reviewId) {
        Review review = movieService.findReviewByMovieId(movieId, reviewId);

        return ResponseEntity.ok(reviewAssembler.toModel(reviewMapper.toDTO(review)));
    }

    /**
     * Creates and adds a new {@link Review} resource specified by {@code reviewDTO} to {@link Movie}
     * identified by {@code movieId}.
     *
     * @param movieId the ID of the {@link Movie} to which the new {@link Review} will be added
     * @param reviewDTO a DTO containing data of the new {@link ReviewDTO}
     * @return {@link ResponseEntity<>} containing a 201 Created status and the created {@link Review}
     * (or a 404 Not Found status if no {@link Movie} exists with the specified {@code movieId}).
     */
    @PostMapping("/{movieId}/reviews")
    public ResponseEntity<EntityModel<ReviewDTO>> addReviewToMovie(@NotNull @Range(min = 1) @PathVariable Long movieId,
                                                   @Valid @RequestBody ReviewCreationDTO reviewDTO) {
        Review newReview = movieService.addReviewToMovie(movieId, reviewDTO);
        EntityModel<ReviewDTO> entityModel = reviewAssembler.toModel(reviewMapper
                .toDTO(newReview));

        return responseBuilderService.buildCreatedResponseWithBody(entityModel);
    }

    /**
     * Updates a {@link Review} based on its {@code reviewId}.
     *
     * @param movieId the ID of the {@link Movie} for which the to be edited {@link Review} was created
     * @param reviewId the ID of the {@link Review} which will be edited
     * @param reviewDTO a DTO containing the needed update data
     * @return {@link ResponseEntity<>} containing a 204 No Content status (or a 404 Not Found status if the
     * {@link Movie} does not exist or the {@link Review} is not found).
     */
    @PutMapping("/{movieId}/reviews/{reviewId}")
    public ResponseEntity<Object> editReviewById(@NotNull @Range(min = 1) @PathVariable Long movieId,
                                                 @NotNull @Range(min = 1) @PathVariable Long reviewId,
                                                 @Valid @RequestBody ReviewUpdateDTO reviewDTO) {
        Review updatedReview = movieService.editReviewForMovie(movieId, reviewId, reviewDTO);
        EntityModel<ReviewDTO> entityModel = reviewAssembler.toModel(reviewMapper
                .toDTO(updatedReview));

        return responseBuilderService.buildNoContentResponseWithLocation(entityModel);
    }

    /**
     * Removes a {@link Review} resource specified by {@code reviewId} from the {@link Movie}
     * identified by {@code userId}
     *
     * @param movieId the ID of the {@link Movie} from which the {@link Review} is to be removed
     * @param reviewId the ID of the {@link Review} that is to be removed
     * @return {@link ResponseEntity<>} containing a 204 No Content status (or a 404 Not Found status if the
     * {@link Movie} does not exist or the {@link Review} is not found).
     */
    @DeleteMapping("/{movieId}/reviews/{reviewId}")
    public ResponseEntity<?> removeReviewById(@NotNull @Range(min = 1) @PathVariable Long movieId,
                                              @NotNull @Range(min = 1) @PathVariable Long reviewId) {
        movieService.removeReviewFromMovie(movieId, reviewId);

        return ResponseEntity.noContent().build();
    }

// ---------------------------------------- CAST/CREW --------------------------------------------------------------------

    /**
     * Fetches all {@link Person} resources that have {@link Role} {@code actor} in the {@link Movie}
     * identified by {@code id}.
     *
     * @param id the ID of the {@link Movie} for which all actors shall be retrieved
     * @return {@link ResponseEntity<>} containing a 200 Ok status and a collection of all fitting {@link Person}
     * resources (or 404 Not Found status if the {@link Movie} does not exist).
     */
    @GetMapping("/{id}/actors")
    public ResponseEntity<CollectionModel<EntityModel<PersonDTO>>> findActorsByMovie(
            @NotNull @Range(min = 1) @PathVariable Long id) {
        List<Person> actorsInMovie = movieService.findActorsByMovieId(id);

        if (actorsInMovie.isEmpty()) { return ResponseEntity.ok().build(); }

        CollectionModel<EntityModel<PersonDTO>> collectionModel = collectionModelBuilderService
                .createCollectionModelFromList(actorsInMovie, personMapper, personDTOAssembler,
                        linkTo(methodOn(MovieController.class).findActorsByMovie(id)).withSelfRel());

        return ResponseEntity.ok(collectionModel);
    }

    /**
     * Fetches all {@link Person} resources that have {@link Role} {@code director} in the {@link Movie}
     * identified by {@code id}.
     *
     * @param id the ID of the {@link Movie} for which all actors shall be retrieved
     * @return {@link ResponseEntity<>} containing a 200 Ok status and a collection of all fitting {@link Person}
     * resources (or 404 Not Found status if the {@link Movie} does not exist).
     */
    @GetMapping("/{id}/directors")
    public ResponseEntity<CollectionModel<EntityModel<PersonDTO>>> findDirectorsByMovie(
            @NotNull @Range(min = 1) @PathVariable Long id) {
        List<Person> directorsInMovie = movieService.findDirectorsByMovieId(id);

        if (directorsInMovie.isEmpty()) { return ResponseEntity.ok().build(); }

        CollectionModel<EntityModel<PersonDTO>> collectionModel = collectionModelBuilderService
                .createCollectionModelFromList(directorsInMovie, personMapper, personDTOAssembler,
                        linkTo(methodOn(MovieController.class).findActorsByMovie(id)).withSelfRel());

        return ResponseEntity.ok(collectionModel);
    }

// --------------------------------------------------- GENRES --------------------------------------------------------

    /**
     * Fetches every {@link Genre} associated with the {@link Movie} with a specific {@code id}.
     *
     * @param movieId the ID of the {@link Movie} for which genres are to be fetched
     * @return {@link ResponseEntity<>} containing a 200 Ok status and every {@link Genre} associated with this
     * {@link Movie} (or a 404 Not Found status if no {@link Movie} exists with this {@code id}).
     */
    @GetMapping("/{movieId}/genres")
    public ResponseEntity<CollectionModel<EntityModel<GenreDTO>>> findGenresByMovie(
            @NotNull @Range(min = 1) @PathVariable Long movieId) {
        List<Genre> genres = movieService.findGenresByMovieId(movieId);

        if (genres.isEmpty()) { return ResponseEntity.ok().build(); }

        CollectionModel<EntityModel<GenreDTO>> collectionModel = collectionModelBuilderService
                .createCollectionModelFromList(genres, genreMapper, genreDTOAssembler,
                        linkTo(methodOn(MovieController.class).findGenresByMovie(movieId)).withSelfRel());

        return ResponseEntity.ok(collectionModel);
    }

// ------------------------------------------------- COUNTRIES --------------------------------------------------------

    /**
     * Fetches every {@link Country} associated with the {@link Movie} with a specific {@code id}.
     *
     * @param movieId the ID of the {@link Movie} for which countries are to be fetched
     * @return {@link ResponseEntity<>} containing a 200 Ok status and every {@link Country} associated with this
     * {@link Movie} (or a 404 Not Found status if no {@link Movie} exists with this {@code id}).
     */
    @GetMapping("/{movieId}/countries")
    public ResponseEntity<CollectionModel<EntityModel<CountryDTO>>> findCountriesByMovie(
            @NotNull @Range(min = 1) @PathVariable Long movieId) {
        List<Country> countries = movieService.findCountriesByMovieId(movieId);

        if (countries.isEmpty()) {
            return ResponseEntity.ok().build();
        }

        CollectionModel<EntityModel<CountryDTO>> collectionModel = collectionModelBuilderService
                .createCollectionModelFromList(countries, countryMapper, countryDTOAssembler,
                        linkTo(methodOn(MovieController.class).findCountriesByMovie(movieId)).withSelfRel());

        return ResponseEntity.ok(collectionModel);
    }
}
