package at.saekenz.cinerator.config;

import at.saekenz.cinerator.model.actor.Actor;
import at.saekenz.cinerator.model.actor.ActorNotFoundException;
import at.saekenz.cinerator.model.castinfo.CastInfo;
import at.saekenz.cinerator.model.role.Role;
import at.saekenz.cinerator.model.country.Country;
import at.saekenz.cinerator.model.follow.Follow;
import at.saekenz.cinerator.model.follow.FollowKey;
import at.saekenz.cinerator.model.genre.Genre;
import at.saekenz.cinerator.model.movie.MovieNotFoundException;
import at.saekenz.cinerator.model.person.Person;
import at.saekenz.cinerator.model.review.Review;
import at.saekenz.cinerator.model.movie.Movie;
import at.saekenz.cinerator.model.user.User;
import at.saekenz.cinerator.model.user.UserNotFoundException;
import at.saekenz.cinerator.model.userlist.UserList;
import at.saekenz.cinerator.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDate;
import java.util.*;

@Configuration
public class TestDataLoader {

    private static final Logger log = LoggerFactory.getLogger(TestDataLoader.class);

    @Bean
    @Order(1)
    public CommandLineRunner initGenresAndCountries(GenreRepository genreRepository,
                                                    CountryRepository countryRepository,
                                                    RoleRepository roleRepository) {
        return args -> {
            log.info("Initializing genres ...");

            List<Genre> genre = List.of(
                    new Genre("Action"),
                    new Genre("Adventure"),
                    new Genre("Animation"),
                    new Genre("Biography"),
                    new Genre("Comedy"),
                    new Genre("Crime"),
                    new Genre("Documentary"),
                    new Genre("Drama"),
                    new Genre("Family"),
                    new Genre("Fantasy"),
                    new Genre("Film-Noir"),
                    new Genre("Game-Show"),
                    new Genre("History"),
                    new Genre("Horror"),
                    new Genre("Music"),
                    new Genre("Musical"),
                    new Genre("Mystery"),
                    new Genre("News"),
                    new Genre("Reality-TV"),
                    new Genre("Romance"),
                    new Genre("Sci-Fi"),
                    new Genre("Sport"),
                    new Genre("Talk-Show"),
                    new Genre("Thriller"),
                    new Genre("War"),
                    new Genre("Western")
            );

            // Batch insert genres
            genreRepository.saveAll(genre);

            log.info("Initializing genres ...");

            List<Country> countries = List.of(
                    new Country("United States"),
                    new Country("France"),
                    new Country("Mexico"),
                    new Country("Hong Kong"),
                    new Country("Canada"),
                    new Country("Germany"),
                    new Country("United Kingdom"),
                    new Country("Ireland"),
                    new Country("Japan"),
                    new Country("South Korea"),
                    new Country("Austria"),
                    new Country("Australia"),
                    new Country("Spain"),
                    new Country("Poland")
            );

            // Batch insert countries
            countryRepository.saveAll(countries);

            log.info("Initializing roles ...");

            List<Role> roles = List.of(
                    new Role("Actor"),
                    new Role("Director"),
                    new Role("Producer"),
                    new Role("Writer"),
                    new Role("Cinematographer"),
                    new Role("Editor"),
                    new Role("Casting"),
                    new Role("Special Effects"),
                    new Role("Lighting"),
                    new Role("Stunts")
            );

            // Batch insert roles
            roleRepository.saveAll(roles);
        };
    }

    // TODO -> replace actors with persons
    @Bean
    @Order(2)
    public CommandLineRunner initActorsAndPersons(ActorRepository actorRepository,
                                                  PersonRepository personRepository,
                                                  CountryRepository countryRepository) {
        return args -> {
            log.info("Initializing actors...");

            List<Actor> actors = List.of(
                    new Actor("Leonardo DiCaprio", LocalDate.of(1974, 11, 11), "United States"),
                    new Actor("Meryl Streep", LocalDate.of(1949, 6, 22), "United States"),
                    new Actor("Daniel Day-Lewis", LocalDate.of(1957, 4, 29), "United Kingdom"),
                    new Actor("Penélope Cruz", LocalDate.of(1974, 4, 28), "Spain"),
                    new Actor("Cate Blanchett", LocalDate.of(1969, 5, 14), "Australia")
            );

            // Batch insert & log creation for each actor
            actorRepository.saveAll(actors).forEach(actor -> log.info("Created actor: {}", actor));

            log.info("Initializing persons...");

            List<Country> countries = countryRepository.findAll();

            List<Person> persons = List.of(
                    new Person("Leonardo DiCaprio", LocalDate.of(1974, 11, 11),
                            null, countries.get(0)),
                    new Person("Meryl Streep", LocalDate.of(1949, 6, 22),
                            null, countries.get(0)),
                    new Person("Daniel Day-Lewis", LocalDate.of(1957, 4, 29),
                            null, countries.get(6)),
                    new Person("Penélope Cruz", LocalDate.of(1974, 4, 28),
                            null, countries.get(12)),
                    new Person("Cate Blanchett", LocalDate.of(1969, 5, 14),
                            null, countries.get(11)),
                    new Person("Denis Villeneuve", LocalDate.of(1967, 10, 3),
                            null, countries.get(4)),
                    new Person("Gus Van Sant", LocalDate.of(1952, 7, 24),
                            null, countries.get(0)),
                    new Person("Krzysztof Kieslowski", LocalDate.of(1941, 6, 27),
                            LocalDate.of(1996, 3, 13), countries.get(13)),
                    new Person("Christopher Nolan", LocalDate.of(1970, 7, 30),
                            null, countries.get(6)),
                    new Person("Bong Joon Ho", LocalDate.of(1969, 9, 14),
                            null, countries.get(9)),
                    new Person("Wes Anderson", LocalDate.of(1969, 5, 1),
                            null, countries.get(0)),
                    new Person("Hayao Miyazaki", LocalDate.of(1941, 1, 5),
                            null, countries.get(8)),
                    new Person("Francis Ford Coppola", LocalDate.of(1939, 4, 7),
                            null, countries.get(0)),
                    new Person("Jean-Pierre Jeunet", LocalDate.of(1953, 9, 3),
                            null, countries.get(1)),
                    new Person("Quentin Tarantino", LocalDate.of(1963, 3, 27),
                            null, countries.get(0)),
                    new Person("Damien Chazelle", LocalDate.of(1985, 1, 19),
                            null, countries.get(0)),
                    new Person("Dan Gilroy", LocalDate.of(1959, 6, 24),
                            null, countries.get(0)),
                    new Person("Jake Gyllenhaal", LocalDate.of(1980,12,19),
                            null, countries.get(0))
            );

            personRepository.saveAll(persons);
        };
    }

    @Bean
    @Order(3)
    public CommandLineRunner initMovies(MovieRepository movieRepository, ActorRepository actorRepository,
                                        GenreRepository genreRepository, CountryRepository countryRepository) {
        return (args) -> {
            log.info("Initializing movies...");

            List<Actor> actors = actorRepository.findAllById(List.of(1L, 2L, 3L, 4L, 5L));
            List<Genre> genres = genreRepository.findAll();
            List<Country> countries = countryRepository.findAll();

            if (actors.size() < 5) { throw new ActorNotFoundException(); }

            List<Movie> movies = List.of(
                    createMovie("Sicario", "Denis Villeneuve", LocalDate.of(2015,10,1), "122 min",
                        "tt3397884","https://upload.wikimedia.org/wikipedia/en/4/4b/Sicario_poster.jpg",
                        List.of(actors.get(0), actors.get(1), actors.get(2), actors.get(3))),
                    createMovie("Dune: Part Two", "Denis Villeneuve", LocalDate.of(2024,3,1), "167 min",
                            "tt15239678","https://upload.wikimedia.org/wikipedia/en/5/52/Dune_Part_Two_poster.jpeg",
                            List.of(actors.get(1), actors.get(2), actors.get(4))),
                    createMovie("Good Will Hunting", "Gus Van Sant", LocalDate.of(1998,9,1), "127 min",
                            "tt0119217","https://upload.wikimedia.org/wikipedia/en/5/52/Good_Will_Hunting.png",
                            List.of()),
                    createMovie("Three Colors: Red", "Krzysztof Kieslowski", LocalDate.of(1994,11,23), "100 min",
                            "tt0111495","https://upload.wikimedia.org/wikipedia/en/0/0a/Three_Colors-Red.jpg",
                            List.of()),
                    createMovie("Inception", "Christopher Nolan", LocalDate.of(2010,7,16), "148 min",
                            "tt1375666","https://upload.wikimedia.org/wikipedia/en/2/2e/Inception_%282010%29_theatrical_poster.jpg",
                            List.of()),
                    createMovie("Parasite", "Bong Joon Ho", LocalDate.of(2019,5,30), "133 min",
                            "tt6751668","https://upload.wikimedia.org/wikipedia/en/5/53/Parasite_%282019_film%29.png",
                            List.of()),
                    createMovie("The Grand Budapest Hotel", "Wes Anderson", LocalDate.of(2014,3,28), "100 min",
                            "tt2278388","https://upload.wikimedia.org/wikipedia/en/1/1c/The_Grand_Budapest_Hotel.png",
                            List.of()),
                    createMovie("Spirited Away", "Hayao Miyazaki", LocalDate.of(2001,7,20), "125 min",
                            "tt0245429","https://upload.wikimedia.org/wikipedia/en/d/db/Spirited_Away_Japanese_poster.png",
                            List.of()),
                    createMovie("The Godfather", "Francis Ford Coppola", LocalDate.of(1972,3,24), "175 min",
                            "tt0068646","https://upload.wikimedia.org/wikipedia/en/1/1c/Godfather_ver1.jpg",
                            List.of()),
                    createMovie("Amélie", "Jean-Pierre Jeunet", LocalDate.of(2001,4,25), "122 min",
                            "tt0211915","https://upload.wikimedia.org/wikipedia/en/5/53/Amelie_poster.jpg",
                            List.of()),
                    createMovie("Pulp Fiction", "Quentin Tarantino", LocalDate.of(1994,10,14),"154 min",
                            "tt0110912","https://upload.wikimedia.org/wikipedia/en/3/3b/Pulp_Fiction_%281994%29_poster.jpg",
                            List.of()),
                    createMovie("The Dark Knight", "Christopher Nolan", LocalDate.of(2008,7,18),"152 min",
                            "tt0468569","https://upload.wikimedia.org/wikipedia/en/1/1c/The_Dark_Knight_%282008_film%29.jpg",
                            List.of()),
                    createMovie("La La Land", "Damien Chazelle", LocalDate.of(2016,12,9),"129 min",
                            "tt3783958","https://upload.wikimedia.org/wikipedia/en/a/ab/La_La_Land_%28film%29.png",
                            List.of())
            );

//            Movie m14 = new Movie("Nightcrawler","Dan Gilroy", LocalDate.of(2014,10,31),"Thriller", "118 min",
//                    "United States","tt287271","https://upload.wikimedia.org/wikipedia/en/d/d4/Nightcrawlerfilm.jpg");

            movies.get(0).setGenres(Set.copyOf(genres.subList(0,2)));
            movies.get(1).setGenres(Set.copyOf(genres.subList(0,2)));
            movies.get(2).setGenres(Set.copyOf(genres.subList(3,5)));
            movies.get(3).setGenres(Set.copyOf(genres.subList(6,8)));
            movies.get(4).setGenres(Set.copyOf(genres.subList(7,10)));

            movies.get(0).setCountries(Set.of(countries.get(0), countries.get(2), countries.get(3)));
            movies.get(1).setCountries(Set.of(countries.get(0), countries.get(4)));
            movies.get(2).setCountries(Set.of(countries.get(0)));
            movies.get(3).setCountries(Set.of(countries.get(1)));
            movies.get(4).setCountries(Set.of(countries.get(0), countries.get(6)));
            movies.get(5).setCountries(Set.of(countries.get(9)));
            movies.get(6).setCountries(Set.of(countries.get(0), countries.get(5)));
            movies.get(7).setCountries(Set.of(countries.get(8)));
            movies.get(8).setCountries(Set.of(countries.get(0)));
            movies.get(9).setCountries(Set.of(countries.get(1)));
            movies.get(10).setCountries(Set.of(countries.get(0)));
            movies.get(11).setCountries(Set.of(countries.get(0), countries.get(6)));
            movies.get(12).setCountries(Set.of(countries.get(0), countries.get(3)));

            movieRepository.saveAll(movies).forEach(movie -> log.info("Created new movie: {}", movie));
        };
    }

    @Bean
    @Order(4)
    public CommandLineRunner initUsers(UserRepository userRepository, MovieRepository movieRepository) {
        return (args) -> {
            log.info("Initializing users...");

            BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
            String encodedPassword = passwordEncoder.encode("password");

            List<Long> ids = List.of(5L, 13L, 1L, 7L, 2L, 3L, 6L);
            List<Movie> movies = movieRepository.findAllById(ids);

            if (movies.size() < 7) { throw new MovieNotFoundException(); }

            Map<Long, Integer> idOrderMap = new HashMap<>();
            for (int i = 0; i < ids.size(); i++) {
                idOrderMap.put(ids.get(i), i);
            }

            movies.sort(Comparator.comparing(movie -> idOrderMap.get(movie.getId())));

            List<User> users = List.of(
                    new User("UserA", "Peter Klein", encodedPassword, "peter.klein@example.com","", "USER", false,
                    Set.of(movies.get(0), movies.get(1), movies.get(2))),
                    new User("UserB", "Susan McDonald", encodedPassword, "susan.mcdonald@example.com", "","USER", true,
                    Set.of(movies.get(3), movies.get(4), movies.get(5))),
                    new User("UserC", "Jane Smith", encodedPassword, "jane.smith@example.com", "","ADMIN", true,
                    Set.of(movies.get(6), movies.get(0), movies.get(4))),
                    new User("UserD", "Mark Taylor", encodedPassword, "mark.taylor@example.com", "","USER", false,
                    Set.of(movies.get(4), movies.get(3), movies.get(6)))
            );

            userRepository.saveAll(users).forEach(user -> log.info("Created new user: {}", user));
        };
    }

    @Bean
    @Order(5)
    public CommandLineRunner initReviewsAndUserLists(ReviewRepository reviewRepository, UserRepository userRepository,
                                                     MovieRepository movieRepository, UserListRepository userListRepository) {
        return (args) -> {
            log.info("Initializing reviews...");

            List<User> users = userRepository.findAllById(List.of(1L, 2L, 3L, 4L));

            if (users.size() < 4) { throw new UserNotFoundException(); }

            List<Movie> movies = movieRepository.findAllById(List.of(1L, 2L, 3L, 4L, 5L, 6L, 7L, 8L, 9L, 10L, 11L));

            if (movies.size() < 11) { throw new MovieNotFoundException(); }

            Set<Movie> moviesInUser4List = Set.copyOf(movies.subList(0, 5));
            Set<Movie> moviesInUser2List = Set.copyOf(movies.subList(4, 9));
            UserList user4List = new UserList("Good movies", "Some absolute bangers", false, users.get(3), moviesInUser4List);
            UserList user4List2 = new UserList("Funny movies", "Bangers that always make me smile!", false, users.get(3), moviesInUser2List);
            UserList user2List = new UserList("My Top movies so far",
                    "Let's see how manic 2024 can be with an expected high volume of viewing pleasures in store for the senses.", false, users.get(1), moviesInUser2List);
            userListRepository.saveAll(List.of(user4List, user2List, user4List2));

            List<Review> reviews = List.of(
                    new Review("An absolute visual treat. The cinematography is breathtaking, but the plot feels like it's treading water.",
                            3, LocalDate.of(2020,5,8), true, users.get(0), movies.get(0)),
                    new Review("A gripping tale that keeps you on the edge of your seat, though the ending felt a bit rushed.",
                            4, LocalDate.of(2021,1,15), false, users.get(1), movies.get(1)),
                    new Review("Fantastic performances from the cast, but the storyline is somewhat predictable.",
                            3, LocalDate.of(2020,8,23), true, users.get(2), movies.get(2)),
                    new Review("An emotional rollercoaster with brilliant direction, but the pacing could have been better.",
                            4, LocalDate.of(2019,12,19), false, users.get(3), movies.get(3)),
                    new Review("Visually stunning with an engaging narrative, but some characters feel underdeveloped.",
                            4, LocalDate.of(2020,3,30), true, users.get(0), movies.get(4)),
                    new Review("A thought-provoking story that challenges conventions, but it may not appeal to everyone.",
                            3, LocalDate.of(2021,6,10), false, users.get(1), movies.get(5)),
                    new Review("An inspiring and heartwarming movie, though it sometimes veers into cliché territory.",
                            3, LocalDate.of(2020,10,5), true, users.get(2), movies.get(6)),
                    new Review("A masterful blend of humor and drama, but the runtime feels unnecessarily long.",
                            4, LocalDate.of(2021,3,17), false, users.get(3), movies.get(7)),
                    new Review("A visually unique experience, but the complex plot might confuse some viewers.",
                            3, LocalDate.of(2020,11,25), true, users.get(0), movies.get(8)),
                    new Review("A compelling story with a powerful message, though the dialogue is sometimes stilted.",
                            4, LocalDate.of(2021,7,22), false, users.get(1), movies.get(9)),
                    new Review("A bold and daring film that pushes boundaries, but its experimental nature won't be for everyone.",
                            3, LocalDate.of(2020,2,14), true, users.get(2), movies.get(10)),
                    new Review("A rollercoaster of emotions from start to finish.",
                            4, LocalDate.of(2024,8,1), true, users.get(0), movies.get(1)),
                    new Review("I wanted to love this, but it felt like a missed opportunity. The concept was intriguing, but the execution left much to be desired.",
                            2, LocalDate.of(2000,7,4), false, users.get(0), movies.get(2)),
                    new Review("This one took me by surprise. A slow burn that pays off in the end with a haunting finale.",
                            5, LocalDate.of(2022,2,27), true, users.get(1), movies.get(0))
            );

            // Batch insert all created reviews
            reviewRepository.saveAll(reviews).forEach(review -> log.info("Created new review: {}", review));

        };
    }

    @Bean
    @Order(6)
    public CommandLineRunner initFollowers(FollowRepository followRepository, UserRepository userRepository) {
        return args -> {
            log.info("Initializing followers...");

            List<User> users = userRepository.findAllById(List.of(1L, 2L, 3L, 4L));

            if (users.size() < 4) { throw new UserNotFoundException(); }

            Follow f1 = new Follow(new FollowKey(users.get(0).getId(), users.get(1).getId()), users.get(0), users.get(1));
            Follow f2 = new Follow(new FollowKey(users.get(1).getId(),users.get(0).getId()), users.get(1), users.get(0));
            Follow f3 = new Follow(new FollowKey(users.get(1).getId(),users.get(2).getId()), users.get(1), users.get(2));
            Follow f4 = new Follow(new FollowKey(users.get(1).getId(),users.get(3).getId()), users.get(1), users.get(3));

            followRepository.saveAll(List.of(f1, f2, f3, f4)).forEach(follow -> log.info("Created follow: {}", follow));
        };
    }

    @Bean
    @Order(7)
    public CommandLineRunner initCastInfo(MovieRepository movieRepository, PersonRepository personRepository,
                                          RoleRepository roleRepository, CastInfoRepository castInfoRepository) {
        return args -> {
            log.info("Initializing cast/crew...");

            List<Movie> movies = movieRepository.findAll();
            List<Person> persons = personRepository.findAll();
            List<Role> roles = roleRepository.findAll();

            List<CastInfo> castInfos = List.of(
                    new CastInfo(movies.get(4), persons.get(0), roles.get(0), "Cobb"),
                    new CastInfo(movies.get(0), persons.get(5), roles.get(1)),
                    new CastInfo(movies.get(1), persons.get(5), roles.get(1)),
                    new CastInfo(movies.get(2), persons.get(6), roles.get(1)),
                    new CastInfo(movies.get(3), persons.get(7), roles.get(1)),
                    new CastInfo(movies.get(4), persons.get(8), roles.get(1)),
                    new CastInfo(movies.get(5), persons.get(9), roles.get(1)),
                    new CastInfo(movies.get(6), persons.get(10), roles.get(1)),
                    new CastInfo(movies.get(7), persons.get(11), roles.get(1)),
                    new CastInfo(movies.get(8), persons.get(12), roles.get(1)),
                    new CastInfo(movies.get(9), persons.get(13), roles.get(1)),
                    new CastInfo(movies.get(10), persons.get(14), roles.get(1)),
                    new CastInfo(movies.get(11), persons.get(8), roles.get(1)),
                    new CastInfo(movies.get(12), persons.get(15), roles.get(1))
            );

            // Batch insert all cast/crew infos
            castInfoRepository.saveAll(castInfos);
        };
    }

    private Movie createMovie(String title, String director, LocalDate releaseDate, String duration,
                              String imdbId, String posterUrl, List<Actor> actors) {
        Movie movie = new Movie(title, director, releaseDate, duration, imdbId, posterUrl);
        movie.setActors(actors);
        return movie;
    }
}
