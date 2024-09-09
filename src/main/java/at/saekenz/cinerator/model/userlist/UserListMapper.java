package at.saekenz.cinerator.model.userlist;

import at.saekenz.cinerator.model.user.User;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class UserListMapper {

    public UserListDTO toDTO(UserList userList) {
        UserListDTO userListDTO = new UserListDTO();

        userListDTO.setId(userList.getId());
        userListDTO.setName(userList.getName());
        userListDTO.setDescription(userList.getDescription());
        userListDTO.setPrivate(userList.isPrivate());
        userListDTO.setUserId(userList.getId());
        userListDTO.setCreatedAt(userList.getCreatedAt());

        return userListDTO;
    }

    public UserList toUserList(UserListCreationDTO userListCreationDTO, User user) {
        return new UserList(userListCreationDTO.name(),
                userListCreationDTO.description(),
                userListCreationDTO.isPrivate(),
                user,
                Set.of());
    }

    public UserList toUserList(UserListDTO userListDTO, User user) {
        return new UserList(userListDTO.getName(),
                userListDTO.getDescription(),
                userListDTO.isPrivate(),
                user,
                Set.of());
    }
}
