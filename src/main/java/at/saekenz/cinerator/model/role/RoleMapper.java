package at.saekenz.cinerator.model.role;

import at.saekenz.cinerator.util.EntityMapper;
import org.springframework.stereotype.Component;

@Component
public class RoleMapper implements EntityMapper<Role, RoleDTO> {

    @Override
    public RoleDTO toDTO(Role role) {
        return new RoleDTO(role.getId(),
                role.getRole());
    }

    public Role toRole(RoleDTO roleDTO) {
        return new Role(roleDTO.role());
    }
}
