package at.saekenz.cinerator.controller;

import at.saekenz.cinerator.model.actor.Actor;
import at.saekenz.cinerator.model.actor.ActorModelAssembler;
import at.saekenz.cinerator.model.actor.ActorNotFoundException;
import at.saekenz.cinerator.model.actor.EActorSearchParam;
import at.saekenz.cinerator.model.movie.Movie;
import at.saekenz.cinerator.model.movie.MovieModelAssembler;
import at.saekenz.cinerator.model.movie.MovieNotFoundException;
import at.saekenz.cinerator.service.IActorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Objects;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/actors")
public class ActorController {

    @Autowired
    IActorService actorService;

    private final ActorModelAssembler actorAssembler;
    private final MovieModelAssembler movieAssembler;

    public ActorController(ActorModelAssembler actorAssembler,
                           MovieModelAssembler movieAssembler) {
        this.actorAssembler = actorAssembler;
        this.movieAssembler = movieAssembler;
    }

    @GetMapping
    public ResponseEntity<CollectionModel<EntityModel<Actor>>> findAll() {
        List<Actor> actors = actorService.findAll();

        if (actors.isEmpty()) { return ResponseEntity.ok().build(); }

        List<EntityModel<Actor>> actorModels = actors.stream()
                .map(actorAssembler::toModel)
                .toList();

        CollectionModel<EntityModel<Actor>> collectionModel = CollectionModel.of(actorModels,
                linkTo(methodOn(ActorController.class).findAll()).withSelfRel());

        return ResponseEntity.ok(collectionModel);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<Actor>> findById(@PathVariable Long id) {
        Actor actor = actorService.findById(id).orElseThrow(() -> new ActorNotFoundException(id));
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

    @GetMapping("/birthDate/{birthDate}")
    public ResponseEntity<CollectionModel<EntityModel<Actor>>> findByBirthDate(@PathVariable String birthDate) {
        LocalDate parsedBirthDate;

        try {
            parsedBirthDate = LocalDate.parse(birthDate);
        }
        catch (DateTimeParseException e) {
            throw new ActorNotFoundException(EActorSearchParam.BIRTH_DATE, birthDate);
        }

        List<Actor> actors = actorService.findByBirthDate(parsedBirthDate);

        if (actors.isEmpty()) { throw new ActorNotFoundException(EActorSearchParam.BIRTH_DATE, birthDate); }

        List<EntityModel<Actor>> actorModels = actors.stream()
                .map(actorAssembler::toModel)
                .toList();

        CollectionModel<EntityModel<Actor>> collectionModel = CollectionModel.of(actorModels,
                linkTo(methodOn(ActorController.class).findByBirthDate(birthDate)).withSelfRel());

        return ResponseEntity.ok(collectionModel);
    }

    @GetMapping("/birthCountry/{birthCountry}")
    public ResponseEntity<CollectionModel<EntityModel<Actor>>> findByBirthCountry(@PathVariable String birthCountry) {
        List<Actor> actors = actorService.findByBirthCountry(birthCountry);

        if (actors.isEmpty()) { throw new ActorNotFoundException(EActorSearchParam.BIRTH_COUNTRY, birthCountry); }

        List<EntityModel<Actor>> actorModels = actors.stream()
                .map(actorAssembler::toModel)
                .toList();

        CollectionModel<EntityModel<Actor>> collectionModel = CollectionModel.of(actorModels,
                linkTo(methodOn(ActorController.class).findByBirthCountry(birthCountry)).withSelfRel());

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

    @GetMapping("/search")
    public ResponseEntity<CollectionModel<EntityModel<Actor>>> searchActors(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate birthDate,
            @RequestParam(required = false) String birthCountry,
            @RequestParam(required = false) Integer age) {

        List<Actor> actors = actorService.searchActors(name, birthDate, birthCountry, age);

        // return empty body if no actors were found
        if (actors.isEmpty()) { return ResponseEntity.ok().build(); }

        List<EntityModel<Actor>> actorModels = actors.stream()
                .map(actorAssembler::toModel)
                .toList();

        CollectionModel<EntityModel<Actor>> collectionModel = CollectionModel.of(actorModels,
                linkTo(methodOn(ActorController.class).searchActors(name,birthDate,birthCountry,age)).withSelfRel());

        return ResponseEntity.ok(collectionModel);
    }

    @PostMapping()
    public ResponseEntity<?> createActor(@RequestBody Actor actor) {
        EntityModel<Actor> actorModel = actorAssembler.toModel(actorService.save(actor));

        return ResponseEntity
                .created(actorModel.getRequiredLink(IanaLinkRelations.SELF).toUri())
                .body(actorModel);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateActor(@PathVariable Long id, @RequestBody Actor newActor) {
        Actor updatedActor = actorService.findById(id).map(
                actor -> {
                    actor.setName(newActor.getName());
                    actor.setBirthDate(newActor.getBirthDate());
                    actor.setBirthCountry(newActor.getBirthCountry());
                    actor.setAge(newActor.getAge());
                    return actorService.save(actor);
                })
                .orElseGet(() -> actorService.save(newActor));
        EntityModel<Actor> entityModel = actorAssembler.toModel(updatedActor);

        return ResponseEntity
                .created(entityModel.getRequiredLink(IanaLinkRelations.SELF).toUri())
                .body(entityModel);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteActor(@PathVariable Long id) {
        if (actorService.findById(id).isPresent()) {
            actorService.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        else {
            throw new ActorNotFoundException(id);
        }
    }

    @GetMapping("/{id}/movies")
    public ResponseEntity<CollectionModel<EntityModel<Movie>>> findMoviesById(@PathVariable Long id) {
        Actor actor = actorService.findById(id).orElseThrow(() -> new ActorNotFoundException(id));

        List<Movie> movies = actor.getMovies();
        if (movies.isEmpty()) { return ResponseEntity.ok().build(); }

        List<EntityModel<Movie>> movieModels = movies.stream()
                .map(movieAssembler::toModel)
                .toList();


        CollectionModel<EntityModel<Movie>> collectionModel = CollectionModel.of(movieModels,
                linkTo(methodOn(ActorController.class).findMoviesById(id)).withSelfRel());

        return ResponseEntity.ok(collectionModel);
    }

    @GetMapping("/{actorId}/movies/{movieId}")
    public ResponseEntity<EntityModel<Movie>> findMovieById(@PathVariable Long actorId, @PathVariable Long movieId) {
        Actor actor = actorService.findById(actorId).orElseThrow(() -> new ActorNotFoundException(actorId));
        Movie movie = actor.getMovies().stream()
                .filter(m -> Objects.equals(m.getId(), movieId))
                .findFirst()
                .orElseThrow(() -> new MovieNotFoundException(movieId));

        return ResponseEntity.ok(movieAssembler.toModel(movie));
    }
}
