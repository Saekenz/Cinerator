package at.saekenz.cinerator.service;

import at.saekenz.cinerator.model.movie.Movie;
import at.saekenz.cinerator.model.user.User;
import at.saekenz.cinerator.model.userlist.UserList;
import at.saekenz.cinerator.model.userlist.UserListCreationDTO;
import at.saekenz.cinerator.model.userlist.UserListDTO;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Optional;

public interface IUserListService {

    List<UserList> findAll();

    Optional<UserList> findById(Long id);

    UserList findUserListById(Long id);

    UserList getReferenceById(Long id);

    List<UserList> findAllById(Iterable<Long> ids);

    UserList save(UserList userList);

    void deleteById(Long id);

    List<UserList> searchUserLists(String name, String description, Long userId);

    Page<UserList> findAllPaged(int page, int size, String sortField, String sortDirection);

    UserList createUserList(UserListCreationDTO userListCreationDTO);

    UserList updateUserList(Long id, UserListDTO userListDTO);

    User findUserByUserListId(Long userListId);

    List<Movie> findMoviesByUserListId(Long userListId);

    UserList addMovieToUserListById(Long userListId, Long movieId);

    void removeMovieFromUserListId(Long userListId, Long movieId);
}
