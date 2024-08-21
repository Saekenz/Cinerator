package at.saekenz.cinerator.controller;

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

    UserController(UserModelAssembler assembler) {
        this.assembler = assembler;
    }

    @GetMapping
    public CollectionModel<EntityModel<User>> findAll() {
        List<EntityModel<User>> users = userService.findAll().stream()
                .map(assembler::toModel)
                .toList();

        return CollectionModel.of(users, linkTo(methodOn(UserController.class).findAll()).withSelfRel());
    }

    @GetMapping("/{id}")
    public EntityModel<User> findById(@PathVariable Long id) {
        User user = userService.findById(id).orElseThrow(() -> new UserNotFoundException(id));

        return assembler.toModel(user);
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
        userService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/username/{username}")
    public CollectionModel<EntityModel<User>> findByUsername(@PathVariable String username) {
        List<EntityModel<User>> users = userService.findByUsername(username).stream()
                .map(assembler::toModel)
                .toList();

        if (users.isEmpty()) {
            throw new UserNotFoundException("username", username);
        }

        return CollectionModel.of(users, linkTo(methodOn(UserController.class).findByUsername(username)).withSelfRel());
    }

    @GetMapping("/role/{role}")
    public CollectionModel<EntityModel<User>> findUsersByRole(@PathVariable String role) {
        List<EntityModel<User>> users = userService.findUsersByRole(role).stream()
                .map(assembler::toModel)
                .toList();

        if (users.isEmpty()) {
            throw new UserNotFoundException("role", role);
        }

        return CollectionModel.of(users, linkTo(methodOn(UserController.class).findUsersByRole(role)).withSelfRel());
    }

//    @GetMapping("/{id}/watchlist")
//    public CollectionModel<EntityModel<Movie>> findWatchlistByUser(@PathVariable Long id) {
//
//    }

    // for testing only
    @GetMapping("/currentUser")
    public String currentUserName(Authentication authentication) {
        return authentication.getName();
    }

}
