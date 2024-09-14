package at.saekenz.cinerator.model.country;

import at.saekenz.cinerator.controller.CountryController;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class CountryDTOModelAssembler implements RepresentationModelAssembler<CountryDTO, EntityModel<CountryDTO>> {

    @Override
    public EntityModel<CountryDTO> toModel(CountryDTO countryDTO) {
        return EntityModel.of(countryDTO,
                linkTo(methodOn(CountryController.class).findCountryById(countryDTO.id())).withSelfRel());
    }
}
