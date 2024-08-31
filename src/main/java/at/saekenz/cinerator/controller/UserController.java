package at.saekenz.cinerator.controller;

import at.saekenz.cinerator.model.movie.Movie;
import at.saekenz.cinerator.model.movie.MovieModelAssembler;
import at.saekenz.cinerator.model.movie.MovieNotFoundException;
import at.saekenz.cinerator.model.review.Review;
import at.saekenz.cinerator.model.review.ReviewModelAssembler;
import at.saekenz.cinerator.model.user.EUserSearchParam;
import at.saekenz.cinerator.model.user.User;
import at.saekenz.cinerator.model.user.UserModelAssembler;
import at.saekenz.cinerator.model.user.UserNotFoundException;
import at.saekenz.cinerator.service.IMovieService;
import at.saekenz.cinerator.service.IUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    IUserService userService;

    @Autowired
    IMovieService movieService;

    private final UserModelAssembler assembler;
    private final MovieModelAssembler movieAssembler;
    private final ReviewModelAssembler reviewAssembler;

    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    UserController(UserModelAssembler assembler, MovieModelAssembler movieAssembler,
                   ReviewModelAssembler reviewAssembler) {
        this.assembler = assembler;
        this.movieAssembler = movieAssembler;
        this.reviewAssembler = reviewAssembler;
    }

    // for testing only
    @GetMapping("/currentUser")
    public String currentUserName(Authentication authentication) {
        return authentication.getName();
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

// ------------------------------------ SEARCH ---------------------------------------------------------------------

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

// ---------------------------------------- WATCHLIST -----------------------------------------------------------------

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

    @GetMapping("/{user_id}/watchlist/{movie_id}")
    public ResponseEntity<EntityModel<Movie>> findMovieInWatchlistById(@PathVariable Long user_id, @PathVariable Long movie_id) {
        User user = userService.findById(user_id).orElseThrow(() -> new UserNotFoundException(user_id));

        Movie movie = user.getWatchlist().stream()
                .filter(m -> Objects.equals(m.getMovie_id(), movie_id)).findFirst().orElseThrow(() -> new MovieNotFoundException(movie_id));

        return ResponseEntity.ok(movieAssembler.toModel(movie));
    }

    @PostMapping("/{id}/watchlist")
    public ResponseEntity<EntityModel<User>> addMovieToWatchlist(@PathVariable Long id, @RequestBody Long movie_id) {
        User user = userService.findById(id).orElseThrow(() -> new UserNotFoundException(id));
        Movie movie = movieService.findById(movie_id).orElseThrow(() -> new MovieNotFoundException(movie_id));

        if (user.addMovieToWatchlist(movie)) {
            log.info(String.format("Movie with id %s added to watchlist", movie_id));
            userService.save(user);
        }

        return ResponseEntity.ok(assembler.toModel(user));
    }

    @DeleteMapping("/{user_id}/watchlist/{movie_id}")
    public ResponseEntity<EntityModel<User>> removeMovieFromWatchlist(@PathVariable Long user_id, @PathVariable Long movie_id) {
        User user = userService.findById(user_id).orElseThrow(() -> new UserNotFoundException(user_id));

        if (user.removeMovieFromWatchlist(movie_id)) {
            userService.save(user);
            log.info(String.format("Movie with id %s removed from watchlist", movie_id));
        }

        return ResponseEntity.noContent().build();
    }

// ------------------------------------------ REVIEWS -----------------------------------------------------------------

    @GetMapping("/{user_id}/reviews")
    public ResponseEntity<CollectionModel<EntityModel<Review>>> findReviewsByUser(@PathVariable Long user_id) {
       User user = userService.findById(user_id).orElseThrow(() -> new UserNotFoundException(user_id));

       List<Review> reviews = user.getReviews();

       if (reviews.isEmpty()) { return ResponseEntity.ok().build(); }

       List<EntityModel<Review>> reviewModels = reviews.stream()
               .map(reviewAssembler::toModel)
               .toList();

       CollectionModel<EntityModel<Review>> collectionModel = CollectionModel.of(reviewModels,
               linkTo(methodOn(UserController.class).findReviewsByUser(user_id)).withSelfRel());

       return ResponseEntity.ok(collectionModel);
    }

}
