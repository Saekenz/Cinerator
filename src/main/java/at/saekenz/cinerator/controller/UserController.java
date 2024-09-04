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
import java.util.Optional;
import java.util.Set;

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

        if (users.isEmpty()) { return ResponseEntity.ok(CollectionModel.empty()); }

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
        Optional<User> existingUser = userService.findById(id);
        User updatedUser = existingUser.map(
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

        if (existingUser.isPresent()) {
            return ResponseEntity.noContent().build();
        }
        else {
            EntityModel<User> entityModel = assembler.toModel(updatedUser);
            return ResponseEntity
                    .created(entityModel.getRequiredLink(IanaLinkRelations.SELF).toUri())
                    .body(entityModel);
        }
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

    /**
     *
     * @param id number of the user which should have their account enabled
     * @return HTTP code 204 or HTTP code 404 if the user is not found in the database
     */
    @PutMapping("/{id}/enable")
    public ResponseEntity<?> enableUser(@PathVariable Long id) {
        User user = userService.findById(id).orElseThrow(() -> new UserNotFoundException(id));

        user.setEnabled(true);
        userService.save(user);

        return ResponseEntity.noContent().build();
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
        Set<Movie> movies = user.getWatchlist();

        if (movies.isEmpty()) { return ResponseEntity.ok(CollectionModel.empty()); }

        List<EntityModel<Movie>> movieModels = movies
                .stream()
                .map(movieAssembler::toModel)
                .toList();

        CollectionModel<EntityModel<Movie>> collectionModel = CollectionModel.of(movieModels,
                linkTo(methodOn(UserController.class).findWatchlistByUser(id)).withSelfRel());

        return ResponseEntity.ok(collectionModel);
    }

    @GetMapping("/{userId}/watchlist/{movieId}")
    public ResponseEntity<EntityModel<Movie>> findMovieInWatchlistById(@PathVariable Long userId, @PathVariable Long movieId) {
        User user = userService.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));

        Movie movie = user.getWatchlist().stream()
                .filter(m -> Objects.equals(m.getId(), movieId)).findFirst().orElseThrow(() -> new MovieNotFoundException(movieId));

        return ResponseEntity.ok(movieAssembler.toModel(movie));
    }

    @PostMapping("/{id}/watchlist")
    public ResponseEntity<EntityModel<User>> addMovieToWatchlist(@PathVariable Long id, @RequestBody Long movieId) {
        User user = userService.findById(id).orElseThrow(() -> new UserNotFoundException(id));
        Movie movie = movieService.findById(movieId).orElseThrow(() -> new MovieNotFoundException(movieId));

        if (user.addMovieToWatchlist(movie)) {
            log.info(String.format("Movie with id %s added to watchlist", movieId));
            userService.save(user);
        }

        return ResponseEntity.ok(assembler.toModel(user));
    }

    @DeleteMapping("/{userId}/watchlist/{movieId}")
    public ResponseEntity<EntityModel<User>> removeMovieFromWatchlist(@PathVariable Long userId, @PathVariable Long movieId) {
        User user = userService.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));

        if (user.removeMovieFromWatchlist(movieId)) {
            userService.save(user);
            log.info(String.format("Movie with id %s removed from watchlist", movieId));
        }

        return ResponseEntity.noContent().build();
    }

// ------------------------------------------ REVIEWS -----------------------------------------------------------------

    /**
     *
     * @param userId number of the user for which {@link Review} objects are to be retrieved
     * @return List of {@link Review} objects (empty list if the {@link User} did not create any reviews yet) and HTTP code 200
     * or HTTP code 404 if the {@link User} does not exist
     */
    @GetMapping("/{userId}/reviews")
    public ResponseEntity<CollectionModel<EntityModel<Review>>> findReviewsByUser(@PathVariable Long userId) {
       User user = userService.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));
       List<Review> reviews = user.getReviews();

       if (reviews.isEmpty()) { return ResponseEntity.ok(CollectionModel.empty()); }

       List<EntityModel<Review>> reviewModels = reviews.stream()
               .map(reviewAssembler::toModel)
               .toList();

       CollectionModel<EntityModel<Review>> collectionModel = CollectionModel.of(reviewModels,
               linkTo(methodOn(UserController.class).findReviewsByUser(userId)).withSelfRel());

       return ResponseEntity.ok(collectionModel);
    }

    /**
     *
     * @param userId number of the user for which liked {@link Movie} objects are to be retrieved
     * @return List of {@link Movie} objects (empty list if the {@link User} did not like any movies yet) and HTTP code 200
     * or HTTP code 404 if the {@link User} does not exist
     */
    @GetMapping("/{userId}/movies/liked")
    public ResponseEntity<CollectionModel<EntityModel<Movie>>> findMoviesLikedByUser(@PathVariable Long userId) {
        userService.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));
        List<Movie> likedMovies = userService.findMoviesLikedByUser(userId);

        if (likedMovies.isEmpty()) { return ResponseEntity.ok(CollectionModel.empty()); }

        List<EntityModel<Movie>> likedMovieModels = likedMovies.stream()
                .map(movieAssembler::toModel)
                .toList();

        CollectionModel<EntityModel<Movie>> collectionModel = CollectionModel.of(likedMovieModels,
                linkTo(methodOn(UserController.class).findMoviesLikedByUser(userId)).withSelfRel());

        return ResponseEntity.ok(collectionModel);
    }

}
