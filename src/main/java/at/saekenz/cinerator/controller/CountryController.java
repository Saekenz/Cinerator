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
import java.util.Set;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/countries")
public class CountryController {

    @Autowired
    ICountryService countryService;

    @Autowired
    CountryMapper countryMapper;

    @Autowired
    PersonMapper personMapper;

    @Autowired
    CollectionModelBuilderService collectionModelBuilderService;

    private final CountryDTOModelAssembler countryDTOAssembler;
    private final PersonDTOModelAssembler personDTOModelAssembler;

    public CountryController(CountryDTOModelAssembler countryDTOAssembler,
                             PersonDTOModelAssembler personDTOModelAssembler) {
        this.countryDTOAssembler = countryDTOAssembler;
        this.personDTOModelAssembler = personDTOModelAssembler;
    }

    /**
     * Fetch every {@link Country} from the database.
     *
     * @return ResponseEntity containing 200 Ok status and a collection of every
     * {@link Country} stored in the database.
     */
    @GetMapping()
    public ResponseEntity<CollectionModel<EntityModel<CountryDTO>>> findAllCountries() {
        List<Country> countries = countryService.findAll();

        if (countries.isEmpty()) { return ResponseEntity.ok(CollectionModel.empty()); }

        CollectionModel<EntityModel<CountryDTO>> collectionModel = collectionModelBuilderService
                .createCollectionModelFromList(countries, countryMapper, countryDTOAssembler,
                        linkTo(methodOn(CountryController.class).findAllCountries()).withSelfRel());

        return ResponseEntity.ok(collectionModel);
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
        Country country = countryService.findById(id).orElseThrow(
                () -> new ObjectNotFoundException(id, Country.class.getSimpleName()));

        return ResponseEntity
                .ok(countryDTOAssembler.toModel(countryMapper.toDTO(country)));
    }

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
        Country country = countryService.findById(id).orElseThrow(
                () -> new ObjectNotFoundException(id, Country.class.getSimpleName()));

        Set<Person> persons = country.getPersons();

        if (persons.isEmpty()) { return ResponseEntity.ok(CollectionModel.empty()); }

        CollectionModel<EntityModel<PersonDTO>> collectionModel = collectionModelBuilderService
                .createCollectionModelFromList(persons, personMapper, personDTOModelAssembler,
                        linkTo(methodOn(CountryController.class).findPersonsByCountry(id)).withSelfRel());

        return ResponseEntity
                .ok(collectionModel);
    }
}
