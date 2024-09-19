package at.saekenz.cinerator.controller;

import at.saekenz.cinerator.model.country.Country;
import at.saekenz.cinerator.model.country.CountryNotFoundException;
import at.saekenz.cinerator.model.person.Person;
import at.saekenz.cinerator.model.person.PersonDTO;
import at.saekenz.cinerator.model.person.PersonDTOModelAssembler;
import at.saekenz.cinerator.model.person.PersonMapper;
import at.saekenz.cinerator.service.ICountryService;
import at.saekenz.cinerator.service.IPersonService;
import at.saekenz.cinerator.util.CollectionModelBuilderService;
import at.saekenz.cinerator.util.ResponseBuilderService;
import jakarta.validation.Valid;
import org.hibernate.ObjectNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    public PersonController(PersonMapper personMapper,
                            PersonDTOModelAssembler personDTOModelAssembler) {
        this.personMapper = personMapper;
        this.personDTOModelAssembler = personDTOModelAssembler;
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

    /**
     * Creates a new {@link Person}.
     *
     * @param personDTO a DTO containing data of the new {@link Person}
     * @return ResponseEntity containing a 201 Created status and the created {@link Person}.
     */
    @PostMapping()
    public ResponseEntity<?> createPerson(@Valid @RequestBody PersonDTO personDTO) {
        Person newPerson = personMapper.toPerson(personDTO);
        Country newPersonsCountry = countryService.findById(personDTO.getBirthCountry().id())
                        .orElseThrow(() -> new CountryNotFoundException(personDTO.getBirthCountry().id()));

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
    public ResponseEntity<?> updatePerson(@PathVariable Long id, @RequestBody PersonDTO personDTO) {
        Person person = personService.findById(id).orElseThrow(
                () -> new ObjectNotFoundException(id, Person.class.getSimpleName()));

        person.setName(personDTO.getName());
        person.setBirthDate(personDTO.getBirthDate());
        person.setDeathDate(personDTO.getDeathDate());

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
    public ResponseEntity<?> deletePerson(@PathVariable Long id) {
        personService.findById(id).orElseThrow(
                () -> new ObjectNotFoundException(id, Person.class.getSimpleName()));
        personService.deleteById(id);

        return ResponseEntity.noContent().build();
    }
}
