package at.saekenz.cinerator.service;

import at.saekenz.cinerator.model.movie.Movie;
import at.saekenz.cinerator.model.user.User;

import java.util.List;
import java.util.Optional;

public interface IUserService {

    User registerNewUser(User user);

    User getUserByEmail(String email);

    List<User> findAll();

    Optional<User> findById(Long id);

    User getReferenceById(Long id);

    List<User> findAllById(Iterable<Long> ids);

    User save(User user);

    void deleteById(Long id);

    List<User> findByUsername(String username);

    List<User> findUsersByRole(String role);

    List<Movie> findMoviesLikedByUser(Long userId);

    List<Movie> findMoviesRatedByUser(Long userId, Integer rating);

    List<User> searchUsers(String name, String username, String email, String role);
}
