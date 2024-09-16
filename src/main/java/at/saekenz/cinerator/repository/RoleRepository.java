package at.saekenz.cinerator.repository;

import at.saekenz.cinerator.model.role.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long> {

}
