package at.saekenz.cinerator.controller;

import at.saekenz.cinerator.model.movie.Movie;
import at.saekenz.cinerator.model.movie.MovieModelAssembler;
import at.saekenz.cinerator.model.user.EUserSearchParam;
import at.saekenz.cinerator.model.user.User;
import at.saekenz.cinerator.model.user.UserModelAssembler;
import at.saekenz.cinerator.model.user.UserNotFoundException;
import at.saekenz.cinerator.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    IUserService userService;

    private final UserModelAssembler assembler;
    private final MovieModelAssembler movieAssembler;

    UserController(UserModelAssembler assembler, MovieModelAssembler movieAssembler) {
        this.assembler = assembler;
        this.movieAssembler = movieAssembler;
    }

    @GetMapping
    public ResponseEntity<CollectionModel<EntityModel<User>>> findAll() {
        List<User> users = userService.findAll();

        if (users.isEmpty()) {
            throw new UserNotFoundException();
        }

        List<EntityModel<User>> userModels = users.stream()
                .map(assembler::toModel)
                .toList();

        CollectionModel<EntityModel<User>> collectionModel = CollectionModel.of(userModels,
                linkTo(methodOn(UserController.class).findAll()).withSelfRel());

        return ResponseEntity.ok(collectionModel);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<User>> findById(@PathVariable Long id) {
        User user = userService.findById(id).orElseThrow(() -> new UserNotFoundException(id));
        return ResponseEntity.ok(assembler.toModel(user));
    }

    @PostMapping
    public ResponseEntity<?> createUser(@RequestBody User user) {
        EntityModel<User> entityModel = assembler.toModel(userService.save(user));
        return ResponseEntity.created(entityModel.getRequiredLink(IanaLinkRelations.SELF).toUri())
                .body(entityModel);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @RequestBody User newUser) {
        User updatedUser = userService.findById(id).map(
                user -> {
                    user.setUsername(newUser.getUsername());
                    user.setPassword(newUser.getPassword());
                    user.setEnabled(newUser.isEnabled());
                    user.setRole(newUser.getRole());
                    user.setWatchlist(newUser.getWatchlist());
                    user.setReviews(newUser.getReviews());
                    return userService.save(user);
                })
                .orElseGet(() -> userService.save(newUser));
        EntityModel<User> entityModel = assembler.toModel(updatedUser);

        return ResponseEntity
                .created(entityModel.getRequiredLink(IanaLinkRelations.SELF).toUri())
                .body(entityModel);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        if (userService.findById(id).isPresent()) {
            userService.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        else {
            throw new UserNotFoundException(id);
        }
    }

    @GetMapping("/username/{username}")
    public ResponseEntity<CollectionModel<EntityModel<User>>> findByUsername(@PathVariable String username) {
        List<User> usersByUsername = userService.findByUsername(username);

        if (usersByUsername.isEmpty()) {
            throw new UserNotFoundException(EUserSearchParam.USERNAME, username);
        }

        List<EntityModel<User>> userModels = usersByUsername.stream()
                .map(assembler::toModel)
                .toList();

        CollectionModel<EntityModel<User>> collectionModel = CollectionModel.of(userModels,
                linkTo(methodOn(UserController.class).findByUsername(username)).withSelfRel());

        return ResponseEntity.ok(collectionModel);
    }

    @GetMapping("/role/{role}")
    public ResponseEntity<CollectionModel<EntityModel<User>>> findUsersByRole(@PathVariable String role) {
        List<User> usersByRole = userService.findUsersByRole(role);

        if (usersByRole.isEmpty()) {
            throw new UserNotFoundException(EUserSearchParam.ROLE, role);
        }

        List<EntityModel<User>> userModels = usersByRole
                .stream()
                .map(assembler::toModel)
                .toList();

        CollectionModel<EntityModel<User>> collectionModel = CollectionModel.of(userModels,
                linkTo(methodOn(UserController.class).findUsersByRole(role)).withSelfRel());

        return ResponseEntity.ok(collectionModel);
    }

    @GetMapping("/{id}/watchlist")
    public ResponseEntity<CollectionModel<EntityModel<Movie>>> findWatchlistByUser(@PathVariable Long id) {
        User user = userService.findById(id).orElseThrow(() -> new UserNotFoundException(id));

        List<EntityModel<Movie>> movies = user.getWatchlist()
                .stream()
                .map(movieAssembler::toModel)
                .toList();

        CollectionModel<EntityModel<Movie>> collectionModel = CollectionModel.of(movies,
                linkTo(methodOn(UserController.class).findWatchlistByUser(id)).withSelfRel());

        return ResponseEntity.ok(collectionModel);
    }

    // for testing only
    @GetMapping("/currentUser")
    public String currentUserName(Authentication authentication) {
        return authentication.getName();
    }

}
