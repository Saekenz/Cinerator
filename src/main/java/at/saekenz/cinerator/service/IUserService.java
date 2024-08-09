package at.saekenz.cinerator.service;

import at.saekenz.cinerator.model.user.User;

import java.util.List;
import java.util.Optional;

public interface IUserService {

    User registerNewUser(User user);

    User getUserByEmail(String email);

    List<User> findAll();

    Optional<User> findById(Long id);

    User save(User user);

    void deleteById(Long id);

    List<User> findByUsername(String username);

    List<User> findUsersByRole(String role);
}
