package at.saekenz.cinerator.controller;

import at.saekenz.cinerator.model.movie.Movie;
import at.saekenz.cinerator.model.movie.MovieModelAssembler;
import at.saekenz.cinerator.model.user.*;
import at.saekenz.cinerator.model.userlist.*;
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
     *
     * @return
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
     *
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<UserListDTO>> findById(@PathVariable Long id) {
        UserList userList = userListService.findById(id).orElseThrow(() -> new ObjectNotFoundException(id, "UserList"));

        return ResponseEntity.ok(userListDTOAssembler.toModel(userListMapper.toDTO(userList)));
    }

    /**
     *
     * @param userListCreationDTO
     * @return
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
     *
     * @param id
     * @param newUserList
     * @return
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
     *
     * @param id
     * @return
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUserList(@PathVariable Long id) {
        userListService.findById(id).orElseThrow(() -> new ObjectNotFoundException(id, "UserList"));
        userListService.deleteById(id);

        return ResponseEntity.noContent().build();
    }

    /**
     *
     * @param id
     * @return
     */
    @GetMapping("/{id}/user")
    public ResponseEntity<?> findUserByUserList(@PathVariable Long id) {
        UserList userList = userListService.findById(id).orElseThrow(() -> new ObjectNotFoundException(id, "UserList"));
        EntityModel<UserDTO> userDTO = userDTOAssembler.toModel(userMapper.toDTO(userList.getUser()));

        return ResponseEntity.ok(userDTO);
    }

    /**
     *
     * @param id
     * @return
     */
    @GetMapping("/{id}/movies")
    public ResponseEntity<?> findMoviesByUserList(@PathVariable Long id) {
        UserList userList = userListService.findById(id).orElseThrow(() -> new ObjectNotFoundException(id, "UserList"));
        List<Movie> moviesInUserList = userList.getMovielist();

        if (moviesInUserList.isEmpty()) { return ResponseEntity.ok(CollectionModel.empty()); }

        CollectionModel<EntityModel<Movie>> collectionModel = CollectionModel.of(moviesInUserList.stream()
                .map(movieAssembler::toModel)
                .toList(),
                linkTo(methodOn(UserListController.class).findMoviesByUserList(id)).withSelfRel());

        return ResponseEntity.ok(collectionModel);
    }

    /**
     *
     * @param id
     * @return
     */
    @PutMapping("/{id}/movies")
    public ResponseEntity<?> addMovieToUserList(@PathVariable Long id) {
        // TODO: implement
        return ResponseEntity.noContent().build();
    }

    /**
     *
     * @param id
     * @return
     */
    @DeleteMapping("{id}/movies")
    public ResponseEntity<?> deleteMovieFromUserList(@PathVariable Long id) {
        // TODO: implement
        return ResponseEntity.noContent().build();
    }
}
