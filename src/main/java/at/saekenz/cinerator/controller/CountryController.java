package at.saekenz.cinerator.controller;

import at.saekenz.cinerator.model.country.Country;
import at.saekenz.cinerator.model.country.CountryDTO;
import at.saekenz.cinerator.model.country.CountryDTOModelAssembler;
import at.saekenz.cinerator.model.country.CountryMapper;
import at.saekenz.cinerator.model.movie.Movie;
import at.saekenz.cinerator.model.movie.MovieDTO;
import at.saekenz.cinerator.model.movie.MovieDTOModelAssembler;
import at.saekenz.cinerator.model.movie.MovieMapper;
import at.saekenz.cinerator.model.person.Person;
import at.saekenz.cinerator.model.person.PersonDTO;
import at.saekenz.cinerator.model.person.PersonDTOModelAssembler;
import at.saekenz.cinerator.model.person.PersonMapper;
import at.saekenz.cinerator.service.ICountryService;
import at.saekenz.cinerator.util.CollectionModelBuilderService;
import at.saekenz.cinerator.util.ResponseBuilderService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.web.HateoasPageableHandlerMethodArgumentResolver;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/countries")
public class CountryController {
    private final CountryDTOModelAssembler countryDTOAssembler;
    private final PersonDTOModelAssembler personDTOModelAssembler;
    private final MovieDTOModelAssembler movieDTOModelAssembler;

    @Autowired
    ICountryService countryService;

    @Autowired
    CountryMapper countryMapper;

    @Autowired
    PersonMapper personMapper;

    @Autowired
    MovieMapper movieMapper;

    @Autowired
    CollectionModelBuilderService collectionModelBuilderService;

    @Autowired
    ResponseBuilderService responseBuilderService;

    private final PagedResourcesAssembler<CountryDTO> pagedResourcesAssembler = new PagedResourcesAssembler<>(
            new HateoasPageableHandlerMethodArgumentResolver(), null);

    public CountryController(CountryDTOModelAssembler countryDTOAssembler,
                             PersonDTOModelAssembler personDTOModelAssembler,
                             MovieDTOModelAssembler movieDTOModelAssembler) {
        this.countryDTOAssembler = countryDTOAssembler;
        this.personDTOModelAssembler = personDTOModelAssembler;
        this.movieDTOModelAssembler = movieDTOModelAssembler;
    }

    /**
     * Fetch every {@link Country} resource from the database (in a paged format).
     *
     * @param page number of the page returned
     * @param size number of {@link Country} resources returned for each page
     * @param sortField attribute that determines how returned resources will be sorted
     * @param sortDirection order of sorting (can be ASC or DESC)
     * @return {@link PagedModel} object with sorted/filtered {@link Country} resources wrapped
     * in {@link ResponseEntity<>}
     */
    @GetMapping()
    public ResponseEntity<PagedModel<EntityModel<CountryDTO>>> findAllCountries(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "5") int size,
            @RequestParam(name = "sortField", defaultValue = "id") String sortField,
            @RequestParam(name = "sortDirection", defaultValue = "ASC") String sortDirection) {
        Page<CountryDTO> countries = countryService.findAllPaged(page, size, sortField, sortDirection)
                .map(countryMapper::toDTO);

        return ResponseEntity.ok(pagedResourcesAssembler.toModel(countries, countryDTOAssembler));
    }

    /**
     * Fetch a specific {@link Country} by its {@code id}.
     *
     * @param id the ID of the {@link Country} that will be retrieved.
     * @return ResponseEntity containing 200 Ok status and the {@link Country} resource.
     * (Returns 404 Not Found if the {@link Country} does not exist for this {@code id}.)
     */
    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<CountryDTO>> findCountryById(@PathVariable Long id) {
        Country country = countryService.findCountryById(id);

        return ResponseEntity
                .ok(countryDTOAssembler.toModel(countryMapper.toDTO(country)));
    }

// ------------------------------ CREATE/UPDATE/DELETE --------------------------------------------------------------

    /**
     * Creates a new {@link Country}.
     *
     * @param countryDTO a DTO containing data of the new {@link Country}
     * @return ResponseEntity containing a 201 Created status and the created {@link Country}.
     */
    @PostMapping()
    public ResponseEntity<?> createCountry(@Valid @RequestBody CountryDTO countryDTO) {
        Country createdCountry = countryService.createCountry(countryDTO);
        EntityModel<CountryDTO> createdCountryModel = countryDTOAssembler.toModel(countryMapper
                .toDTO(createdCountry));

        return responseBuilderService.buildCreatedResponseWithBody(createdCountryModel);
    }

    /**
     * Updates a {@link Country} based on its id.
     *
     * @param id the ID of the {@link Country} to be updated
     * @param countryDTO a DTO containing the needed data
     * @return ResponseEntity containing a 204 No Content status
     * (Returns 404 Not Found if the to be updated {@link Country} does not exist in the database)
     */
    @PutMapping("/{id}")
    public ResponseEntity<Object> updateCountry(@PathVariable Long id, @Valid @RequestBody CountryDTO countryDTO) {
        Country updatedCountry = countryService.updateCountry(id, countryDTO);
        EntityModel<CountryDTO> updatedCountryModel = countryDTOAssembler.toModel(countryMapper
                .toDTO(updatedCountry));

        return responseBuilderService.buildNoContentResponseWithLocation(updatedCountryModel);
    }

    /**
     * Deletes a {@link Country} by its {@code id}.
     *
     * @param id the ID of the {@link Country} to be deleted
     * @return ResponseEntity containing a 204 No Content status (or a
     * 404 Not Found status if no {@link Country} exists with the specified {@code id}.)
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteCountry(@PathVariable Long id) {
        countryService.deleteById(id);

        return ResponseEntity.noContent().build();
    }

// --------------------------------- OTHER --------------------------------------------------------------------------

    /**
     * Fetch every {@link Person} associated with {@link Country} with {@code id}.
     *
     * @param id the ID of the {@link Country} for which persons are fetched
     * @return ResponseEntity containing a 200 Ok status and the persons associated
     * with that {@link Country}. (Returns a 404 Not Found status if the {@link Country}
     * does not exist.)
     */
    @GetMapping("/{id}/persons")
    public ResponseEntity<CollectionModel<EntityModel<PersonDTO>>> findPersonsByCountry(@PathVariable Long id) {
        Country country = countryService.findCountryById(id);
        Set<Person> persons = country.getPersons();

        if (persons.isEmpty()) { return ResponseEntity.ok(CollectionModel.empty()); }

        CollectionModel<EntityModel<PersonDTO>> collectionModel = collectionModelBuilderService
                .createCollectionModelFromList(persons, personMapper, personDTOModelAssembler,
                        linkTo(methodOn(CountryController.class).findPersonsByCountry(id)).withSelfRel());

        return ResponseEntity.ok(collectionModel);
    }

    /**
     * Fetch every {@link Movie} associated with {@link Country} with {@code id}.
     *
     * @param id the ID of the {@link Country} for which movies are fetched
     * @return ResponseEntity containing a 200 Ok status and the persons associated
     * with that {@link Country}. (Returns a 404 Not Found status if the {@link Country}
     * does not exist.)
     */
    @GetMapping("/{id}/movies")
    public ResponseEntity<CollectionModel<EntityModel<MovieDTO>>> findMoviesByCountry(@PathVariable Long id) {
        Country country = countryService.findCountryById(id);
        Set<Movie> movies = country.getMovies();

        if (movies.isEmpty()) { return ResponseEntity.ok(CollectionModel.empty()); }

        CollectionModel<EntityModel<MovieDTO>> collectionModel = collectionModelBuilderService
                .createCollectionModelFromList(movies, movieMapper, movieDTOModelAssembler,
        linkTo(methodOn(CountryController.class).findMoviesByCountry(id)).withSelfRel());

        return ResponseEntity.ok(collectionModel);
    }
}
