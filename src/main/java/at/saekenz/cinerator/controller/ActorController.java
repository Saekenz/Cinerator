package at.saekenz.cinerator.controller;

import at.saekenz.cinerator.model.actor.Actor;
import at.saekenz.cinerator.model.actor.ActorModelAssembler;
import at.saekenz.cinerator.model.actor.ActorNotFoundException;
import at.saekenz.cinerator.service.IActorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/actors")
public class ActorController {

    @Autowired
    IActorService actorService;

    private final ActorModelAssembler actorAssembler;

    public ActorController(IActorService actorService) {
        this.actorAssembler = new ActorModelAssembler();
    }

    @GetMapping
    public ResponseEntity<List<Actor>> findAll() {
        List<Actor> actors = actorService.findAll();
        return ResponseEntity.ok().body(actors);
    }

    @GetMapping("/{actor_id}")
    public ResponseEntity<EntityModel<Actor>> findById(@PathVariable Long actor_id) {
        Actor actor = actorService.findById(actor_id).orElseThrow(() -> new ActorNotFoundException(actor_id));
        return ResponseEntity.ok(actorAssembler.toModel(actor));
    }
}
