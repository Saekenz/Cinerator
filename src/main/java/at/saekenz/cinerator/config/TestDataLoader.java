package at.saekenz.cinerator.config;

import at.saekenz.cinerator.model.review.Review;
import at.saekenz.cinerator.model.movie.Movie;
import at.saekenz.cinerator.model.user.User;
import at.saekenz.cinerator.repository.MovieRepository;
import at.saekenz.cinerator.repository.ReviewRepository;
import at.saekenz.cinerator.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Configuration
public class TestDataLoader {

    private static final Logger log = LoggerFactory.getLogger(TestDataLoader.class);

    @Bean
    @Order(1)
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
    @Order(2)
    public CommandLineRunner initMovies(MovieRepository movieRepository) {
        return (args) -> {
            log.info("Initializing movies...");

            Movie m1 = new Movie("Sicario", "Denis Villeneuve", LocalDate.of(2015,10,1),
                    "Thriller","United States","tt3397884");

            Movie m2 = new Movie("Dune: Part Two", "Denis Villeneuve", LocalDate.of(2024,3,1),
                    "Science Fiction", "United States","tt15239678");

            Movie m3 = new Movie("Good Will Hunting", "Gus Van Sant", LocalDate.of(1998,9,1),
                    "Drama", "United States","tt0119217");

            Movie m4 = new Movie("Three Colors: Red", "Krzysztof Kieslowski", LocalDate.of(1994,11,23),
                    "Drama","France","tt0111495");

            Movie m5 = new Movie("Inception", "Christopher Nolan", LocalDate.of(2010,7,16),
                    "Science Fiction", "United States", "tt1375666");

            Movie m6 = new Movie("Parasite", "Bong Joon Ho", LocalDate.of(2019,5,30),
                    "Thriller", "South Korea", "tt6751668");

            Movie m7 = new Movie("The Grand Budapest Hotel", "Wes Anderson", LocalDate.of(2014,3,28),
                    "Comedy", "United States", "tt2278388");

            Movie m8 = new Movie("Spirited Away", "Hayao Miyazaki", LocalDate.of(2001,7,20),
                    "Fantasy", "Japan", "tt0245429");

            Movie m9 = new Movie("The Godfather", "Francis Ford Coppola", LocalDate.of(1972,3,24),
                    "Crime", "United States", "tt0068646");

            Movie m10 = new Movie("Amélie", "Jean-Pierre Jeunet", LocalDate.of(2001,4,25),
                    "Romantic Comedy", "France", "tt0211915");

            Movie m11 = new Movie("Pulp Fiction", "Quentin Tarantino", LocalDate.of(1994,10,14),
                    "Crime", "United States", "tt0110912");

            Movie m12 = new Movie("The Dark Knight", "Christopher Nolan", LocalDate.of(2008,7,18),
                    "Action", "United States", "tt0468569");

            Movie m13 = new Movie("La La Land", "Damien Chazelle", LocalDate.of(2016,12,9),
                    "Musical", "United States", "tt3783958");

            for(Movie m : movieRepository.saveAll(List.of(m1,m2,m3,m4,m5,m6,m7,m8,m9,m10,m11,m12,m13))) {
                log.info("Created new movie: {}", m);
            }
        };
    }

    @Bean
    @Order(3)
    public CommandLineRunner initReviews(ReviewRepository reviewRepository, UserRepository userRepository,
                                         MovieRepository movieRepository) {
        return (args) -> {
            log.info("Initializing reviews...");

            User u1 = userRepository.findById(1L).get();
            User u2 = userRepository.findById(2L).get();

            Movie m1 = movieRepository.findById(1L).get();
            Movie m2 = movieRepository.findById(2L).get();
            Movie m3 = movieRepository.findById(3L).get();

            Review r1 = new Review("An absolute visual treat. The cinematography is breathtaking, but the plot feels like it's treading water.",
                    3, LocalDate.of(2020,5,8), true, u1, m1);
            Review r2 = new Review("A rollercoaster of emotions from start to finish.",
                    4, LocalDate.of(2024,8,1), true, u1, m2);
            Review r3 = new Review("I wanted to love this, but it felt like a missed opportunity. The concept was intriguing, but the execution left much to be desired.",
                    2, LocalDate.of(2000,7,4), false, u1, m3);
            Review r4 = new Review("This one took me by surprise. A slow burn that pays off in the end with a haunting finale.",
                    5, LocalDate.of(2022,2,27), true, u2, m1);

            for(Review r : reviewRepository.saveAll(List.of(r1,r2,r3,r4))) {
                log.info("Created new review: {}", r);
            }
        };
    }
}
