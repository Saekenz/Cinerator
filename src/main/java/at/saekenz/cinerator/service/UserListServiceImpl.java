package at.saekenz.cinerator.service;

import at.saekenz.cinerator.model.movie.Movie;
import at.saekenz.cinerator.model.user.User;
import at.saekenz.cinerator.model.userlist.UserList;
import at.saekenz.cinerator.model.userlist.UserListCreationDTO;
import at.saekenz.cinerator.model.userlist.UserListDTO;
import at.saekenz.cinerator.model.userlist.UserListMapper;
import at.saekenz.cinerator.repository.UserListRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class UserListServiceImpl implements IUserListService {
    private final IUserService userService;
    private final IMovieService movieService;

    private static final Logger log = LoggerFactory.getLogger(UserListServiceImpl.class);

    @Autowired
    UserListRepository userListRepository;

    @Autowired
    private UserListMapper userListMapper;

    public UserListServiceImpl(IUserService userService, IMovieService movieService) {
        this.userService = userService;
        this.movieService = movieService;
    }

    @Override
    public List<UserList> findAll() {
        return userListRepository.findAll();
    }

    @Override
    public Optional<UserList> findById(Long id) {
        return userListRepository.findById(id);
    }

    @Override
    public UserList findUserListById(Long id) {
        return userListRepository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        String.format("UserList with id %s could not be found!", id)));
    }

    @Override
    public UserList getReferenceById(Long id) {
        return userListRepository.getReferenceById(id);
    }

    @Override
    public List<UserList> findAllById(Iterable<Long> ids) {
        return userListRepository.findAllById(ids);
    }

    @Override
    public UserList save(UserList userList) {
        return userListRepository.save(userList);
    }

    @Override
    public void deleteById(Long id) {
        findUserListById(id);
        userListRepository.deleteById(id);
    }

    @Override
    public List<UserList> searchUserLists(String name, String description, Long userId) {
        return userListRepository.findUserListsBySearchParams(name, description, userId);
    }

    @Override
    public Page<UserList> findAllPaged(int page, int size, String sortField, String sortDirection) {
        Sort sort = Sort.by(Sort.Direction.fromString(sortDirection), sortField);
        Pageable pageable = PageRequest.of(page, size, sort);

        return userListRepository.findAll(pageable);
    }

    @Override
    public UserList createUserList(UserListCreationDTO userListCreationDTO) {
        User user = userService.findUserById(userListCreationDTO.userId());
        UserList newUserList = userListMapper.toUserList(userListCreationDTO, user);

        return save(newUserList);
    }

    @Override
    public UserList updateUserList(Long id, UserListDTO userListDTO) {
        UserList exisitingUserList = findUserListById(id);

        if (!Objects.equals(userListDTO.getUserId(), exisitingUserList.getUser().getId())) {
            User updatedUser = userService.findUserById(userListDTO.getUserId());
            exisitingUserList.setUser(updatedUser);
        }

        exisitingUserList.setName(userListDTO.getName());
        exisitingUserList.setDescription(userListDTO.getDescription());
        exisitingUserList.setPrivate(userListDTO.isPrivate());

        return save(exisitingUserList);
    }

    @Override
    public User findUserByUserListId(Long userListId) {
        findUserListById(userListId);
        return userListRepository.findUserByUserListId(userListId);
    }

    @Override
    public List<Movie> findMoviesByUserListId(Long userListId) {
        findUserListById(userListId);
        return userListRepository.findMoviesByUserListId(userListId);
    }

    @Override
    public UserList addMovieToUserListById(Long userListId, Long movieId) {
        UserList userList = findUserListById(userListId);
        Movie movie = movieService.getReferenceById(movieId);

        if (userList.addMovie(movie)) {
            log.info("Movie with id {} added to UserList with id {}.", movieId, userListId);
            return save(userList);
        }
        else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    String.format("Movie with id %s already exists in UserList with id %s!",
                            movieId, userListId));
        }
    }

    @Override
    public void removeMovieFromUserListId(Long userListId, Long movieId) {
        UserList foundUserList = findUserListById(userListId);
        boolean isRemoved = foundUserList.removeMovieById(movieId);

        if (isRemoved) {
            log.info("Movie with id {} removed from UserList with id {}.", movieId,userListId);
            save(foundUserList);
        }
        else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    String.format("Movie with id %s was not found in UserList with id %s!",
                            movieId, userListId));
        }
    }
}
