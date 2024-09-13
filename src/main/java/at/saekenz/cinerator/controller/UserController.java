package at.saekenz.cinerator.controller;

import at.saekenz.cinerator.model.follow.*;
import at.saekenz.cinerator.model.movie.*;
import at.saekenz.cinerator.model.review.Review;
import at.saekenz.cinerator.model.review.ReviewModelAssembler;
import at.saekenz.cinerator.model.user.*;
import at.saekenz.cinerator.model.userlist.UserList;
import at.saekenz.cinerator.model.userlist.UserListDTO;
import at.saekenz.cinerator.model.userlist.UserListDTOModelAssembler;
import at.saekenz.cinerator.model.userlist.UserListMapper;
import at.saekenz.cinerator.service.IFollowService;
import at.saekenz.cinerator.service.IMovieService;
import at.saekenz.cinerator.service.IUserService;
import at.saekenz.cinerator.util.CollectionModelBuilderService;
import at.saekenz.cinerator.util.ResponseBuilderService;
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

    @Autowired
    IFollowService followService;

    @Autowired
    ResponseBuilderService responseBuilderService;

    @Autowired
    CollectionModelBuilderService collectionModelBuilderService;

    @Autowired
    UserListMapper userListMapper;

    @Autowired
    UserListDTOModelAssembler userListDTOAssembler;

    @Autowired
    MovieDTOModelAssembler movieDTOAssembler;

    @Autowired
    MovieMapper movieMapper;

    private final ReviewModelAssembler reviewAssembler;

    private final FollowMapper followMapper;
    private final FollowDTOModelAssembler followAssembler;

    private final UserMapper userMapper;
    private final UserDTOAssembler userDTOAssembler;

    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserListDTOModelAssembler userListDTOModelAssembler;

    UserController(UserDTOAssembler userDTOAssembler,
                   ReviewModelAssembler reviewAssembler, FollowDTOModelAssembler followAssembler) {
        this.reviewAssembler = reviewAssembler;
        this.followMapper = new FollowMapper();
        this.followAssembler = followAssembler;
        this.userDTOAssembler = userDTOAssembler;
        this.userMapper = new UserMapper();
    }

    // for testing only
    @GetMapping("/currentUser")
    public String currentUserName(Authentication authentication) {
        return authentication.getName();
    }

    /**
     *
     * @return List of {@link UserDTO} objects (or empty list if no users have been saved yet) and
     * HTTP code 200
     */
    @GetMapping
    public ResponseEntity<CollectionModel<EntityModel<UserDTO>>> findAll() {
        List<User> users = userService.findAll();

        if (users.isEmpty()) { return ResponseEntity.ok(CollectionModel.empty()); }

        List<EntityModel<UserDTO>> userModels = users.stream()
                .map(userMapper::toDTO)
                .map(userDTOAssembler::toModel)
                .toList();

        CollectionModel<EntityModel<UserDTO>> collectionModel = CollectionModel.of(userModels,
                linkTo(methodOn(UserController.class).findAll()).withSelfRel());

        return ResponseEntity.ok(collectionModel);
    }

    /**
     *
     * @param id number of the {@link User} that is to be retrieved
     * @return HTTP code 200 and the {@link User} object if it was found. HTTP code 404 otherwise
     */
    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<UserDTO>> findById(@PathVariable Long id) {
        UserDTO userDTO = userMapper.toDTO(userService.findById(id).orElseThrow(() -> new UserNotFoundException(id)));
        return ResponseEntity.ok(userDTOAssembler.toModel(userDTO));
    }

    /**
     *
     * @param userCreationDTO information about the {@link User} that is to be added to the database
     * @return HTTP code 201 and the {@link User} object that was created
     */
    @PostMapping
    public ResponseEntity<?> createUser(@RequestBody UserCreationDTO userCreationDTO) {
        User user = userMapper.toUser(userCreationDTO);
        EntityModel<UserDTO> entityModel = userDTOAssembler.toModel(userMapper.toDTO(userService.save(user)));

        return ResponseEntity.created(entityModel.getRequiredLink(IanaLinkRelations.SELF).toUri())
                .body(entityModel);
    }

    /**
     *
     * @param id number of the {@link User} that will be updated
     * @param updatedUser contains data that will be used for the update
     * @return HTTP code 404 if the {@link User} object is not found otherwise HTTP code 204
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @RequestBody UserDTO updatedUser) {
        User existingUser = userService.findById(id).orElseThrow(() -> new UserNotFoundException(id));

        existingUser.setUsername(updatedUser.getUsername());
        existingUser.setEmail(updatedUser.getEmail());
        existingUser.setName(updatedUser.getName());
        existingUser.setBio(updatedUser.getBio());

        EntityModel<UserDTO> entityModel = userDTOAssembler.toModel(userMapper.toDTO(userService.save(existingUser)));

        return responseBuilderService.buildNoContentResponseWithLocation(entityModel);
    }

    /**
     *
     * @param id number of {@link User} that is to be removed from the database
     * @return HTTP code 204 if {@link User} was deleted. HTTP code 404 if {@link User} was not found
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        userService.findById(id).orElseThrow(() -> new UserNotFoundException(id));
        userService.deleteById(id);

        return ResponseEntity.noContent().build();
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
        EntityModel<UserDTO> entityModel = userDTOAssembler.toModel(userMapper.toDTO(userService.save(user)));

        return responseBuilderService.buildNoContentResponseWithLocation(entityModel);
    }

// ------------------------------------ SEARCH ---------------------------------------------------------------------

    /**
     *
     * @param username username of searched for {@link User} objects
     * @return list of {@link User} objects and HTTP code 200 if any users were found. HTTP code 404 otherwise
     */
    @GetMapping("/username/{username}")
    public ResponseEntity<CollectionModel<EntityModel<UserDTO>>> findByUsername(@PathVariable String username) {
        List<User> usersByUsername = userService.findByUsername(username);

        if (usersByUsername.isEmpty()) {
            throw new UserNotFoundException(EUserSearchParam.USERNAME, username);
        }

        List<EntityModel<UserDTO>> userModels = usersByUsername.stream()
                .map(userMapper::toDTO)
                .map(userDTOAssembler::toModel)
                .toList();

        CollectionModel<EntityModel<UserDTO>> collectionModel = CollectionModel.of(userModels,
                linkTo(methodOn(UserController.class).findByUsername(username)).withSelfRel());

        return ResponseEntity.ok(collectionModel);
    }

    /**
     *
     * @param role level of permissions that the searched for {@link User} objects possess
     * @return list of {@link User} objects and HTTP code 200 if any users were found.
     * HTTP code 404 otherwise
     */
    @GetMapping("/role/{role}")
    public ResponseEntity<CollectionModel<EntityModel<UserDTO>>> findUsersByRole(@PathVariable String role) {
        List<User> usersByRole = userService.findUsersByRole(role);

        if (usersByRole.isEmpty()) {
            throw new UserNotFoundException(EUserSearchParam.ROLE, role);
        }

        List<EntityModel<UserDTO>> userModels = usersByRole.stream()
                .map(userMapper::toDTO)
                .map(userDTOAssembler::toModel)
                .toList();

        CollectionModel<EntityModel<UserDTO>> collectionModel = CollectionModel.of(userModels,
                linkTo(methodOn(UserController.class).findUsersByRole(role)).withSelfRel());

        return ResponseEntity.ok(collectionModel);
    }

    @GetMapping("/search")
    public ResponseEntity<?> searchUsers(@RequestParam(required = false) String name,
                                         @RequestParam(required = false) String username,
                                         @RequestParam(required = false) String email,
                                         @RequestParam(required = false) String role) {
        List<User> foundUsers = userService.searchUsers(name, username, email, role);

        if (foundUsers.isEmpty()) { return ResponseEntity.ok(CollectionModel.empty()); }

        List<EntityModel<UserDTO>> userModels = foundUsers.stream()
                .map(userMapper::toDTO)
                .map(userDTOAssembler::toModel)
                .toList();

        return ResponseEntity.ok(CollectionModel.of(userModels,
                linkTo(methodOn(UserController.class).searchUsers(name, username, email, role)).withSelfRel()));
    }

// ---------------------------------------- WATCHLIST -----------------------------------------------------------------

    /**
     *
     * @param id of the {@link User} for which the watchlist is to be fetched
     * @return HTTP code 200 and a list of {@link Movie} objects or HTTP code 404 if the {@link User}
     * does not exist
     */
    @GetMapping("/{id}/watchlist")
    public ResponseEntity<CollectionModel<EntityModel<MovieDTO>>> findWatchlistByUser(@PathVariable Long id) {
        User user = userService.findById(id).orElseThrow(() -> new UserNotFoundException(id));
        Set<Movie> movies = user.getWatchlist();

        if (movies.isEmpty()) { return ResponseEntity.ok(CollectionModel.empty()); }

        CollectionModel<EntityModel<MovieDTO>> collectionModel = collectionModelBuilderService
                .createCollectionModelFromList(movies,
                linkTo(methodOn(UserController.class).findWatchlistByUser(id)).withSelfRel());

        return ResponseEntity.ok(collectionModel);
    }

    /**
     *
     * @param userId number of the {@link User} for which the {@link Movie} is to be fetched
     * @param movieId number of the {@link Movie} that is to be fetched
     * @return HTTP code 200 and a {@link Movie} object or HTTP code 404 if
     * the {@link User} or {@link Movie} do not exist
     */
    @GetMapping("/{userId}/watchlist/{movieId}")
    public ResponseEntity<EntityModel<MovieDTO>> findMovieInWatchlistById(@PathVariable Long userId, @PathVariable Long movieId) {
        User user = userService.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));

        Movie movie = user.getWatchlist().stream()
                .filter(m -> Objects.equals(m.getId(), movieId)).findFirst().orElseThrow(() -> new MovieNotFoundException(movieId));

        return ResponseEntity.ok(movieDTOAssembler.toModel(movieMapper.toDTO(movie)));
    }

    /**
     *
     * @param id number of the {@link User} for which the {@link Movie} is to be added to the watchlist
     * @param movieId number of the {@link Movie} that is to be added to the watchlist
     * @return HTTP code 204 if the {@link Movie} was successfully added
     * or HTTP code 404 if the {@link User}/{@link Movie} was not found
     */
    @PutMapping("/{id}/watchlist")
    public ResponseEntity<?> addMovieToWatchlist(@PathVariable Long id, @RequestBody Long movieId) {
        User user = userService.findById(id).orElseThrow(() -> new UserNotFoundException(id));
        Movie movie = movieService.findById(movieId).orElseThrow(() -> new MovieNotFoundException(movieId));

        user.addMovieToWatchlist(movie);
        EntityModel<UserDTO> entityModel = userDTOAssembler.toModel(userMapper.toDTO(userService.save(user)));

        return responseBuilderService.buildNoContentResponseWithLocation(entityModel);
    }

    /**
     *
     * @param userId identifies the {@link User} that owns the watchlist
     * @param movieId identifies the {@link Movie} that is to be removed
     * @return HTTP code 204 (or HTTP code 404 if the {@link User} does not exist or
     * the watchlist does not contain the {@link Movie})
     */
    @DeleteMapping("/{userId}/watchlist/{movieId}")
    public ResponseEntity<?> removeMovieFromWatchlist(@PathVariable Long userId, @PathVariable Long movieId) {
        User user = userService.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));

        if (user.removeMovieFromWatchlist(movieId)) {
            userService.save(user);
            log.info(String.format("Movie with id %s removed from watchlist", movieId));
            return ResponseEntity.noContent().build();
        }
        else {
            return ResponseEntity.notFound().build();
        }
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
    public ResponseEntity<CollectionModel<EntityModel<MovieDTO>>> findMoviesLikedByUser(@PathVariable Long userId) {
        userService.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));
        List<Movie> likedMovies = userService.findMoviesLikedByUser(userId);

        if (likedMovies.isEmpty()) { return ResponseEntity.ok(CollectionModel.empty()); }

       CollectionModel<EntityModel<MovieDTO>> collectionModel = collectionModelBuilderService
               .createCollectionModelFromList(likedMovies,
                linkTo(methodOn(UserController.class).findMoviesLikedByUser(userId)).withSelfRel());

        return ResponseEntity.ok(collectionModel);
    }

    /**
     *
     * @param userId number of the {@link User} for which rated movies will be retrieved
     * @param rating number the {@link User} has rated movies with
     * @return List of {@link Movie} objects (empty list if the {@link User} has not rated
     * any movies with this specific rating) and HTTP code 200
     * or HTTP code 404 if the {@link User} does not exist
     */
    @GetMapping("/{userId}/movies/rated/{rating}")
    public ResponseEntity<CollectionModel<EntityModel<MovieDTO>>> findMoviesRatedByUser(@PathVariable Long userId,
                                                                                     @PathVariable Integer rating) {
        userService.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));
        List<Movie> ratedMovies = userService.findMoviesRatedByUser(userId, rating);

        if (ratedMovies.isEmpty()) { return ResponseEntity.ok(CollectionModel.empty()); }

                CollectionModel<EntityModel<MovieDTO>> collectionModel = collectionModelBuilderService
                        .createCollectionModelFromList(ratedMovies,
                linkTo(methodOn(UserController.class).findMoviesRatedByUser(userId, rating)).withSelfRel());

        return ResponseEntity.ok(collectionModel);
    }

// ----------------------------------------- FOLLOWERS ----------------------------------------------------------------

    /**
     *
     * @param id number of the {@link User} for which followers are to be retrieved
     * @return List of {@link User} objects (or empty list) and HTTP code 200 or HTTP code
     * 404 if the {@link User} does not exist
     */
    @GetMapping("/{id}/followers")
    public ResponseEntity<CollectionModel<EntityModel<UserDTO>>> findFollowersByUser(@PathVariable Long id) {
        User user = userService.findById(id).orElseThrow(() -> new UserNotFoundException(id));
        Set<Follow> followers = user.getFollowers();

        if (followers.isEmpty()) { return ResponseEntity.ok(CollectionModel.empty()); }

        List<EntityModel<UserDTO>> userModels = followers.stream()
                .map(follow -> userMapper.toDTO(follow.getFollower()))
                .map(userDTOAssembler::toModel)
                .toList();

        CollectionModel<EntityModel<UserDTO>> collectionModel = CollectionModel.of(userModels,
                linkTo(methodOn(UserController.class).findFollowersByUser(user.getId())).withSelfRel());

        return ResponseEntity.ok(collectionModel);
    }

    /**
     *
     * @param id number of the {@link User} of which users they follow are to be retrieved
     * @return List of {@link User} objects (or empty list) and HTTP code 200 or HTTP code
     * 404 if the {@link User} does not exist
     */
    @GetMapping("/{id}/following")
    public ResponseEntity<CollectionModel<EntityModel<UserDTO>>> findFollowingByUser(@PathVariable Long id) {
        User user = userService.findById(id).orElseThrow(() -> new UserNotFoundException(id));
        Set<Follow> following = user.getFollows();

        if (following.isEmpty()) { return ResponseEntity.ok(CollectionModel.empty()); }

        List<EntityModel<UserDTO>> userModels = following.stream()
                .map(follow -> userMapper.toDTO(follow.getUser()))
                .map(userDTOAssembler::toModel)
                .toList();

        CollectionModel<EntityModel<UserDTO>> collectionModel = CollectionModel.of(userModels,
                linkTo(methodOn(UserController.class).findFollowingByUser(user.getId())).withSelfRel());

        return ResponseEntity.ok(collectionModel);
    }

    /**
     *
     * @param id number of the {@link User} that will be followed
     * @param followActionDTO contains the id of {@link User} that wants to follow
     * @return HTTP code 201 if the request is successful, HTTP code 404 if one of the users is not found in the database
     * and HTTP code 400 if the {@link User} is already following
     */
    @PostMapping("/{id}/follow")
    public ResponseEntity<?> followAnotherUser(@PathVariable Long id, @RequestBody FollowActionDTO followActionDTO) {
        Long followerId = followActionDTO.followerId();

        User user = userService.findById(id).orElseThrow(() -> new UserNotFoundException(id));
        User follower = userService.findById(followerId).orElseThrow(() -> new UserNotFoundException(followerId));
        FollowKey key = new FollowKey(id, followerId);

        if (followService.findByKey(key).isEmpty()) {
            Follow follow = followService.save(new Follow(new FollowKey(id, followerId), user, follower));

            EntityModel<FollowDTO> entityModel = followAssembler
                    .toModel(followMapper.toDTO(follow));

            return ResponseEntity
                    .created(entityModel.getRequiredLink(IanaLinkRelations.SELF).toUri())
                    .body(entityModel);
        }
        else {
            return ResponseEntity
                    .badRequest()
                    .body(String.format("User %s is already following user %s!", followerId, id));
        }
    }

    /**
     *
     * @param id number of the {@link User} that will be unfollowed
     * @param followActionDTO contains the id of {@link User} that wants to unfollow
     * @return HTTP code 204 or HTTP code 404 if one of the users is not found in the database
     */
    @DeleteMapping("/{id}/unfollow")
    public ResponseEntity<?> unfollowAnotherUser(@PathVariable Long id, @RequestBody FollowActionDTO followActionDTO) {
        Long followerId = followActionDTO.followerId();
        List<User> involvedUsers = userService.findAllById(List.of(id, followerId));

        if (involvedUsers.size() != 2) {
            if (involvedUsers.stream().noneMatch(u -> u.getId().equals(id))) {
                throw new UserNotFoundException(id);
            }
            if (involvedUsers.stream().noneMatch(u -> u.getId().equals(followerId))) {
                throw new UserNotFoundException(followerId);
            }
        }

        followService.deleteByKey(new FollowKey(id, followerId));

        return ResponseEntity.noContent().build();
    }

// ---------------------------------------- LISTS ---------------------------------------------------------------------

    /**
     * Fetches every {@link UserList} associated with the {@link User} with a
     * specific {@code id}.
     *
     * @param id the ID of the {@link User} for which the lists are to be fetched
     * @return ResponseEntity containing a 200 Ok status and every {@link UserList}
     * associated with this {@link User}. (Returns a 404 Not Found status if
     * no {@link User} exists with this {@code id}.
     */
    @GetMapping("/{id}/lists")
    public ResponseEntity<?> findListsByUser(@PathVariable Long id) {
        User user = userService.findById(id).orElseThrow(() -> new UserNotFoundException(id));
        List<UserList> userLists = user.getUserlists();

        if (userLists.isEmpty()) { return ResponseEntity.ok(CollectionModel.empty()); }

        List<EntityModel<UserListDTO>> userListModels = userLists.stream()
                .map(userListMapper::toDTO)
                .map(userListDTOModelAssembler::toModel)
                .toList();

        CollectionModel<EntityModel<UserListDTO>> collectionModel = CollectionModel.of(userListModels,
                linkTo(methodOn(UserController.class).findListsByUser(id)).withSelfRel());

        return ResponseEntity.ok(collectionModel);
    }
}
