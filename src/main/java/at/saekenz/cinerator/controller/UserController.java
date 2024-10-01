package at.saekenz.cinerator.controller;

import at.saekenz.cinerator.model.follow.*;
import at.saekenz.cinerator.model.movie.*;
import at.saekenz.cinerator.model.review.*;
import at.saekenz.cinerator.model.user.*;
import at.saekenz.cinerator.model.userlist.UserList;
import at.saekenz.cinerator.model.userlist.UserListDTO;
import at.saekenz.cinerator.model.userlist.UserListDTOModelAssembler;
import at.saekenz.cinerator.model.userlist.UserListMapper;
import at.saekenz.cinerator.service.IUserService;
import at.saekenz.cinerator.util.CollectionModelBuilderService;
import at.saekenz.cinerator.util.ResponseBuilderService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Range;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.web.HateoasPageableHandlerMethodArgumentResolver;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/users")
public class UserController {
    private final ReviewMapper reviewMapper;
    private final ReviewDTOModelAssembler reviewAssembler;

    private final FollowMapper followMapper;
    private final FollowDTOModelAssembler followAssembler;

    private final UserMapper userMapper;
    private final UserDTOAssembler userDTOAssembler;

    @Autowired
    IUserService userService;

    @Autowired
    ResponseBuilderService responseBuilderService;

    @Autowired
    CollectionModelBuilderService collectionModelBuilderService;

    @Autowired
    UserListMapper userListMapper;

    @Autowired
    MovieDTOModelAssembler movieDTOAssembler;

    @Autowired
    MovieMapper movieMapper;

    @Autowired
    private UserListDTOModelAssembler userListDTOModelAssembler;

    private final PagedResourcesAssembler<UserDTO> pagedResourcesAssembler = new PagedResourcesAssembler<>(
            new HateoasPageableHandlerMethodArgumentResolver(), null);

    UserController(ReviewMapper reviewMapper, UserDTOAssembler userDTOAssembler,
                   ReviewDTOModelAssembler reviewAssembler, FollowMapper followMapper,
                   FollowDTOModelAssembler followAssembler, UserMapper userMapper) {
        this.reviewMapper = reviewMapper;
        this.reviewAssembler = reviewAssembler;
        this.followMapper = followMapper;
        this.followAssembler = followAssembler;
        this.userDTOAssembler = userDTOAssembler;
        this.userMapper = userMapper;
    }

    // for testing only
    @GetMapping("/currentUser")
    public String currentUserName(Authentication authentication) {
        return authentication.getName();
    }

    /**
     * Fetch every {@link User} resource from the database (in a paged format).
     *
     * @param page number of the page returned
     * @param size number of {@link User} resources returned for each page
     * @param sortField attribute that determines how returned resources will be sorted
     * @param sortDirection order of sorting (can be ASC or DESC)
     * @return {@link PagedModel} object with sorted/filtered {@link User} resources wrapped
     * in {@link ResponseEntity<>}
     */
    @GetMapping
    public ResponseEntity<PagedModel<EntityModel<UserDTO>>> findAll(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "5") int size,
            @RequestParam(name = "sortField", defaultValue = "id") String sortField,
            @RequestParam(name = "sortDirection", defaultValue = "ASC") String sortDirection) {
        Page<UserDTO> users = userService.findAllPaged(page, size, sortField, sortDirection)
                .map(userMapper::toDTO);

        return ResponseEntity.ok(pagedResourcesAssembler.toModel(users, userDTOAssembler));
    }

    /**
     * Fetch a specific {@link User} by their {@code id}.
     *
     * @param id number of the {@link User} that is to be retrieved
     * @return {@link ResponseEntity<>} containing 200 Ok status and the {@link User} resource (or a
     * 404 Not Found status if no {@link User} exists with the specified {@code id}.)
     */
    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<UserDTO>> findById(@NotNull @Range(min = 1) @PathVariable Long id) {
        User foundUser = userService.findUserById(id);

        return ResponseEntity.ok(userDTOAssembler.toModel(userMapper.toDTO(foundUser)));
    }

// ------------------------------ CREATE/UPDATE/DELETE --------------------------------------------------------------

    /**
     * Creates a new {@link User}.
     *
     * @param userCreationDTO a DTO containing data of the new {@link User}
     * @return {@link ResponseEntity<>} containing a 201 Created status and the created {@link User}.
     */
    @PostMapping
    public ResponseEntity<EntityModel<UserDTO>> createUser(@Valid @RequestBody UserCreationDTO userCreationDTO) {
        User createdUser = userService.createUser(userCreationDTO);
        EntityModel<UserDTO> entityModel = userDTOAssembler.toModel(userMapper.toDTO(createdUser));

        return responseBuilderService.buildCreatedResponseWithBody(entityModel);
    }

    /**
     * Updates a {@link User} based on their {@code id}.
     *
     * @param id the ID of the {@link User} to be updated
     * @param userDTO a DTO containing the needed data
     * @return {@link ResponseEntity<>} containing a 204 No Content status (or a
     * 404 Not Found status if no {@link User} exists with the specified {@code id}.)
     */
    @PutMapping("/{id}")
    public ResponseEntity<Object> updateUser(@NotNull @Range(min = 1) @PathVariable Long id,
                                        @Valid @RequestBody UserDTO userDTO) {
        User updatedUser = userService.updateUser(id, userDTO);
        EntityModel<UserDTO> entityModel = userDTOAssembler.toModel(userMapper.toDTO(updatedUser));

        return responseBuilderService.buildNoContentResponseWithLocation(entityModel);
    }

    /**
     * Deletes a {@link User} by their {@code id}.
     *
     * @param id the ID of the {@link User} to be deleted
     * @return {@link ResponseEntity<>} containing a 204 No Content status (or a
     * 404 Not Found status if no {@link User} exists with the specified {@code id}.)
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@NotNull @Range(min = 1) @PathVariable Long id) {
        userService.deleteById(id);

        return ResponseEntity.noContent().build();
    }

// ------------------------------------ SEARCH ---------------------------------------------------------------------

    /**
     * Fetches {@link User} resources based on search parameters.
     *
     * @param name name of the searched for user(s)
     * @param username username of the searched for user(s)
     * @param email email address of the searched for user(s)
     * @param role role of the searched for user(s)
     * @return {@link ResponseEntity} containing a 200 Ok status and a collection of the found
     * {@link User} resources.
     */
    @GetMapping("/search")
    public ResponseEntity<CollectionModel<EntityModel<UserDTO>>> searchUsers(
            @RequestParam(required = false) String name, @RequestParam(required = false) String username,
            @RequestParam(required = false) String email, @RequestParam(required = false) String role) {
        List<User> foundUsers = userService.searchUsers(name, username, email, role);

        if (foundUsers.isEmpty()) { return ResponseEntity.ok(CollectionModel.empty()); }

        CollectionModel<EntityModel<UserDTO>> collectionModel = collectionModelBuilderService
                .createCollectionModelFromList(foundUsers, userMapper, userDTOAssembler,
                        linkTo(methodOn(UserController.class).searchUsers(name, username, email, role)).withSelfRel());

        return ResponseEntity.ok(collectionModel);
    }

// ---------------------------------------- WATCHLIST -----------------------------------------------------------------

    /**
     * Fetches every {@link Movie} resource that was put in the watchlist of the {@link User} identified
     * by {@code id}.
     *
     * @param id the ID of the {@link User} for which the watchlist is to be fetched
     * @return {@link ResponseEntity<>} containing a 200 Ok status and a collection of {@link Movie} resources (or a
     *  404 Not Found status if no {@link User} exists with the specified {@code id}.)
     */
    @GetMapping("/{id}/watchlist")
    public ResponseEntity<CollectionModel<EntityModel<MovieDTO>>> findWatchlistByUser(
            @NotNull @Range(min = 1) @PathVariable Long id) {
        User user = userService.findUserById(id);
        Set<Movie> movies = user.getWatchlist();

        if (movies.isEmpty()) { return ResponseEntity.ok(CollectionModel.empty()); }

        CollectionModel<EntityModel<MovieDTO>> collectionModel = collectionModelBuilderService
                .createCollectionModelFromList(movies,
                linkTo(methodOn(UserController.class).findWatchlistByUser(id)).withSelfRel());

        return ResponseEntity.ok(collectionModel);
    }

    /**
     * Fetches a specific {@link Movie} resource by its {@code id} if it exists in the watchlist of {@link User}
     * identified by {@code userId}.
     *
     * @param userId the ID of the {@link User} for which the {@link Movie} is to be fetched
     * @param movieId the ID of the {@link Movie} that is to be fetched
     * @return {@link ResponseEntity<>} containing a 200 Ok status and the {@link Movie} resource (or a
     * 404 Not Found status if no {@link User}/{@link Movie} exists with the specified {@code id}.)
     */
    @GetMapping("/{userId}/watchlist/{movieId}")
    public ResponseEntity<EntityModel<MovieDTO>> findMovieInWatchlistById(
            @NotNull @Range(min = 1) @PathVariable Long userId, @NotNull @Range(min = 1) @PathVariable Long movieId) {
        Movie foundMovie = userService.findMovieInUsersWatchlist(userId, movieId);

        return ResponseEntity.ok(movieDTOAssembler.toModel(movieMapper.toDTO(foundMovie)));
    }

    /**
     * Adds a {@link Movie} resource specified by {@code movieId} to the watchlist belonging to {@link User}
     * identified by {@code userId}
     *
     * @param userId the ID of the {@link User} for which the {@link Movie} is to be added
     * @param movieId the ID of the {@link Movie} that is to be added
     * @return {@link ResponseEntity<>} containing a 204 No Content status and a link to the updated {@link User}
     * resource. It returns a 404 Not Found status if the {@link User} does not exist or a 400 Bad Request status
     * if the {@link Movie} is already present in this watchlist.
     */
    @PutMapping("/{userId}/watchlist")
    public ResponseEntity<Object> addMovieToWatchlist(@NotNull @Range(min = 1) @PathVariable Long userId,
                                                 @NotNull @RequestBody Long movieId) {
        User updatedUser = userService.addMovieToWatchlistById(userId, movieId);
        EntityModel<UserDTO> entityModel = userDTOAssembler.toModel(userMapper.toDTO(updatedUser));

        return responseBuilderService.buildNoContentResponseWithLocation(entityModel);
    }

    /**
     * Removes a {@link Movie} resource specified by {@code movieId} from the watchlist belonging to {@link User}
     * identified by {@code userId}
     *
     * @param userId the ID of the {@link User} for which the {@link Movie} is to be removed
     * @param movieId the ID of the {@link Movie} that is to be removed
     * @return {@link ResponseEntity<>} containing a 204 No Content status (or a 404 Not Found status if the
     * {@link User} does not exist or the {@link Movie} is not found in the watchlist).
     */
    @DeleteMapping("/{userId}/watchlist/{movieId}")
    public ResponseEntity<?> removeMovieFromWatchlist(@NotNull @Range(min = 1) @PathVariable Long userId,
                                                      @NotNull @Range(min = 1) @PathVariable Long movieId) {
        userService.removeMovieFromWatchlistById(userId, movieId);

        return ResponseEntity.noContent().build();
    }

// ------------------------------------------ REVIEWS -----------------------------------------------------------------

    /**
     * Fetches every {@link Review} that was created by {@link User} with {@code userId}.
     *
     * @param userId the ID of the {@link User} for which the {@link Review} resources are to be retrieved
     * @return {@link ResponseEntity<>} containing a 200 Ok status and a collection of {@link Review} resources
     * (or a 404 Not Found status if the {@link User} does not exist).
     */
    @GetMapping("/{userId}/reviews")
    public ResponseEntity<CollectionModel<EntityModel<ReviewDTO>>> findReviewsByUser(
            @NotNull @Range(min = 1) @PathVariable Long userId) {
       List<Review> reviews = userService.findReviewsByUserId(userId);

       if (reviews.isEmpty()) { return ResponseEntity.ok(CollectionModel.empty()); }

       CollectionModel<EntityModel<ReviewDTO>> collectionModel = collectionModelBuilderService
               .createCollectionModelFromList(reviews, reviewMapper, reviewAssembler,
                       linkTo(methodOn(UserController.class).findReviewsByUser(userId)).withSelfRel());

       return ResponseEntity.ok(collectionModel);
    }

    /**
     * Fetches every {@link Movie} that was liked by {@link User} with {@code userId}.
     *
     * @param userId the ID of the {@link User} for which the liked {@link Movie} resources are to be retrieved
     * @return {@link ResponseEntity<>} containing a 200 Ok status and a collection of {@link Movie} resources
     * (or a 404 Not Found status if the {@link User} does not exist).
     */
    @GetMapping("/{userId}/movies/liked")
    public ResponseEntity<CollectionModel<EntityModel<MovieDTO>>> findMoviesLikedByUser(
            @NotNull @Range(min = 1) @PathVariable Long userId) {
        List<Movie> likedMovies = userService.findMoviesLikedByUser(userId);

        if (likedMovies.isEmpty()) { return ResponseEntity.ok(CollectionModel.empty()); }

       CollectionModel<EntityModel<MovieDTO>> collectionModel = collectionModelBuilderService
               .createCollectionModelFromList(likedMovies,
                linkTo(methodOn(UserController.class).findMoviesLikedByUser(userId)).withSelfRel());

        return ResponseEntity.ok(collectionModel);
    }

    /**
     * Fetches every {@link Movie} that was rated with {@code rating} by {@link User} with {@code userId}.
     *
     * @param userId the ID of the {@link User} for which rated movies will be retrieved
     * @param rating number the {@link User} has rated movies with
     * @return {@link ResponseEntity<>} containing a 200 Ok status and a collection of {@link Movie} resources
     * (or a 404 Not Found status if the {@link User} does not exist).
     */
    @GetMapping("/{userId}/movies/rated/{rating}")
    public ResponseEntity<CollectionModel<EntityModel<MovieDTO>>> findMoviesRatedByUser(
            @NotNull @Range(min = 1) @PathVariable Long userId,
            @NotNull @Range(min = 1) @PathVariable Integer rating) {
        List<Movie> ratedMovies = userService.findMoviesRatedByUser(userId, rating);

        if (ratedMovies.isEmpty()) { return ResponseEntity.ok(CollectionModel.empty()); }

        CollectionModel<EntityModel<MovieDTO>> collectionModel = collectionModelBuilderService
                .createCollectionModelFromList(ratedMovies,
                        linkTo(methodOn(UserController.class).findMoviesRatedByUser(userId, rating)).withSelfRel());

        return ResponseEntity.ok(collectionModel);
    }

// ----------------------------------------- FOLLOWERS ----------------------------------------------------------------

    /**
     * Fetches every {@link User} that follows {@link User} with {@code id}.
     *
     * @param id the ID of the {@link User} for which followers are to be retrieved
     * @return {@link ResponseEntity<>} containing a 200 Ok status and a collection of {@link User} resources
     * (or a 404 Not Found status if the {@link User} does not exist).
     */
    @GetMapping("/{id}/followers")
    public ResponseEntity<CollectionModel<EntityModel<UserDTO>>> findFollowersByUser(
            @NotNull @Range(min = 1) @PathVariable Long id) {
        List<User> followers = userService.findFollowersByUserId(id);

        if (followers.isEmpty()) { return ResponseEntity.ok(CollectionModel.empty()); }

        CollectionModel<EntityModel<UserDTO>> collectionModel = collectionModelBuilderService
                .createCollectionModelFromList(followers, userMapper, userDTOAssembler,
                        linkTo(methodOn(UserController.class).findFollowersByUser(id)).withSelfRel());

        return ResponseEntity.ok(collectionModel);
    }

    /**
     * Fetches every {@link User} that {@link User} with {@code id} is following.
     *
     * @param id the ID of the {@link User} for which followed {@link User} resources are to be retrieved
     * @return {@link ResponseEntity<>} containing a 200 Ok status and a collection of {@link User} resources
     * (or a 404 Not Found status if the {@link User} does not exist).
     */
    @GetMapping("/{id}/following")
    public ResponseEntity<CollectionModel<EntityModel<UserDTO>>> findFollowingByUser(
            @NotNull @Range(min = 1) @PathVariable Long id) {
        List<User> following = userService.findFollowingByUserId(id);

        if (following.isEmpty()) { return ResponseEntity.ok(CollectionModel.empty()); }

        CollectionModel<EntityModel<UserDTO>> collectionModel = collectionModelBuilderService
               .createCollectionModelFromList(following, userMapper, userDTOAssembler,
                       linkTo(methodOn(UserController.class).findFollowingByUser(id)).withSelfRel());

        return ResponseEntity.ok(collectionModel);
    }

    /**
     * Adds {@link User} contained in {@code followActionDTO} to follower list of {@link User} with {@code id}.
     *
     * @param id the ID of the {@link User} that will be followed
     * @param followActionDTO contains the ID of the {@link User} that wants to follow
     * @return {@link ResponseEntity<>} containing a 201 Created status (or a 404 Not Found status if
     * any of the {@link User} resources does not exist or a 400 Bad Request status if the following
     * relationship already exists).
     */
    @PostMapping("/{id}/follow")
    public ResponseEntity<?> followAnotherUser(@NotNull @Range(min = 1) @PathVariable Long id,
                                               @Valid @RequestBody FollowActionDTO followActionDTO) {
        Follow newFollow = userService.followAnotherUser(id, followActionDTO.followerId());
        EntityModel<FollowDTO> entityModel = followAssembler.toModel(followMapper.toDTO(newFollow));

        return responseBuilderService.buildCreatedResponseWithBody(entityModel);
    }

    /**
     * Removes {@link User} contained in {@code followActionDTO} from follower list of {@link User} with {@code id}.
     *
     * @param id the ID of the {@link User} that will be unfollowed
     * @param followActionDTO contains the ID of {@link User} that wants to unfollow
     * @return {@link ResponseEntity<>} containing a 204 No Content status (or a 404 Not Found status if the
     * following relationship does not exist).
     */
    @DeleteMapping("/{id}/unfollow")
    public ResponseEntity<?> unfollowAnotherUser(@NotNull @Range(min = 1) @PathVariable Long id,
                                                 @Valid @RequestBody FollowActionDTO followActionDTO) {
        userService.unfollowAnotherUser(id, followActionDTO.followerId());

        return ResponseEntity.noContent().build();
    }

// ---------------------------------------- LISTS ---------------------------------------------------------------------

    /**
     * Fetches every {@link UserList} associated with the {@link User} with a specific {@code id}.
     *
     * @param id the ID of the {@link User} for which the lists are to be fetched
     * @return {@link ResponseEntity<>} containing a 200 Ok status and every {@link UserList}
     * associated with this {@link User} (or a 404 Not Found status if no {@link User} exists with this {@code id}).
     */
    @GetMapping("/{id}/lists")
    public ResponseEntity<?> findListsByUser(@NotNull @Range(min = 1) @PathVariable Long id) {
        List<UserList> userLists = userService.findUserListsByUserId(id);

        if (userLists.isEmpty()) { return ResponseEntity.ok(CollectionModel.empty()); }

        CollectionModel<EntityModel<UserListDTO>> collectionModel = collectionModelBuilderService
                .createCollectionModelFromList(userLists, userListMapper, userListDTOModelAssembler,
                        linkTo(methodOn(UserController.class).findListsByUser(id)).withSelfRel());

        return ResponseEntity.ok(collectionModel);
    }

// -------------------------------------- OTHER -------------------------------------------------------------------

    /**
     * Enable the account of {@link User} with {@code id}.
     *
     * @param id the ID of the {@link User} which will have their account enabled
     * @return {@link ResponseEntity<>} containing a 204 No Content status
     * (or a 404 Not Found status if no {@link User} exists with this {@code id}).
     */
    @PutMapping("/{id}/enable")
    public ResponseEntity<Object> enableUser(@NotNull @Range(min = 1) @PathVariable Long id) {
       userService.enableUser(id);

       UserDTO updatedUserDTO = new UserDTO();
       updatedUserDTO.setId(id); // only valid id needed to build links

       EntityModel<UserDTO> entityModel = userDTOAssembler.toModel(updatedUserDTO);

       return responseBuilderService.buildNoContentResponseWithLocation(entityModel);
    }
}
