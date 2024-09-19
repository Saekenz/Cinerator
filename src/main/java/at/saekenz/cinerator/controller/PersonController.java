package at.saekenz.cinerator.controller;

import at.saekenz.cinerator.model.country.Country;
import at.saekenz.cinerator.model.country.CountryDTO;
import at.saekenz.cinerator.model.country.CountryDTOModelAssembler;
import at.saekenz.cinerator.model.country.CountryMapper;
import at.saekenz.cinerator.model.person.Person;
import at.saekenz.cinerator.model.person.PersonDTO;
import at.saekenz.cinerator.model.person.PersonDTOModelAssembler;
import at.saekenz.cinerator.model.person.PersonMapper;
import at.saekenz.cinerator.service.ICountryService;
import at.saekenz.cinerator.service.IPersonService;
import at.saekenz.cinerator.util.CollectionModelBuilderService;
import at.saekenz.cinerator.util.ResponseBuilderService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.hibernate.ObjectNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/persons")
public class PersonController {

    @Autowired
    IPersonService personService;

    @Autowired
    ICountryService countryService;

    @Autowired
    CollectionModelBuilderService collectionModelBuilderService;

    @Autowired
    private ResponseBuilderService responseBuilderService;

    private final PersonMapper personMapper;
    private final PersonDTOModelAssembler personDTOModelAssembler;

    private final CountryMapper countryMapper;
    private final CountryDTOModelAssembler countryDTOModelAssembler;

    public PersonController(PersonMapper personMapper,
                            PersonDTOModelAssembler personDTOModelAssembler,
                            CountryMapper countryMapper,
                            CountryDTOModelAssembler countryDTOModelAssembler) {
        this.personMapper = personMapper;
        this.personDTOModelAssembler = personDTOModelAssembler;
        this.countryMapper = countryMapper;
        this.countryDTOModelAssembler = countryDTOModelAssembler;
    }

    /**
     * Fetch every {@link Person} from the database.
     *
     * @return ResponseEntity containing 200 Ok status and a collection of every
     * {@link Person} stored in the database.
     */
    @GetMapping
    public ResponseEntity<CollectionModel<EntityModel<PersonDTO>>> findAllPersons() {
        List<Person> persons = personService.findAll();

        if (persons.isEmpty()) { return ResponseEntity.ok(CollectionModel.empty()); }

        CollectionModel<EntityModel<PersonDTO>> collectionModel = collectionModelBuilderService
                .createCollectionModelFromList(persons, personMapper, personDTOModelAssembler,
                        linkTo(methodOn(PersonController.class).findAllPersons()).withSelfRel());

        return ResponseEntity.ok(collectionModel);
    }

    /**
     * Fetch a specific {@link Person} by its {@code id}.
     *
     * @param id the ID of the {@link Person} that will be retrieved.
     * @return ResponseEntity containing 200 Ok status and the {@link Person} resource.
     * (Returns 404 Not Found if the {@link Person} does not exist for this {@code id}.)
     */
    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<PersonDTO>> findPersonById(@PathVariable Long id) {
        Person person = personService.findById(id).orElseThrow(
                () -> new ObjectNotFoundException(id, Person.class.getSimpleName()));

        EntityModel<PersonDTO> entityModel = personDTOModelAssembler
                .toModel(personMapper.toDTO(person));

        return ResponseEntity.ok(entityModel);
    }

 // ------------------------------ CREATE/UPDATE/DELETE --------------------------------------------------------------

    /**
     * Creates a new {@link Person}.
     *
     * @param personDTO a DTO containing data of the new {@link Person}
     * @return ResponseEntity containing a 201 Created status and the created {@link Person}.
     */
    @PostMapping()
    public ResponseEntity<?> createPerson(@Valid @RequestBody PersonDTO personDTO) {
        Person newPerson = personMapper.toPerson(personDTO);
        Country newPersonsCountry = countryService.getReferenceById(personDTO.getBirthCountry().id());

        newPerson.setBirthCountry(newPersonsCountry);

        EntityModel<PersonDTO> createdPersonModel = personDTOModelAssembler.toModel(personMapper
                .toDTO(personService.save(newPerson)));

        return responseBuilderService.buildCreatedResponseWithBody(createdPersonModel);
    }

    /**
     * Updates a {@link Person} based on its id.
     *
     * @param id the ID of the {@link Person} to be updated
     * @param personDTO a DTO containing the needed data
     * @return ResponseEntity containing a 204 No Content status
     * (Returns 404 Not Found if the to be updated {@link Person} does not exist in the database)
     */
    @PutMapping("/{id}")
    public ResponseEntity<Object> updatePerson(@PathVariable Long id, @Valid @RequestBody PersonDTO personDTO) {
        Person person = personService.findById(id).orElseThrow(
                () -> new ObjectNotFoundException(id, Person.class.getSimpleName()));

        if (!Objects.equals(personDTO.getBirthCountry().id(), person.getBirthCountry().getId())) {
            Country updatedCountry = countryService.getReferenceById(personDTO.getBirthCountry().id());
            person.setBirthCountry(updatedCountry);
        }

        person.setName(personDTO.getName());
        person.setBirthDate(personDTO.getBirthDate());
        person.setDeathDate(personDTO.getDeathDate());
        person.setHeight(personDTO.getHeight());

        EntityModel<PersonDTO> updatedPersonModel = personDTOModelAssembler.toModel(personMapper
                .toDTO(personService.save(person)));

        return responseBuilderService.buildNoContentResponseWithLocation(updatedPersonModel);
    }

    /**
     * Deletes a {@link Person} by its {@code id}.
     *
     * @param id the ID of the {@link Person} to be deleted
     * @return ResponseEntity containing a 204 No Content status (or a
     * 404 Not Found status if no {@link Person} exists with the specified {@code id}.)
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deletePerson(@PathVariable Long id) {
        personService.findById(id).orElseThrow(
                () -> new ObjectNotFoundException(id, Person.class.getSimpleName()));
        personService.deleteById(id);

        return ResponseEntity.noContent().build();
    }

// ------------------------------------- OTHER ----------------------------------------------------------------------

    /**
     * Fetches the {@link Country} where the {@link Person} with {@code id} was born.
     *
     * @param id the ID of the {@link Person} for which the {@link Country} is to be fetched
     * @return ResponseEntity containing a 200 Ok status and requested {@link Country} resource
     * (Returns 404 Not Found if the {@link Person} does not exist for this {@code id}).
     */
    @GetMapping("/{id}/country")
    public ResponseEntity<EntityModel<CountryDTO>> findCountryByPerson(@NotNull @PathVariable Long id) {
        Country country = personService.findById(id).orElseThrow(
                () -> new ObjectNotFoundException(id, Person.class.getSimpleName())).getBirthCountry();

        EntityModel<CountryDTO> countryDTOEntityModel = countryDTOModelAssembler.toModel(countryMapper.toDTO(country));

        return ResponseEntity.ok(countryDTOEntityModel);
    }

    /**
     * Fetches {@link Person} resources based on search parameters.
     *
     * @param name name of the searched for person(s)
     * @param birthDate birthday of the searched for person(s)
     * @param deathDate date of death of the searched for person(s)
     * @param height height of the searched for person(s)
     * @return ResponseEntity containing a 200 Ok status and a collection of the found
     * {@link Person} resources.
     */
    @GetMapping("/search")
    public ResponseEntity<CollectionModel<EntityModel<PersonDTO>>> searchPersons(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) LocalDate birthDate,
            @RequestParam(required = false) LocalDate deathDate,
            @RequestParam(required = false) String height) {

        List<Person> foundPersons = personService.findPersonsBySearchParams(name,birthDate,deathDate,height);

        if (foundPersons.isEmpty()) { return ResponseEntity.ok(CollectionModel.empty()); }

        CollectionModel<EntityModel<PersonDTO>> collectionModel = collectionModelBuilderService
                .createCollectionModelFromList(foundPersons, personMapper, personDTOModelAssembler,
                        linkTo(methodOn(PersonController.class).searchPersons(name,birthDate,deathDate,height))
                                .withSelfRel());

        return ResponseEntity.ok(collectionModel);
    }




}
