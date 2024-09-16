package at.saekenz.cinerator.service;

import at.saekenz.cinerator.model.role.Role;

import java.util.List;
import java.util.Optional;

public interface IRoleService {

    List<Role> findAll();

    Optional<Role> findById(Long id);

    Role getReferenceById(Long id);

    Role save(Role role);

    void delete(Role role);
}
