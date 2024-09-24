package at.saekenz.cinerator.service;

import at.saekenz.cinerator.model.role.Role;
import at.saekenz.cinerator.model.role.RoleDTO;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Optional;

public interface IRoleService {

    List<Role> findAll();

    Optional<Role> findById(Long id);

    Role findRoleById(Long id);

    Role getReferenceById(Long id);

    Role save(Role role);

    void deleteById(Long id);

    Page<Role> findAllPaged(int page, int size, String sortField, String sortDirection);

    Role createRole(RoleDTO roleDTO);

    Role updateRole(Long id, RoleDTO roleDTO);
}
