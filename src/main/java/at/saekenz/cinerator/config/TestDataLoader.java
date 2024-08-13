package at.saekenz.cinerator.config;

import at.saekenz.cinerator.model.Review;
import at.saekenz.cinerator.model.movie.Movie;
import at.saekenz.cinerator.model.user.User;
import at.saekenz.cinerator.repository.MovieRepository;
import at.saekenz.cinerator.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Configuration
public class TestDataLoader {

    private static final Logger log = LoggerFactory.getLogger(TestDataLoader.class);

    @Bean
    public CommandLineRunner initUsers(UserRepository userRepository) {
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
    public CommandLineRunner initMovies(MovieRepository movieRepository) {
        return (args) -> {
            log.info("Initializing movies...");

            List<Review> reviews = new ArrayList<>();

            Movie m1 = new Movie("Sicario", "Denis Villeneuve", LocalDate.of(2015,10,1),
                    "Thriller","United States","tt3397884", reviews);
            Movie m2 = new Movie("Dune: Part Two", "Denis Villeneuve", LocalDate.of(2024,3,1),
                    "Science Fiction", "United States","tt15239678", reviews);
            Movie m3 = new Movie("Good Will Hunting", "Gus Van Sant", LocalDate.of(1998,9,1),
                    "Drama", "United States","tt0119217", reviews);
            Movie m4 = new Movie("Three Colors: Red", "Krzysztof Kieslowski", LocalDate.of(1994,11,23),
                    "Drama","France","tt0111495", reviews);

            for(Movie m : movieRepository.saveAll(List.of(m1,m2,m3,m4))) {
                log.info("Created new movie: {}", m);
            }
        };
    }
}
