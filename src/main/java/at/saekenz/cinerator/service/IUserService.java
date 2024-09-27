package at.saekenz.cinerator.service;

import at.saekenz.cinerator.model.follow.Follow;
import at.saekenz.cinerator.model.movie.Movie;
import at.saekenz.cinerator.model.review.Review;
import at.saekenz.cinerator.model.user.User;
import at.saekenz.cinerator.model.user.UserCreationDTO;
import at.saekenz.cinerator.model.user.UserDTO;
import at.saekenz.cinerator.model.userlist.UserList;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Optional;

public interface IUserService {

    User registerNewUser(User user);

    User getUserByEmail(String email);

    List<User> findAll();

    Optional<User> findById(Long id);

    User findUserById(Long id);

    User getReferenceById(Long id);

    List<User> findAllById(Iterable<Long> ids);

    User save(User user);

    void deleteById(Long id);

    List<User> findByUsername(String username);

    List<User> findUsersByRole(String role);

    List<Movie> findMoviesLikedByUser(Long userId);

    List<Movie> findMoviesRatedByUser(Long userId, Integer rating);

    List<User> searchUsers(String name, String username, String email, String role);

    Page<User> findAllPaged(int page, int size, String sortField, String sortDirection);

    User createUser(UserCreationDTO userCreationDTO);

    User updateUser(Long id, UserDTO userDTO);

    Movie findMovieInUsersWatchlist(Long userId, Long movieId);

    User addMovieToWatchlistById(Long userId, Long movieId);

    void removeMovieFromWatchlistById(Long userId, Long movieId);

    List<Review> findReviewsByUserId(Long userId);

    List<User> findFollowersByUserId(Long userId);

    List<User> findFollowingByUserId(Long userId);

    List<UserList> findUserListsByUserId(Long userId);

    Follow followAnotherUser(Long userId, Long followerId);

    void unfollowAnotherUser(Long userId, Long followerId);

    void enableUser(Long userId);
}
