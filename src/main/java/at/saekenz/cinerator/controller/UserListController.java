package at.saekenz.cinerator.controller;

import at.saekenz.cinerator.model.movie.Movie;
import at.saekenz.cinerator.model.movie.MovieModelAssembler;
import at.saekenz.cinerator.model.movie.MovieNotFoundException;
import at.saekenz.cinerator.model.user.*;
import at.saekenz.cinerator.model.userlist.*;
import at.saekenz.cinerator.service.IMovieService;
import at.saekenz.cinerator.service.IUserListService;
import at.saekenz.cinerator.service.IUserService;
import at.saekenz.cinerator.util.ResponseBuilderService;
import org.hibernate.ObjectNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/lists")
public class UserListController {

    @Autowired
    IUserListService userListService;

    @Autowired
    IUserService userService;

    @Autowired
    IMovieService movieService;

    @Autowired
    UserListMapper userListMapper;

    @Autowired
    UserMapper userMapper;

    @Autowired
    ResponseBuilderService responseBuilderService;

    private final UserListDTOModelAssembler userListDTOAssembler;
    private final UserDTOAssembler userDTOAssembler;
    private final MovieModelAssembler movieAssembler;


    public UserListController(UserListDTOModelAssembler userListDTOAssembler,
                              UserDTOAssembler userDTOAssembler,
                              MovieModelAssembler movieAssembler) {
        this.userListDTOAssembler = userListDTOAssembler;
        this.userDTOAssembler = userDTOAssembler;
        this.movieAssembler = movieAssembler;
    }

    /**
     * Fetches every {@link UserList} stored in the database.
     *
     * @return ResponseEntity containing 200 Ok status and a collection of every
     * {@link UserList} stored in the database.
     */
    @GetMapping
    public ResponseEntity<CollectionModel<EntityModel<UserListDTO>>> findAll() {
        List<UserList> userLists = userListService.findAll();

        if (userLists.isEmpty()) { return ResponseEntity.ok(CollectionModel.empty()); }

        CollectionModel<EntityModel<UserListDTO>> collectionModel = CollectionModel.of(
                userLists.stream()
                        .map(userListMapper::toDTO)
                        .map(userListDTOAssembler::toModel)
                        .toList(),
                linkTo(methodOn(UserListController.class).findAll()).withSelfRel());

        return ResponseEntity.ok(collectionModel);
    }

    /**
     * Fetches a {@link UserList} by its {@code id}.
     *
     * @param id the ID of the {@link UserList} to be retrieved
     * @return ResponseEntity containing a 200 Ok status and the {@link UserList}.
     * (Returns 404 Not Found if the {@link UserList} does not exist for this {@code id}.)
     */
    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<UserListDTO>> findById(@PathVariable Long id) {
        UserList userList = userListService.findById(id).orElseThrow(() -> new ObjectNotFoundException(id, "UserList"));

        return ResponseEntity.ok(userListDTOAssembler.toModel(userListMapper.toDTO(userList)));
    }

    /**
     * Creates a new {@link UserList}.
     *
     * @param userListCreationDTO a DTO containing data of the new {@link UserList}
     * @return ResponseEntity containing a 201 Created status and the created {@link UserList}.
     * (Returns 404 Not Found if the {@link User} owning the new {@link UserList} does not exist in the database.)
     */
    @PostMapping
    public ResponseEntity<?> createUserList(@RequestBody UserListCreationDTO userListCreationDTO) {
        User user = userService.findById(userListCreationDTO.userId()).orElseThrow(
                () -> new UserNotFoundException(userListCreationDTO.userId()));
        UserList userList = userListService.save(userListMapper.toUserList(userListCreationDTO, user));

        EntityModel<UserListDTO> entityModel = userListDTOAssembler.toModel(userListMapper.toDTO(userList));

        return ResponseEntity.created(entityModel.getRequiredLink(IanaLinkRelations.SELF).toUri())
                .body(entityModel);
    }

    /**
     * Creates or updates a {@link UserList} based on its id.
     *
     * @param id the ID of the {@link UserList} to be created/updated
     * @param newUserList a DTO containing the needed data
     * @return ResponseEntity containing a 204 No Content status if a {@link UserList} was updated or
     * 201 Created status if a new {@link UserList} was created. (Returns 404 Not Found if
     * the {@link User} for a new {@link UserList} does not exist in the database.)
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateUserList(@PathVariable Long id, @RequestBody UserListDTO newUserList) {
        Optional<UserList> existingUserList = userListService.findById(id);

        UserList userList = existingUserList.map(uList -> {
            uList.setName(newUserList.getName());
            uList.setDescription(newUserList.getDescription());
            uList.setPrivate(newUserList.isPrivate());
            return userListService.save(uList);
                })
                .orElseGet(() -> {
                    User user = userService.findById(newUserList.getUserId()).orElseThrow(() -> new UserNotFoundException(newUserList.getUserId()));
                    return userListService.save(userListMapper.toUserList(newUserList, user));
                }
        );

        EntityModel<UserListDTO> entityModel = userListDTOAssembler.toModel(userListMapper.toDTO(userList));

        if (existingUserList.isPresent()) {
            return responseBuilderService.buildNoContentResponseWithLocation(entityModel);
        }
        else {
            return ResponseEntity
                    .created(entityModel.getRequiredLink(IanaLinkRelations.SELF).toUri())
                    .body(entityModel);
        }
    }

    /**
     * Deletes a {@link UserList} by its {@code id}.
     *
     * @param id the ID of the {@link UserList} to be deleted
     * @return ResponseEntity containing a 204 No Content status (or a
     * 404 Not Found status if no {@link UserList} exists with the specified {@code id}.)
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUserList(@PathVariable Long id) {
        userListService.findById(id).orElseThrow(() -> new ObjectNotFoundException(id, "UserList"));
        userListService.deleteById(id);

        return ResponseEntity.noContent().build();
    }

    /**
     * Fetches the {@link User} that owns the {@link UserList} specified by {@code id}.
     *
     * @param id the ID of the {@link UserList} for which the {@link User} is to be retrieved.
     * @return ResponseEntity containing a 200 Ok status and the {@link User} (or a
     * 404 Not Found status if no {@link UserList} exists with the specified {@code id}.)
     */
    @GetMapping("/{id}/user")
    public ResponseEntity<?> findUserByUserList(@PathVariable Long id) {
        UserList userList = userListService.findById(id).orElseThrow(() -> new ObjectNotFoundException(id, "UserList"));
        EntityModel<UserDTO> userDTO = userDTOAssembler.toModel(userMapper.toDTO(userList.getUser()));

        return ResponseEntity.ok(userDTO);
    }

    /**
     * Fetches movies from a {@link UserList} specified by {@code id}.
     *
     * @param id the ID of the {@link UserList} for which the movies are to be retrieved
     * @return ResponseEntity containing a 200 Ok status and the updated {@link UserList} (or a 404
     * Not Found status if no {@link UserList} exists with the specified {@code id}.)
     */
    @GetMapping("/{id}/movies")
    public ResponseEntity<?> findMoviesByUserList(@PathVariable Long id) {
        UserList userList = userListService.findById(id).orElseThrow(() -> new ObjectNotFoundException(id, "UserList"));
        Set<Movie> moviesInUserList = userList.getMovielist();

        if (moviesInUserList.isEmpty()) { return ResponseEntity.ok(CollectionModel.empty()); }

        CollectionModel<EntityModel<Movie>> collectionModel = CollectionModel.of(moviesInUserList.stream()
                .map(movieAssembler::toModel)
                .toList(),
                linkTo(methodOn(UserListController.class).findMoviesByUserList(id)).withSelfRel());

        return ResponseEntity.ok(collectionModel);
    }

    /**
     * Adds a {@link Movie} to the {@link UserList} specified by {@code id}.
     *
     * @param id the ID of the {@link UserList} to which the {@link Movie} is to be added
     * @param movieId the ID of the {@link Movie} to be added
     * @return ResponseEntity indicating the result of the put operation.
     *  Typically, returns a 204 No Content status,
     *  or a 400 Bad Request status if the {@link Movie} already was added. (Returns a 404 Not Found status
     *  if the {@link Movie}/{@link UserList} does not exist.)
     */
    @PutMapping("/{id}/movies")
    public ResponseEntity<?> addMovieToUserList(@PathVariable Long id, @RequestBody Long movieId) {
        UserList userList = userListService.findById(id).orElseThrow(() -> new ObjectNotFoundException(id, "UserList"));
        Movie movie = movieService.findById(movieId).orElseThrow(() -> new MovieNotFoundException(movieId));

        if (userList.addMovie(movie)) {
            EntityModel<UserListDTO> entityModel = userListDTOAssembler.toModel(userListMapper
                    .toDTO(userListService.save(userList)));
            return responseBuilderService.buildNoContentResponseWithLocation(entityModel);
        }
        else {
            return ResponseEntity
                    .badRequest()
                    .body("Adding movie failed. (Movie was already added)");
        }
    }

    /**
     * Deletes a {@link Movie} from the {@link UserList} specified by listId.
     *
     * @param listId the ID of the {@link UserList} from which the {@link Movie} is to be deleted
     * @param movieId the ID of the {@link Movie} to be removed
     * @return ResponseEntity indicating the result of the delete operation.
     *  Typically, a 204 No Content status is returned if the deletion is successful,
     *  or a 404 Not Found error status if the {@link Movie} was not part of the list.
     */
    @DeleteMapping("{listId}/movies/{movieId}")
    public ResponseEntity<?> deleteMovieFromUserList(@PathVariable Long listId, @PathVariable Long movieId) {
        UserList userList = userListService.findById(listId).orElseThrow(() -> new ObjectNotFoundException(listId, "UserList"));

        if (userList.removeMovieById(movieId)) {
            userListService.save(userList);
            return ResponseEntity.noContent().build();
        }
        else {
            throw new MovieNotFoundException(movieId);
        }
    }
}
