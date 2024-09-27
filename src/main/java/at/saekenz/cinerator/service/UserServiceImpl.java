package at.saekenz.cinerator.service;

import at.saekenz.cinerator.model.follow.Follow;
import at.saekenz.cinerator.model.follow.FollowKey;
import at.saekenz.cinerator.model.movie.Movie;
import at.saekenz.cinerator.model.review.Review;
import at.saekenz.cinerator.model.user.User;
import at.saekenz.cinerator.model.user.UserCreationDTO;
import at.saekenz.cinerator.model.user.UserDTO;
import at.saekenz.cinerator.model.user.UserMapper;
import at.saekenz.cinerator.model.userlist.UserList;
import at.saekenz.cinerator.repository.UserRepository;
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
import java.util.Optional;

@Service
public class UserServiceImpl implements IUserService {
    private final IMovieService movieService;
    private final IFollowService followService;

    private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserMapper userMapper;

    public UserServiceImpl(IMovieService movieService, IFollowService followService) {
        this.movieService = movieService;
        this.followService = followService;
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
        findUserById(userId);
        return userRepository.findMoviesLikedByUser(userId);
    }

    @Override
    public List<Movie> findMoviesRatedByUser(Long userId, Integer rating) {
        findUserById(userId);
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
            log.info("Movie with id {} added to watchlist of User with id {}.", movieId, userId);
            return save(foundUser);
        }
        else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    String.format("Movie with id %s already exists in watchlist belonging to User with id %s!",
                            movieId, userId));
        }
    }

    @Override
    public void removeMovieFromWatchlistById(Long userId, Long movieId) {
        User foundUser = findUserById(userId);
        boolean isRemoved = foundUser.removeMovieFromWatchlist(movieId);

        if (isRemoved) {
            log.info("Movie with id {} removed from watchlist of User with id {}.", movieId, userId);
            save(foundUser);
        }
        else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    String.format("Movie with id %s was not found in watchlist belonging to User with id %s!",
                            movieId, userId));
        }
    }

    @Override
    public List<Review> findReviewsByUserId(Long userId) {
        findUserById(userId);
        return userRepository.findReviewsByUserId(userId);
    }

    @Override
    public List<User> findFollowersByUserId(Long userId) {
        findUserById(userId);
        return userRepository.findFollowersByUserId(userId);
    }

    @Override
    public List<User> findFollowingByUserId(Long userId) {
        findUserById(userId);
        return userRepository.findFollowingByUserId(userId);
    }

    @Override
    public List<UserList> findUserListsByUserId(Long userId) {
        findUserById(userId);
        return userRepository.findUserListsByUserId(userId);
    }

    @Override
    public Follow followAnotherUser(Long userId, Long followerId) {
        User user = findUserById(userId);
        User follower = findUserById(followerId);

        FollowKey followKey = new FollowKey(userId, followerId);

        if (followService.findByKey(followKey).isEmpty()) {
            return followService.save(new Follow(followKey, user, follower));
        }
        else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    String.format("User with id %s is already following User with id %s!",followerId, userId));
        }
    }

    @Override
    public void unfollowAnotherUser(Long userId, Long followerId) {
        FollowKey followKey = new FollowKey(userId, followerId);
        if (followService.findByKey(followKey).isPresent()) {
            followService.deleteByKey(followKey);
        }
        else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    String.format("User with id %s is not following User with id %s!",followerId, userId));
        }
    }

    @Override
    public void enableUser(Long userId) {
        if (userRepository.enableUser(userId) == 0) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    String.format("User with id %s could not be found!", userId));
        }
    }
}
