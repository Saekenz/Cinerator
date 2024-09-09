package at.saekenz.cinerator.service;

import at.saekenz.cinerator.model.userlist.UserList;

import java.util.List;
import java.util.Optional;

public interface IUserListService {

    List<UserList> findAll();

    Optional<UserList> findById(Long id);

    List<UserList> findAllById(Iterable<Long> ids);

    UserList save(UserList userList);

    void deleteById(Long id);
}
