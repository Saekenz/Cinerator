package at.saekenz.cinerator.model.user;

import org.springframework.stereotype.Component;

import java.util.Collections;

@Component
public class UserMapper {

    public UserDTO toDTO(User user) {
        UserDTO userDTO = new UserDTO();

        userDTO.setId(user.getId());
        userDTO.setUsername(user.getUsername());
        userDTO.setName(user.getName());
        userDTO.setEmail(user.getEmail());
        userDTO.setBio(user.getBio());
        userDTO.setRole(user.getRole());
        userDTO.setEnabled(user.isEnabled());
        userDTO.setCreatedAt(user.getCreatedAt());

        return userDTO;
    }

    public User toUser(UserCreationDTO creationDTO) {
        return new User(creationDTO.getUsername(),
                "",
                creationDTO.getPassword(),
                creationDTO.getEmail(),
                "",
                "USER",
                false,
                Collections.emptySet());
    }
}
