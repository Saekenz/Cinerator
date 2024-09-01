package at.saekenz.cinerator.model.actor;

import at.saekenz.cinerator.controller.ActorController;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class ActorModelAssembler implements RepresentationModelAssembler<Actor, EntityModel<Actor>> {

    @Override
    public EntityModel<Actor> toModel(Actor actor) {

        return EntityModel.of(actor,
                linkTo(methodOn(ActorController.class).findById(actor.getId())).withSelfRel(),
                linkTo(methodOn(ActorController.class).findMoviesById(actor.getId())).withRel("movies"),
                linkTo(methodOn(ActorController.class).findByBirthCountry(actor.getBirthCountry())).withRel("country"),
                linkTo(methodOn(ActorController.class).findByAge(actor.getAge())).withRel("age"),
                linkTo(methodOn(ActorController.class).findAll()).withRel("actors"));
    }

}
