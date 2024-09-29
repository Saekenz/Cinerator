package at.saekenz.cinerator.repository;

import at.saekenz.cinerator.model.movie.Movie;
import at.saekenz.cinerator.model.user.User;
import at.saekenz.cinerator.model.userlist.UserList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserListRepository extends JpaRepository<UserList, Long> {

    @Query("SELECT u FROM UserList u WHERE " +
            "(LOWER(u.name) LIKE LOWER(CONCAT('%', :name, '%')) OR :name IS NULL) AND " +
            "(LOWER(u.description) LIKE LOWER(CONCAT('%', :description, '%')) OR :description IS NULL) AND " +
            "(u.user.id = :userId OR :userId IS NULL)")
    List<UserList> findUserListsBySearchParams(@Param("name") String name,
                                               @Param("description") String description,
                                               @Param("userId") Long userId);

    @Query("SELECT ul.user FROM UserList ul WHERE " +
            "ul.id = :userListId")
    User findUserByUserListId(@Param("userListId") Long userListId);

    @Query("SELECT ul.movielist FROM UserList ul WHERE " +
            "ul.id = :userListId")
    List<Movie> findMoviesByUserListId(@Param("userListId") Long userListId);
}
