package at.saekenz.cinerator.controller;

import at.saekenz.cinerator.model.actor.Actor;
import at.saekenz.cinerator.model.actor.ActorModelAssembler;
import at.saekenz.cinerator.model.actor.ActorNotFoundException;
import at.saekenz.cinerator.model.actor.EActorSearchParam;
import at.saekenz.cinerator.service.IActorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/actors")
public class ActorController {

    @Autowired
    IActorService actorService;

    private final ActorModelAssembler actorAssembler;

    public ActorController(ActorModelAssembler actorAssembler) {
        this.actorAssembler = actorAssembler;
    }

    @GetMapping
    public ResponseEntity<CollectionModel<EntityModel<Actor>>> findAll() {
        List<Actor> actors = actorService.findAll();

        if (actors.isEmpty()) { throw new ActorNotFoundException(); }

        List<EntityModel<Actor>> actorModels = actors.stream()
                .map(actorAssembler::toModel)
                .toList();

        CollectionModel<EntityModel<Actor>> collectionModel = CollectionModel.of(actorModels,
                linkTo(methodOn(ActorController.class).findAll()).withSelfRel());

        return ResponseEntity.ok(collectionModel);
    }

    @GetMapping("/{actor_id}")
    public ResponseEntity<EntityModel<Actor>> findById(@PathVariable Long actor_id) {
        Actor actor = actorService.findById(actor_id).orElseThrow(() -> new ActorNotFoundException(actor_id));
        return ResponseEntity.ok(actorAssembler.toModel(actor));
    }

    @GetMapping("/name/{name}")
    public ResponseEntity<CollectionModel<EntityModel<Actor>>> findByName(@PathVariable String name) {
        List<Actor> actors = actorService.findByName(name);

        if (actors.isEmpty()) { throw new ActorNotFoundException(EActorSearchParam.NAME, name); }

        List<EntityModel<Actor>> actorModels = actors.stream()
                .map(actorAssembler::toModel)
                .toList();

        CollectionModel<EntityModel<Actor>> collectionModel = CollectionModel.of(actorModels,
                linkTo(methodOn(ActorController.class).findByName(name)).withSelfRel());

        return ResponseEntity.ok(collectionModel);
    }

    @GetMapping("/birth_date/{birth_date}")
    public ResponseEntity<CollectionModel<EntityModel<Actor>>> findByBirthDate(@PathVariable String birth_date) {
        LocalDate parsedBirthDate;

        try {
            parsedBirthDate = LocalDate.parse(birth_date);
        }
        catch (DateTimeParseException e) {
            throw new ActorNotFoundException(EActorSearchParam.BIRTH_DATE, birth_date);
        }

        List<Actor> actors = actorService.findByBirthDate(parsedBirthDate);

        if (actors.isEmpty()) { throw new ActorNotFoundException(EActorSearchParam.BIRTH_DATE, birth_date); }

        List<EntityModel<Actor>> actorModels = actors.stream()
                .map(actorAssembler::toModel)
                .toList();

        CollectionModel<EntityModel<Actor>> collectionModel = CollectionModel.of(actorModels,
                linkTo(methodOn(ActorController.class).findByBirthDate(birth_date)).withSelfRel());

        return ResponseEntity.ok(collectionModel);
    }

    @GetMapping("/birth_country/{birth_country}")
    public ResponseEntity<CollectionModel<EntityModel<Actor>>> findByBirthCountry(@PathVariable String birth_country) {
        List<Actor> actors = actorService.findByBirthCountry(birth_country);

        if (actors.isEmpty()) { throw new ActorNotFoundException(EActorSearchParam.BIRTH_COUNTRY, birth_country); }

        List<EntityModel<Actor>> actorModels = actors.stream()
                .map(actorAssembler::toModel)
                .toList();

        CollectionModel<EntityModel<Actor>> collectionModel = CollectionModel.of(actorModels,
                linkTo(methodOn(ActorController.class).findByBirthCountry(birth_country)).withSelfRel());

        return ResponseEntity.ok(collectionModel);
    }

    @GetMapping("/age/{age}")
    public ResponseEntity<CollectionModel<EntityModel<Actor>>> findByAge(@PathVariable int age) {
        List<Actor> actors = actorService.findByAge(age);

        if (actors.isEmpty()) { throw new ActorNotFoundException(EActorSearchParam.AGE, age+""); }

        List<EntityModel<Actor>> actorModels = actors.stream()
                .map(actorAssembler::toModel)
                .toList();

        CollectionModel<EntityModel<Actor>> collectionModel = CollectionModel.of(actorModels,
                linkTo(methodOn(ActorController.class).findByAge(age)).withSelfRel());

        return ResponseEntity.ok(collectionModel);
    }

//    @GetMapping("/searchCountry")
//    public ResponseEntity<CollectionModel<EntityModel<Actor>>> searchActorsCountry(@RequestParam String country) {
//        List<Actor> actors = actorService.findByBirthCountry(country);
//
//        if (actors.isEmpty()) { throw new ActorNotFoundException(EActorSearchParam.BIRTH_COUNTRY, country); }
//
//        List<EntityModel<Actor>> actorModels = actors.stream()
//                .map(actorAssembler::toModel)
//                .toList();
//
//        CollectionModel<EntityModel<Actor>> collectionModel = CollectionModel.of(actorModels,
//                linkTo(methodOn(ActorController.class).searchActorsCountry(country)).withSelfRel());
//
//        return ResponseEntity.ok(collectionModel);
//    }

    @GetMapping("/search")
    public ResponseEntity<CollectionModel<EntityModel<Actor>>> searchActors(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String birth_date,
            @RequestParam(required = false) String birth_country,
            @RequestParam(required = false) Integer age) {

        LocalDate parsedBirthDate = null;
        if  (birth_date != null) {
            try {
                parsedBirthDate = LocalDate.parse(birth_date);
            } catch (DateTimeParseException e) {
                throw new ActorNotFoundException(EActorSearchParam.BIRTH_DATE, birth_date);
            }
        }

        List<Actor> actors = actorService.searchActors(name, parsedBirthDate, birth_country, age);

        if (actors.isEmpty()) { throw new ActorNotFoundException(); }

        List<EntityModel<Actor>> actorModels = actors.stream()
                .map(actorAssembler::toModel)
                .toList();

        CollectionModel<EntityModel<Actor>> collectionModel = CollectionModel.of(actorModels,
                linkTo(methodOn(ActorController.class).searchActors(name,birth_date,birth_country,age)).withSelfRel());

        return ResponseEntity.ok(collectionModel);
    }

    @PostMapping()
    public ResponseEntity<?> createActor(@RequestBody Actor actor) {
        EntityModel<Actor> actorModel = actorAssembler.toModel(actorService.save(actor));

        return ResponseEntity
                .created(actorModel.getRequiredLink(IanaLinkRelations.SELF).toUri())
                .body(actorModel);
    }

    @PutMapping("/{actor_id}")
    public ResponseEntity<?> updateActor(@PathVariable Long actor_id, @RequestBody Actor newActor) {
        Actor updatedActor = actorService.findById(actor_id).map(
                actor -> {
                    actor.setName(newActor.getName());
                    actor.setBirth_date(newActor.getBirth_date());
                    actor.setBirth_country(newActor.getBirth_country());
                    actor.setAge(newActor.getAge());
                    return actorService.save(actor);
                })
                .orElseGet(() -> actorService.save(newActor));
        EntityModel<Actor> entityModel = actorAssembler.toModel(updatedActor);

        return ResponseEntity
                .created(entityModel.getRequiredLink(IanaLinkRelations.SELF).toUri())
                .body(entityModel);
    }

    @DeleteMapping("/{actor_id}")
    public ResponseEntity<?> deleteActor(@PathVariable Long actor_id) {
        if (actorService.findById(actor_id).isPresent()) {
            actorService.deleteById(actor_id);
            return ResponseEntity.noContent().build();
        }
        else {
            throw new ActorNotFoundException(actor_id);
        }
    }
}
