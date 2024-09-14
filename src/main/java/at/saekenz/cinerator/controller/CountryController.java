package at.saekenz.cinerator.controller;

import at.saekenz.cinerator.model.country.Country;
import at.saekenz.cinerator.model.country.CountryDTO;
import at.saekenz.cinerator.model.country.CountryDTOModelAssembler;
import at.saekenz.cinerator.model.country.CountryMapper;
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
    CollectionModelBuilderService collectionModelBuilderService;

    private final CountryDTOModelAssembler countryDTOAssembler;

    public CountryController(CountryDTOModelAssembler countryDTOAssembler) {
        this.countryDTOAssembler = countryDTOAssembler;
    }

    /**
     *
     *
     * @return
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
     *
     *
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<CountryDTO>> findCountryById(@PathVariable Long id) {
        Country country = countryService.findById(id).orElseThrow(() -> new ObjectNotFoundException(id, Country.class.getSimpleName()));

        return ResponseEntity
                .ok(countryDTOAssembler.toModel(countryMapper.toDTO(country)));
    }
}
