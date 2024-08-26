package at.saekenz.cinerator.config;

import at.saekenz.cinerator.model.actor.Actor;
import at.saekenz.cinerator.model.movie.MovieNotFoundException;
import at.saekenz.cinerator.model.review.Review;
import at.saekenz.cinerator.model.movie.Movie;
import at.saekenz.cinerator.model.user.User;
import at.saekenz.cinerator.model.user.UserNotFoundException;
import at.saekenz.cinerator.repository.ActorRepository;
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
import java.util.List;

@Configuration
public class TestDataLoader {

    private static final Logger log = LoggerFactory.getLogger(TestDataLoader.class);

    @Bean
    @Order(2)
    public CommandLineRunner initUsers(UserRepository userRepository, MovieRepository movieRepository) {
        return (args) -> {

            log.info("Initializing users...");

            BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
            String encodedPassword = passwordEncoder.encode("password");

            Movie m1 = movieRepository.findById(5L).orElseThrow(() ->new MovieNotFoundException(5L));
            Movie m2 = movieRepository.findById(13L).orElseThrow(() ->new MovieNotFoundException(13L));
            Movie m3 = movieRepository.findById(1L).orElseThrow(() ->new MovieNotFoundException(1L));
            Movie m4 = movieRepository.findById(7L).orElseThrow(() ->new MovieNotFoundException(7L));
            Movie m5 = movieRepository.findById(2L).orElseThrow(() ->new MovieNotFoundException(2L));
            Movie m6 = movieRepository.findById(3L).orElseThrow(() ->new MovieNotFoundException(3L));
            Movie m7 = movieRepository.findById(6L).orElseThrow(() ->new MovieNotFoundException(6L));

            User u1 = new User("UserA", encodedPassword, "USER", true, List.of(m1,m2,m3));
            User u2 = new User("UserB", encodedPassword, "USER", true, List.of(m4,m5,m6));
            User u3 = new User("UserC", encodedPassword, "ADMIN", true, List.of(m7,m1,m5));
            User u4 = new User("UserD", encodedPassword, "USER", false, List.of(m5,m4,m7));

            for(User u : userRepository.saveAll(List.of(u1,u2,u3,u4))) {
                log.info("Created new user: {}", u);
            }
        };
    }

    @Bean
    @Order(1)
    public CommandLineRunner initMovies(MovieRepository movieRepository) {
        return (args) -> {
            log.info("Initializing movies...");

            Movie m1 = new Movie("Sicario", "Denis Villeneuve", LocalDate.of(2015,10,1), "122 min",
                    "Thriller","United States","tt3397884","https://upload.wikimedia.org/wikipedia/en/4/4b/Sicario_poster.jpg");

            Movie m2 = new Movie("Dune: Part Two", "Denis Villeneuve", LocalDate.of(2024,3,1), "167 min",
                    "Science Fiction", "United States","tt15239678","https://upload.wikimedia.org/wikipedia/en/5/52/Dune_Part_Two_poster.jpeg");

            Movie m3 = new Movie("Good Will Hunting", "Gus Van Sant", LocalDate.of(1998,9,1), "127 min",
                    "Drama", "United States","tt0119217","https://upload.wikimedia.org/wikipedia/en/5/52/Good_Will_Hunting.png");

            Movie m4 = new Movie("Three Colors: Red", "Krzysztof Kieslowski", LocalDate.of(1994,11,23), "100 min",
                    "Drama","France","tt0111495","https://upload.wikimedia.org/wikipedia/en/0/0a/Three_Colors-Red.jpg");

            Movie m5 = new Movie("Inception", "Christopher Nolan", LocalDate.of(2010,7,16), "148 min",
                    "Science Fiction", "United States", "tt1375666","https://upload.wikimedia.org/wikipedia/en/2/2e/Inception_%282010%29_theatrical_poster.jpg");

            Movie m6 = new Movie("Parasite", "Bong Joon Ho", LocalDate.of(2019,5,30), "133 min",
                    "Thriller", "South Korea", "tt6751668","https://upload.wikimedia.org/wikipedia/en/5/53/Parasite_%282019_film%29.png");

            Movie m7 = new Movie("The Grand Budapest Hotel", "Wes Anderson", LocalDate.of(2014,3,28), "100 min",
                    "Comedy", "United States", "tt2278388","https://upload.wikimedia.org/wikipedia/en/1/1c/The_Grand_Budapest_Hotel.png");

            Movie m8 = new Movie("Spirited Away", "Hayao Miyazaki", LocalDate.of(2001,7,20), "125 min",
                    "Fantasy", "Japan", "tt0245429","https://upload.wikimedia.org/wikipedia/en/d/db/Spirited_Away_Japanese_poster.png");

            Movie m9 = new Movie("The Godfather", "Francis Ford Coppola", LocalDate.of(1972,3,24), "175 min",
                    "Crime", "United States", "tt0068646","https://upload.wikimedia.org/wikipedia/en/1/1c/Godfather_ver1.jpg");

            Movie m10 = new Movie("Amélie", "Jean-Pierre Jeunet", LocalDate.of(2001,4,25), "122 min",
                    "Romantic Comedy", "France", "tt0211915","https://upload.wikimedia.org/wikipedia/en/5/53/Amelie_poster.jpg");

            Movie m11 = new Movie("Pulp Fiction", "Quentin Tarantino", LocalDate.of(1994,10,14),"154 min",
                    "Crime", "United States", "tt0110912","https://upload.wikimedia.org/wikipedia/en/3/3b/Pulp_Fiction_%281994%29_poster.jpg");

            Movie m12 = new Movie("The Dark Knight", "Christopher Nolan", LocalDate.of(2008,7,18),"152 min",
                    "Action", "United States", "tt0468569","https://upload.wikimedia.org/wikipedia/en/1/1c/The_Dark_Knight_%282008_film%29.jpg");

            Movie m13 = new Movie("La La Land", "Damien Chazelle", LocalDate.of(2016,12,9),"129 min",
                    "Musical", "United States", "tt3783958","https://upload.wikimedia.org/wikipedia/en/a/ab/La_La_Land_%28film%29.png");

//            Movie m14 = new Movie("Nightcrawler","Dan Gilroy", LocalDate.of(2014,10,31),"Thriller", "118 min",
//                    "United States","tt287271","https://upload.wikimedia.org/wikipedia/en/d/d4/Nightcrawlerfilm.jpg");

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

            User u1 = userRepository.findById(1L).orElseThrow(() -> new UserNotFoundException(1L));
            User u2 = userRepository.findById(2L).orElseThrow(() -> new UserNotFoundException(2L));
            User u3 = userRepository.findById(3L).orElseThrow(() -> new UserNotFoundException(3L));
            User u4 = userRepository.findById(4L).orElseThrow(() -> new UserNotFoundException(4L));

            Movie m1 = movieRepository.findById(1L).orElseThrow(() -> new MovieNotFoundException(1L));
            Movie m2 = movieRepository.findById(2L).orElseThrow(() -> new MovieNotFoundException(2L));
            Movie m3 = movieRepository.findById(3L).orElseThrow(() -> new MovieNotFoundException(3L));
            Movie m4 = movieRepository.findById(4L).orElseThrow(() -> new MovieNotFoundException(4L));
            Movie m5 = movieRepository.findById(5L).orElseThrow(() -> new MovieNotFoundException(5L));
            Movie m6 = movieRepository.findById(6L).orElseThrow(() -> new MovieNotFoundException(6L));
            Movie m7 = movieRepository.findById(7L).orElseThrow(() -> new MovieNotFoundException(7L));
            Movie m8 = movieRepository.findById(8L).orElseThrow(() -> new MovieNotFoundException(8L));
            Movie m9 = movieRepository.findById(9L).orElseThrow(() -> new MovieNotFoundException(9L));
            Movie m10 = movieRepository.findById(10L).orElseThrow(() -> new MovieNotFoundException(10L));
            Movie m11 = movieRepository.findById(11L).orElseThrow(() -> new MovieNotFoundException(11L));

            Review r1 = new Review("An absolute visual treat. The cinematography is breathtaking, but the plot feels like it's treading water.",
                    3, LocalDate.of(2020,5,8), true, u1, m1);
            Review r2 = new Review("A gripping tale that keeps you on the edge of your seat, though the ending felt a bit rushed.",
                    4, LocalDate.of(2021,1,15), false, u2, m2);
            Review r3 = new Review("Fantastic performances from the cast, but the storyline is somewhat predictable.",
                    3, LocalDate.of(2020,8,23), true, u3, m3);
            Review r4 = new Review("An emotional rollercoaster with brilliant direction, but the pacing could have been better.",
                    4, LocalDate.of(2019,12,19), false, u4, m4);
            Review r5 = new Review("Visually stunning with an engaging narrative, but some characters feel underdeveloped.",
                    4, LocalDate.of(2020,3,30), true, u1, m5);
            Review r6 = new Review("A thought-provoking story that challenges conventions, but it may not appeal to everyone.",
                    3, LocalDate.of(2021,6,10), false, u2, m6);
            Review r7 = new Review("An inspiring and heartwarming movie, though it sometimes veers into cliché territory.",
                    3, LocalDate.of(2020,10,5), true, u3, m7);
            Review r8 = new Review("A masterful blend of humor and drama, but the runtime feels unnecessarily long.",
                    4, LocalDate.of(2021,3,17), false, u4, m8);
            Review r9 = new Review("A visually unique experience, but the complex plot might confuse some viewers.",
                    3, LocalDate.of(2020,11,25), true, u1, m9);
            Review r10 = new Review("A compelling story with a powerful message, though the dialogue is sometimes stilted.",
                    4, LocalDate.of(2021,7,22), false, u2, m10);
            Review r11 = new Review("A bold and daring film that pushes boundaries, but its experimental nature won't be for everyone.",
                    3, LocalDate.of(2020,2,14), true, u3, m11);
            Review r12 = new Review("A rollercoaster of emotions from start to finish.",
                    4, LocalDate.of(2024,8,1), true, u1, m2);
            Review r13 = new Review("I wanted to love this, but it felt like a missed opportunity. The concept was intriguing, but the execution left much to be desired.",
                    2, LocalDate.of(2000,7,4), false, u1, m3);
            Review r14 = new Review("This one took me by surprise. A slow burn that pays off in the end with a haunting finale.",
                    5, LocalDate.of(2022,2,27), true, u2, m1);

            for(Review r : reviewRepository.saveAll(List.of(r1,r2,r3,r4,r5,r6,r7,r8,r9,r10,r11,r12,r13,r14))) {
                log.info("Created new review: {}", r);
            }
        };
    }

    @Bean
    public CommandLineRunner initActors(ActorRepository actorRepository) {
        return args -> {
          log.info("Initializing actors...");

          Actor a1 = new Actor("Leonardo DiCaprio", LocalDate.of(1974, 11, 11), "United States");
          Actor a2 = new Actor("Meryl Streep", LocalDate.of(1949, 6, 22), "United States");
          Actor a3 = new Actor("Daniel Day-Lewis", LocalDate.of(1957, 4, 29), "United Kingdom");
          Actor a4 = new Actor("Penélope Cruz", LocalDate.of(1974, 4, 28), "Spain");
          Actor a5 = new Actor("Cate Blanchett", LocalDate.of(1969, 5, 14), "Australia");

          for(Actor a : actorRepository.saveAll(List.of(a1, a2, a3, a4, a5))) {
              log.info("Created actor: {}", a);
          }
        };
    }
}
