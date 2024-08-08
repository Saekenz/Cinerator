package at.saekenz.cinerator.config;

import at.saekenz.cinerator.model.User;
import at.saekenz.cinerator.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.getUserByUsername(username);

        if(user == null) {
            throw new UsernameNotFoundException("Could not find user: " + username);
        }

        return new MyUserDetails(user);
    }
}
