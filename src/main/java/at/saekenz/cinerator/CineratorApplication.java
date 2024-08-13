package at.saekenz.cinerator;

import at.saekenz.cinerator.repository.MovieRepository;
import at.saekenz.cinerator.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@SpringBootApplication()
public class CineratorApplication {

    @Autowired
    UserRepository userRepository;

    @Autowired
    MovieRepository movieRepository;

    public static void main(String[] args) {
        SpringApplication.run(CineratorApplication.class, args);
    }

    @GetMapping("/greet")
    public String greet(@RequestParam(value = "myName", defaultValue = "world") String name) {
        return String.format("Hello %s!", name);
    }

}
