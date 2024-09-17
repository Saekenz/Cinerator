package at.saekenz.cinerator.controller;

import at.saekenz.cinerator.model.person.Person;
import at.saekenz.cinerator.model.person.PersonDTO;
import at.saekenz.cinerator.model.person.PersonDTOModelAssembler;
import at.saekenz.cinerator.model.person.PersonMapper;
import at.saekenz.cinerator.service.IPersonService;
import at.saekenz.cinerator.util.CollectionModelBuilderService;
import org.hibernate.ObjectNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
