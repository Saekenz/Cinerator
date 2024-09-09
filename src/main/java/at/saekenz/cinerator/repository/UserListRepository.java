package at.saekenz.cinerator.repository;

import at.saekenz.cinerator.model.userlist.UserList;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserListRepository extends JpaRepository<UserList, Long> {
}
