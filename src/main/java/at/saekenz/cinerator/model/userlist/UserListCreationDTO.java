package at.saekenz.cinerator.model.userlist;

public record UserListCreationDTO(String name,
                                  String description,
                                  boolean isPrivate,
                                  Long userId) {

}
