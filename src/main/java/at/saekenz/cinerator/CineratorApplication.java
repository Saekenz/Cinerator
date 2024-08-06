package at.saekenz.cinerator;

import at.saekenz.cinerator.model.User;
import at.saekenz.cinerator.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@SpringBootApplication()
public class CineratorApplication {

    private static final Logger log = LoggerFactory.getLogger(CineratorApplication.class);

    @Autowired
    UserRepository userRepository;

    public static void main(String[] args) {
        SpringApplication.run(CineratorApplication.class, args);
    }

    @Bean
    @ConditionalOnProperty(prefix = "app", name = "db.init.enabled", havingValue = "true")
    public CommandLineRunner testDbInsert() {
        return (args) -> {

            System.out.println("Running....");

            User u1 = new User("UserA", "password", "USER", true);
            User u2 = new User("UserB", "password", "USER", true);
            User u3 = new User("UserC", "password", "ADMIN", true);
            User u4 = new User("UserD", "password", "USER", false);

            userRepository.saveAll(List.of(u1,u2,u3,u4));
        };
    }

    @GetMapping("/greet")
    public String greet(@RequestParam(value = "myName", defaultValue = "world") String name) {
        return String.format("Hello %s!", name);
    }

}
