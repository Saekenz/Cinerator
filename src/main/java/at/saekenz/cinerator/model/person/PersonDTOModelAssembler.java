package at.saekenz.cinerator.model.person;

import at.saekenz.cinerator.controller.PersonController;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class PersonDTOModelAssembler implements RepresentationModelAssembler<PersonDTO, EntityModel<PersonDTO>> {

    @Override
    public EntityModel<PersonDTO> toModel(PersonDTO person) {
        return EntityModel.of(person,
                linkTo(methodOn(PersonController.class).getPersonById(person.getId())).withSelfRel());
    }
}
