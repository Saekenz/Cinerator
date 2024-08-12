package at.saekenz.cinerator;

import at.saekenz.cinerator.model.movie.Movie;
import at.saekenz.cinerator.model.Review;
import at.saekenz.cinerator.model.user.User;
import at.saekenz.cinerator.repository.MovieRepository;
import at.saekenz.cinerator.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

@SpringBootApplication()
public class CineratorApplication {

    private static final Logger log = LoggerFactory.getLogger(CineratorApplication.class);

    @Autowired
    UserRepository userRepository;

    @Autowired
    MovieRepository movieRepository;

    public static void main(String[] args) {
        SpringApplication.run(CineratorApplication.class, args);
    }

    @Bean
    @ConditionalOnProperty(prefix = "app", name = "db.init.enabled", havingValue = "true")
    public CommandLineRunner initUsers() {
        return (args) -> {

            log.info("Initializing users...");

            BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
            String encodedPassword = passwordEncoder.encode("password");

            User u1 = new User("UserA", encodedPassword, "USER", true);
            User u2 = new User("UserB", encodedPassword, "USER", true);
            User u3 = new User("UserC", encodedPassword, "ADMIN", true);
            User u4 = new User("UserD", encodedPassword, "USER", false);

            for(User u : userRepository.saveAll(List.of(u1,u2,u3,u4))) {
                log.info("Created new user: {}", u);
            }
        };
    }

    @Bean
    public CommandLineRunner initMovies() {
        return (args) -> {
            log.info("Initializing movies...");

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            List<Review> reviews = new ArrayList<>();

            Movie m1 = new Movie("Sicario", "Denis Villeneuve", sdf.parse("2015-10-01"), "Thriller",
                    "United States","tt3397884", reviews);
            Movie m2 = new Movie("Dune: Part Two", "Denis Villeneuve", sdf.parse("2024-03-01"), "Science Fiction",
                    "United States","tt15239678", reviews);
            Movie m3 = new Movie("Good Will Hunting", "Gus Van Sant", sdf.parse("1998-01-09"), "Drama",
                    "United States","tt0119217", reviews);
            Movie m4 = new Movie("Three Colors: Red", "Krzysztof Kieslowski", sdf.parse("1994-11-23"), "Drama",
                    "France","tt0111495", reviews);

            for(Movie m : movieRepository.saveAll(List.of(m1,m2,m3,m4))) {
                log.info("Created new movie: {}", m);
            }
        };
    }

    @GetMapping("/greet")
    public String greet(@RequestParam(value = "myName", defaultValue = "world") String name) {
        return String.format("Hello %s!", name);
    }

}
