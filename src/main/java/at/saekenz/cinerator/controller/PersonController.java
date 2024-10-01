package at.saekenz.cinerator.controller;

import at.saekenz.cinerator.model.castinfo.CastInfo;
import at.saekenz.cinerator.model.castinfo.CastInfoDTO;
import at.saekenz.cinerator.model.castinfo.CastInfoDTOModelAssembler;
import at.saekenz.cinerator.model.castinfo.CastInfoMapper;
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
import at.saekenz.cinerator.model.role.Role;
import at.saekenz.cinerator.model.role.RoleDTO;
import at.saekenz.cinerator.model.role.RoleDTOModelAssembler;
import at.saekenz.cinerator.model.role.RoleMapper;
import at.saekenz.cinerator.service.IPersonService;
import at.saekenz.cinerator.util.CollectionModelBuilderService;
import at.saekenz.cinerator.util.ResponseBuilderService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.web.HateoasPageableHandlerMethodArgumentResolver;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/persons")
public class PersonController {

    @Autowired
    IPersonService personService;

    @Autowired
    CollectionModelBuilderService collectionModelBuilderService;

    @Autowired
    private ResponseBuilderService responseBuilderService;

    private final PersonMapper personMapper;
    private final PersonDTOModelAssembler personDTOModelAssembler;

    private final CountryMapper countryMapper;
    private final CountryDTOModelAssembler countryDTOModelAssembler;

    private final MovieMapper movieMapper;
    private final MovieDTOModelAssembler movieDTOModelAssembler;

    private final CastInfoMapper castInfoMapper;
    private final CastInfoDTOModelAssembler castInfoDTOModelAssembler;

    private final PagedResourcesAssembler<PersonDTO> pagedResourcesAssembler = new PagedResourcesAssembler<>(
            new HateoasPageableHandlerMethodArgumentResolver(), null);
    @Autowired
    private RoleMapper roleMapper;
    @Autowired
    private RoleDTOModelAssembler roleDTOModelAssembler;

    public PersonController(PersonMapper personMapper, PersonDTOModelAssembler personDTOModelAssembler,
                            CountryMapper countryMapper, CountryDTOModelAssembler countryDTOModelAssembler,
                            MovieMapper movieMapper, MovieDTOModelAssembler movieDTOModelAssembler,
                            CastInfoMapper castInfoMapper, CastInfoDTOModelAssembler castInfoDTOModelAssembler) {
        this.personMapper = personMapper;
        this.personDTOModelAssembler = personDTOModelAssembler;
        this.countryMapper = countryMapper;
        this.countryDTOModelAssembler = countryDTOModelAssembler;
        this.movieMapper = movieMapper;
        this.movieDTOModelAssembler = movieDTOModelAssembler;
        this.castInfoMapper = castInfoMapper;
        this.castInfoDTOModelAssembler = castInfoDTOModelAssembler;
    }

    /**
     * Fetch every {@link Person} resource from the database (in a paged format).
     *
     * @param page number of the page returned
     * @param size number of {@link Person} resources returned for each page
     * @param sortField attribute that determines how returned resources will be sorted
     * @param sortDirection order of sorting (can be ASC or DESC)
     * @return {@link PagedModel} object with sorted/filtered {@link Person} resources wrapped
     * in {@link ResponseEntity<>}
     */
    @GetMapping
    public ResponseEntity<PagedModel<EntityModel<PersonDTO>>> findAllPersons(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "5") int size,
            @RequestParam(name = "sortField", defaultValue = "id") String sortField,
            @RequestParam(name = "sortDirection", defaultValue = "ASC") String sortDirection) {
        Page<PersonDTO> persons = personService.findAllPaged(page, size, sortField, sortDirection)
                .map(personMapper::toDTO);

        return ResponseEntity.ok(pagedResourcesAssembler.toModel(persons, personDTOModelAssembler));
    }

    /**
     * Fetch a specific {@link Person} by its {@code id}.
     *
     * @param id the ID of the {@link Person} that will be retrieved.
     * @return {@link ResponseEntity<>} containing 200 Ok status and the {@link Person} resource.
     * (Returns 404 Not Found if the {@link Person} does not exist for this {@code id}.)
     */
    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<PersonDTO>> findPersonById(@PathVariable Long id) {
        Person person = personService.findPersonById(id);
        EntityModel<PersonDTO> entityModel = personDTOModelAssembler
                .toModel(personMapper.toDTO(person));

        return ResponseEntity.ok(entityModel);
    }

 // ------------------------------ CREATE/UPDATE/DELETE --------------------------------------------------------------

    /**
     * Creates a new {@link Person}.
     *
     * @param personDTO a DTO containing data of the new {@link Person}
     * @return {@link ResponseEntity<>} containing a 201 Created status and the created {@link Person}.
     */
    @PostMapping()
    public ResponseEntity<?> createPerson(@Valid @RequestBody PersonDTO personDTO) {
        Person createdPerson = personService.createPerson(personDTO);
        EntityModel<PersonDTO> createdPersonModel = personDTOModelAssembler.toModel(personMapper
                .toDTO(createdPerson));

        return responseBuilderService.buildCreatedResponseWithBody(createdPersonModel);
    }

    /**
     * Updates a {@link Person} based on its id.
     *
     * @param id the ID of the {@link Person} to be updated
     * @param personDTO a DTO containing the needed data
     * @return {@link ResponseEntity<>} containing a 204 No Content status
     * (Returns 404 Not Found if the to be updated {@link Person} does not exist in the database)
     */
    @PutMapping("/{id}")
    public ResponseEntity<Object> updatePerson(@PathVariable Long id, @Valid @RequestBody PersonDTO personDTO) {
        Person updatedPerson = personService.updatePerson(id, personDTO);
        EntityModel<PersonDTO> updatedPersonModel = personDTOModelAssembler.toModel(personMapper
                .toDTO(updatedPerson));

        return responseBuilderService.buildNoContentResponseWithLocation(updatedPersonModel);
    }

    /**
     * Deletes a {@link Person} by its {@code id}.
     *
     * @param id the ID of the {@link Person} to be deleted
     * @return {@link ResponseEntity<>} containing a 204 No Content status (or a
     * 404 Not Found status if no {@link Person} exists with the specified {@code id}.)
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deletePerson(@PathVariable Long id) {
        personService.deleteById(id);

        return ResponseEntity.noContent().build();
    }

// ------------------------------------- OTHER ----------------------------------------------------------------------

    /**
     * Fetches the {@link Country} where the {@link Person} with {@code id} was born.
     *
     * @param id the ID of the {@link Person} for which the {@link Country} is to be fetched
     * @return {@link ResponseEntity<>} containing a 200 Ok status and requested {@link Country} resource
     * (Returns 404 Not Found if the {@link Person} does not exist for this {@code id}).
     */
    @GetMapping("/{id}/country")
    public ResponseEntity<EntityModel<CountryDTO>> findCountryByPerson(@NotNull @PathVariable Long id) {
        Country country = personService.findCountryByPersonId(id);
        EntityModel<CountryDTO> countryDTOEntityModel = countryDTOModelAssembler.toModel(countryMapper.toDTO(country));

        return ResponseEntity.ok(countryDTOEntityModel);
    }

    /**
     * Fetches {@link Movie} resources associated with the {@link Person} identified
     * by {@code id} and {@code role}. Returns every {@link Movie} resource that the {@link Person} was
     * involved in if no {@code role} is specified.
     *
     * @param id id the ID of the {@link Person} for which the {@link Movie} resources are to be fetched
     * @param role the role that the {@link Person} has in the {@link Movie}
     * @return {@link ResponseEntity<>} containing a 200 Ok status and the requested {@link Movie} resources
     * (Returns 404 Not Found if no {@link Person} exists for this {@code id}).
     */
    @GetMapping("/{id}/movies")
    public ResponseEntity<CollectionModel<EntityModel<MovieDTO>>> findMoviesByPerson(
            @NotNull @PathVariable Long id,
            @RequestParam(required = false) String role) {
        List<Movie> foundMovies = personService.findMoviesByPersonIdAndRole(id, role);

        if (foundMovies.isEmpty()) { return ResponseEntity.ok(CollectionModel.empty()); }

        CollectionModel<EntityModel<MovieDTO>> collectionModel = collectionModelBuilderService
                .createCollectionModelFromList(foundMovies, movieMapper, movieDTOModelAssembler,
                        linkTo(methodOn(PersonController.class).findMoviesByPerson(id, role)).withSelfRel());

        return ResponseEntity.ok(collectionModel);
    }

    /**
     * Fetches {@link CastInfo} resources associated with the {@link Person} identified
     * by {@code id}.
     *
     * @param id id the ID of the {@link Person} for which the {@link CastInfo} resources are to be fetched
     * @return {@link ResponseEntity<>} containing a 200 Ok status and the requested {@link CastInfo} resources
     * (returns 404 Not Found if no {@link Person} exists for this {@code id}).
     */
    @GetMapping("/{id}/credits")
    public ResponseEntity<CollectionModel<EntityModel<CastInfoDTO>>> findCreditsByPerson(
            @NotNull @PathVariable Long id) {
        List<CastInfo> foundCredits = personService.findCastInfosByPersonId(id);

        if (foundCredits.isEmpty()) { return ResponseEntity.ok(CollectionModel.empty()); }

        CollectionModel<EntityModel<CastInfoDTO>> collectionModel = collectionModelBuilderService
                .createCollectionModelFromList(foundCredits, castInfoMapper, castInfoDTOModelAssembler,
                        linkTo(methodOn(PersonController.class).findCreditsByPerson(id)).withSelfRel());

        return ResponseEntity.ok(collectionModel);
    }

    /**
     * Fetches every {@link Role} resource that the {@link Person} identified by {@code id}
     * is credited with (each {@link Role} is only returned once).
     *
     * @param id id the ID of the {@link Person} for which the {@link Role} resources are to be fetched
     * @return {@link ResponseEntity<>} containing a 200 Ok status and the requested {@link Role} resources
     * (returns 404 Not Found if no {@link Person} exists for this {@code id}).
     */
    @GetMapping("/{id}/roles")
    public ResponseEntity<CollectionModel<EntityModel<RoleDTO>>> findRolesByPerson(@NotNull @PathVariable Long id) {
        List<Role> foundRoles = personService.findRolesByPersonId(id);

        if (foundRoles.isEmpty()) { return ResponseEntity.ok(CollectionModel.empty()); }

        CollectionModel<EntityModel<RoleDTO>> collectionModel = collectionModelBuilderService
                .createCollectionModelFromList(foundRoles, roleMapper, roleDTOModelAssembler,
                        linkTo(methodOn(PersonController.class).findRolesByPerson(id)).withSelfRel());

        return ResponseEntity.ok(collectionModel);
    }

    /**
     * Fetches {@link Person} resources based on search parameters.
     *
     * @param name name of the searched for person(s)
     * @param birthDate birthday of the searched for person(s)
     * @param deathDate date of death of the searched for person(s)
     * @param height height of the searched for person(s)
     * @param country country of birth of the searched for person(s)
     * @param age age of the searched for person(s)
     * @return ResponseEntity containing a 200 Ok status and a collection of the found
     * {@link Person} resources.
     */
    @GetMapping("/search")
    public ResponseEntity<CollectionModel<EntityModel<PersonDTO>>> searchPersons(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) LocalDate birthDate,
            @RequestParam(required = false) LocalDate deathDate,
            @RequestParam(required = false) String height,
            @RequestParam(required = false) String country,
            @RequestParam(required = false) Integer age) {

        List<Person> foundPersons = personService.findPersonsBySearchParams(name, birthDate, deathDate,
                height, country, age);

        if (foundPersons.isEmpty()) { return ResponseEntity.ok(CollectionModel.empty()); }

        CollectionModel<EntityModel<PersonDTO>> collectionModel = collectionModelBuilderService
                .createCollectionModelFromList(foundPersons, personMapper, personDTOModelAssembler,
                        linkTo(methodOn(PersonController.class).searchPersons(name,birthDate,deathDate,height,
                                country, age)).withSelfRel());

        return ResponseEntity.ok(collectionModel);
    }
}
