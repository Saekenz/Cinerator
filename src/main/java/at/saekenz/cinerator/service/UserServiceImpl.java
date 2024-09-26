package at.saekenz.cinerator.service;

import at.saekenz.cinerator.model.movie.Movie;
import at.saekenz.cinerator.model.user.User;
import at.saekenz.cinerator.model.user.UserCreationDTO;
import at.saekenz.cinerator.model.user.UserDTO;
import at.saekenz.cinerator.model.user.UserMapper;
import at.saekenz.cinerator.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements IUserService {
    private final IMovieService movieService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserMapper userMapper;

    public UserServiceImpl(IMovieService movieService) {
        this.movieService = movieService;
    }

    @Override
    public User registerNewUser(User user) {
        return null;
    }

    @Override
    public User getUserByEmail(String email) {
        return null;
    }

    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Override
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    @Override
    public User findUserById(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                String.format("User with id %s could not be found!", id)));
    }

    @Override
    public User getReferenceById(Long id) {
        return userRepository.getReferenceById(id);
    }

    @Override
    public List<User> findAllById(Iterable<Long> ids) {
        return userRepository.findAllById(ids);
    }

    @Override
    public User save(User user) {
        return userRepository.save(user);
    }

    @Override
    public void deleteById(Long id) {
        findUserById(id);
        userRepository.deleteById(id);
    }

    @Override
    public List<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public List<User> findUsersByRole(String role) {
        return userRepository.findUsersByRole(role);
    }

    @Override
    public List<Movie> findMoviesLikedByUser(Long userId) {
        return userRepository.findMoviesLikedByUser(userId);
    }

    @Override
    public List<Movie> findMoviesRatedByUser(Long userId, Integer rating) {
        return userRepository.findMoviesRatedByUser(userId, rating);
    }

    @Override
    public List<User> searchUsers(String name, String username, String email, String role) {
        return userRepository.findUsersBySearchParams(name, username, email, role);
    }

    @Override
    public Page<User> findAllPaged(int page, int size, String sortField, String sortDirection) {
        Sort sort = Sort.by(Sort.Direction.fromString(sortDirection), sortField);
        Pageable pageable = PageRequest.of(page, size, sort);

        return userRepository.findAll(pageable);
    }

    @Override
    public User createUser(UserCreationDTO userCreationDTO) {
        User newUser = userMapper.toUser(userCreationDTO);

        return save(newUser);
    }

    @Override
    public User updateUser(Long id, UserDTO userDTO) {
        User existingUser = findUserById(id);

        existingUser.setUsername(userDTO.getUsername());
        existingUser.setEmail(userDTO.getEmail());
        existingUser.setName(userDTO.getName());
        existingUser.setBio(userDTO.getBio());

        return save(existingUser);
    }

    @Override
    public Movie findMovieInUsersWatchlist(Long userId, Long movieId) {
        return userRepository.findMovieInUsersWatchlist(userId, movieId)
                .orElseThrow(() ->  new ResponseStatusException(HttpStatus.NOT_FOUND,
                        String.format("Movie with id %s could not be found in watchlist of User with id %s!",
                                movieId, userId)));
    }

    @Override
    public User addMovieToWatchlistById(Long userId, Long movieId) {
        User foundUser = findUserById(userId);
        Movie foundMovie = movieService.getReferenceById(movieId);

        if (foundUser.addMovieToWatchlist(foundMovie)) {
            return save(foundUser);
        }
        else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    String.format("Movie with id %s already exists in watchlist belonging to User with id %s!",
                            movieId, userId));
        }
    }
}
