package at.saekenz.cinerator.service;

import at.saekenz.cinerator.model.role.Role;
import at.saekenz.cinerator.model.role.RoleDTO;
import at.saekenz.cinerator.model.role.RoleMapper;
import at.saekenz.cinerator.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
public class RoleServiceImpl implements IRoleService {

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private RoleMapper roleMapper;

    @Override
    public List<Role> findAll() {
        return roleRepository.findAll();
    }

    @Override
    public Optional<Role> findById(Long id) {
        return roleRepository.findById(id);
    }

    @Override
    public Role findRoleById(Long id) {
        return roleRepository.findById(id).orElseThrow(
                () ->  new ResponseStatusException(HttpStatus.NOT_FOUND,
                        String.format("Role with id %s could not be found!", id)));
    }

    @Override
    public Role getReferenceById(Long id) {
        return roleRepository.getReferenceById(id);
    }

    @Override
    public Role save(Role role) {
        return roleRepository.save(role);
    }

    @Override
    public void deleteById(Long id) {
        findRoleById(id);
        roleRepository.deleteById(id);
    }

    @Override
    public Page<Role> findAllPaged(int page, int size, String sortField, String sortDirection) {
        Sort sort = Sort.by(Sort.Direction.fromString(sortDirection), sortField);
        Pageable pageable = PageRequest.of(page, size, sort);

        return roleRepository.findAll(pageable);
    }

    @Override
    public Role createRole(RoleDTO roleDTO) {
        Role newRole = roleMapper.toRole(roleDTO);

        return save(newRole);
    }

    @Override
    public Role updateRole(Long id, RoleDTO roleDTO) {
        Role existingRole = findRoleById(id);
        existingRole.setRole(roleDTO.role());

        return save(existingRole);
    }
}
