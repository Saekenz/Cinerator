package at.saekenz.cinerator.service;

import at.saekenz.cinerator.model.userlist.UserList;
import at.saekenz.cinerator.repository.UserListRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserListServiceImpl implements IUserListService {

    @Autowired
    UserListRepository userListRepository;

    @Override
    public List<UserList> findAll() {
        return userListRepository.findAll();
    }

    @Override
    public Optional<UserList> findById(Long id) {
        return userListRepository.findById(id);
    }

    @Override
    public List<UserList> findAllById(Iterable<Long> ids) {
        return userListRepository.findAllById(ids);
    }

    @Override
    public UserList save(UserList userList) {
        return userListRepository.save(userList);
    }

    @Override
    public void deleteById(Long id) {
        userListRepository.deleteById(id);
    }
}
