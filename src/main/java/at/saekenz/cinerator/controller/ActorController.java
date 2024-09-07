package at.saekenz.cinerator.controller;

import at.saekenz.cinerator.model.actor.Actor;
import at.saekenz.cinerator.model.actor.ActorModelAssembler;
import at.saekenz.cinerator.model.actor.ActorNotFoundException;
import at.saekenz.cinerator.model.actor.EActorSearchParam;
import at.saekenz.cinerator.model.movie.Movie;
import at.saekenz.cinerator.model.movie.MovieModelAssembler;
import at.saekenz.cinerator.model.movie.MovieNotFoundException;
import at.saekenz.cinerator.model.user.User;
import at.saekenz.cinerator.service.IActorService;
import at.saekenz.cinerator.util.ResponseBuilderService;
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
import java.util.Optional;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/actors")
public class ActorController {

    @Autowired
    IActorService actorService;

    private final ActorModelAssembler actorAssembler;
    private final MovieModelAssembler movieAssembler;

    @Autowired
    private ResponseBuilderService responseBuilderService;

    public ActorController(ActorModelAssembler actorAssembler,
                           MovieModelAssembler movieAssembler) {
        this.actorAssembler = actorAssembler;
        this.movieAssembler = movieAssembler;
    }

    /**
     *
     * @return HTTP code 200 and every {@link Actor} stored in the database
     * (returns an empty list if no actors were stored in the database yet)
     */
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

    /**
     *
     * @param id number of the {@link Actor} that is to be retrieved
     * @return HTTP code 200 and a JSON representation of the requested {@link Actor} (or
     * HTTP code 404 if the resource was not found
     */
    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<Actor>> findById(@PathVariable Long id) {
        Actor actor = actorService.findById(id).orElseThrow(() -> new ActorNotFoundException(id));
        return ResponseEntity.ok(actorAssembler.toModel(actor));
    }

// ---------------------------------------- SEARCH -------------------------------------------------------------------

    /**
     *
     * @param name name of searched for {@link Actor} objects
     * @return list of {@link Actor} objects and HTTP code 200 if any users were found.
     * HTTP code 404 otherwise
     */
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

    /**
     *
     * @param birthDate birthday of the searched for {@link Actor} objects
     * @return list of {@link Actor} objects and HTTP code 200 if any users were found.
     * HTTP code 404 otherwise
     */
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

    /**
     *
     * @param birthCountry country of birth of searched for {@link Actor} objects
     * @return list of {@link Actor} objects and HTTP code 200 if any users were found.
     * HTTP code 404 otherwise
     */
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

    /**
     *
     * @param age age of searched for {@link Actor} objects
     * @return list of {@link Actor} objects and HTTP code 200 if any users were found.
     * HTTP code 404 otherwise
     */
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

    /**
     *
     * @param name name of searched for actors
     * @param birthDate birthday of searched for actors
     * @param birthCountry country of birth of searched for actors
     * @param age age of searched for actors
     * @return {@link CollectionModel} object containing actors matching search parameters
     */
    @GetMapping("/search")
    public ResponseEntity<CollectionModel<EntityModel<Actor>>> searchActors(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate birthDate,
            @RequestParam(required = false) String birthCountry,
            @RequestParam(required = false) Integer age) {

        List<Actor> actors = actorService.searchActors(name, birthDate, birthCountry, age);

        // return empty body if no actors were found
        if (actors.isEmpty()) { return ResponseEntity.ok(CollectionModel.empty()); }

        List<EntityModel<Actor>> actorModels = actors.stream()
                .map(actorAssembler::toModel)
                .toList();

        CollectionModel<EntityModel<Actor>> collectionModel = CollectionModel.of(actorModels,
                linkTo(methodOn(ActorController.class).searchActors(name,birthDate,birthCountry,age)).withSelfRel());

        return ResponseEntity.ok(collectionModel);
    }

    /**
     *
     * @param actor new {@link Actor} object that will be added to the database
     * @return HTTP code 201 and the {@link Actor} resource that was created
     */
    @PostMapping()
    public ResponseEntity<?> createActor(@RequestBody Actor actor) {
        EntityModel<Actor> actorModel = actorAssembler.toModel(actorService.save(actor));

        return ResponseEntity
                .created(actorModel.getRequiredLink(IanaLinkRelations.SELF).toUri())
                .body(actorModel);
    }

    /**
     *
     * @param id number of the {@link Actor} that is to be updated (or added if the number does not exist in the database yet)
     * @param newActor information about the to be updated/added {@link Actor} object
     * @return HTTP code 201 and the created {@link Actor} object (or HTTP code 204 if an existing {@link Actor} object was updated)
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateActor(@PathVariable Long id, @RequestBody Actor newActor) {
        Optional<Actor> existingActor = actorService.findById(id);

        Actor updatedActor = existingActor.map(
                actor -> {
                    actor.setName(newActor.getName());
                    actor.setBirthDate(newActor.getBirthDate());
                    actor.setBirthCountry(newActor.getBirthCountry());
                    actor.setAge(newActor.getAge());
                    return actorService.save(actor);
                })
                .orElseGet(() -> actorService.save(newActor));

        EntityModel<Actor> entityModel = actorAssembler.toModel(updatedActor);

        if (existingActor.isPresent()) {
            return responseBuilderService.buildNoContentResponseWithLocation(entityModel);
        }
        else {
            return ResponseEntity
                    .created(entityModel.getRequiredLink(IanaLinkRelations.SELF).toUri())
                    .body(entityModel);
        }
    }

    /**
     *
     * @param id number of {@link Actor} that is to be removed from the database
     * @return HTTP code 204 if {@link Actor} was deleted. HTTP code 404 if {@link Movie} was not found
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteActor(@PathVariable Long id) {
        actorService.findById(id).orElseThrow(() -> new ActorNotFoundException(id));
        actorService.deleteById(id);

        return ResponseEntity.noContent().build();
    }

    /**
     *
     * @param id number of the {@link Actor} for which movies will be retrieved
     * @return HTTP code 200 and {@link CollectionModel} containing movies (will be empty if no movies
     * were added yet) or HTTP code 404 if the {@link Actor} resource does not exist
     */
    @GetMapping("/{id}/movies")
    public ResponseEntity<CollectionModel<EntityModel<Movie>>> findMoviesById(@PathVariable Long id) {
        Actor actor = actorService.findById(id).orElseThrow(() -> new ActorNotFoundException(id));

        List<Movie> movies = actor.getMovies();
        if (movies.isEmpty()) { return ResponseEntity.ok(CollectionModel.empty()); }

        List<EntityModel<Movie>> movieModels = movies.stream()
                .map(movieAssembler::toModel)
                .toList();

        CollectionModel<EntityModel<Movie>> collectionModel = CollectionModel.of(movieModels,
                linkTo(methodOn(ActorController.class).findMoviesById(id)).withSelfRel());

        return ResponseEntity.ok(collectionModel);
    }

    /**
     *
     * @param actorId number of the {@link Actor}
     * @param movieId number of the specific {@link Movie} that will be fetched
     * @return HTTP code 200 and a JSON representation of the {@link Movie} resource
     * or HTTP code 404 if the {@link Actor}/{@link Movie} are not found
     */
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
