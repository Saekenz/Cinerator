package at.saekenz.cinerator.controller;

import at.saekenz.cinerator.model.movie.Movie;
import at.saekenz.cinerator.model.review.Review;
import at.saekenz.cinerator.model.user.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDate;
import java.util.Set;

import static org.hamcrest.Matchers.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class MovieControllerSpringBootIntegrationTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(webApplicationContext)
                .apply(springSecurity())
                .build();
    }

    @WithMockUser("test-user")
    @Test
    public void givenFindAllMoviesRequest_shouldSucceedWith200() throws Exception {
        mockMvc.perform(get("/movies").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.movieList").isNotEmpty());
    }

    @WithMockUser("test-user")
    @Test
    public void givenFindAllMoviesRequest_shouldFailWith404() throws Exception {
        mockMvc.perform(get("/movis").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @WithMockUser("test-user")
    @Test
    public void givenFindMovieByIdRequest_shouldSucceedWith200() throws Exception {
        Long movie_id = 1L;
        mockMvc.perform(get("/movies/{movie_id}", movie_id).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.movie_id").value(movie_id));
    }

    @WithMockUser("test-user")
    @Test
    public void givenFindMovieByIdRequest_shouldFailWith404() throws Exception {
        Long movie_id = -999L;
        mockMvc.perform(get("/movies/{movie_id}", movie_id).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString(String.format("movie: %s", movie_id))));
    }

    @WithMockUser("test-user")
    @Test
    public void givenFindMoviesByTitleRequest_shouldSucceedWith200() throws Exception {
        String title = "Sicario";
        mockMvc.perform(get("/movies/title/{title}", title).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.movieList[*].title", everyItem(containsStringIgnoringCase(title))));
    }

    @WithMockUser("test-user")
    @Test
    public void givenFindMoviesByTitleRequest_shouldFailWith404() throws Exception {
        String title = "Sicoria";
        mockMvc.perform(get("/movies/title/{title}", title).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString(String.format("title: %s", title))));
    }

    @WithMockUser("test-user")
    @Test
    public void givenFindMoviesByImdbId_shouldSucceedWith200() throws Exception {
        String imdbId = "tt1375666"; // title: Inception
        mockMvc.perform(get("/movies/imdb_id/{imdbId}", imdbId).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.imdb_id").value(imdbId));
    }

    @WithMockUser("test-user")
    @Test
    public void givenFindMoviesByImdbId_shouldFailWith404() throws Exception {
        String imdbId = "tt1375665"; // not a valid imdb_id
        mockMvc.perform(get("/movies/imdb_id/{imdbId}", imdbId).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString(String.format("imdb_id: %s", imdbId))));
    }

    @WithMockUser("test-user")
    @Test
    public void givenFindMoviesByDirectorRequest_shouldSucceedWith200() throws Exception {
        String director = "Christopher Nolan";
        mockMvc.perform(get("/movies/director/{director}", director).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.movieList[*].director", everyItem(equalToIgnoringCase(director))));
    }

    @WithMockUser("test-user")
    @Test
    public void givenFindMoviesByDirectorRequest_shouldFailWith404() throws Exception {
        String director = "Nolan North"; // not a director
        mockMvc.perform(get("/movies/director/{director}", director).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString(String.format("director: %s", director))));
    }

    @WithMockUser("test-user")
    @Test
    public void givenFindMoviesByCountryRequest_shouldSucceedWith200() throws Exception {
        String country = "France";
        mockMvc.perform(get("/movies/country/{country}", country).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.movieList[*].country", everyItem(equalToIgnoringCase(country))));
    }

    @WithMockUser("test-user")
    @Test
    public void givenFindMoviesByCountryRequest_shouldFailWith404() throws Exception {
        String country = "Wonderland";
        mockMvc.perform(get("/movies/country/{country}", country).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString(String.format("country: %s", country))));
    }

    @WithMockUser("test-user")
    @Test
    public void givenFindMoviesByGenreRequest_shouldSucceedWith200() throws Exception {
        String genre = "Science Fiction";
        mockMvc.perform(get("/movies/genre/{genre}", genre).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.movieList[*].genre", everyItem(equalToIgnoringCase(genre))));
    }

    @WithMockUser("test-user")
    @Test
    public void givenFindMoviesByGenreRequest_shouldFailWith404() throws Exception {
        String genre = "Ice Cream";
        mockMvc.perform(get("/movies/genre/{genre}", genre).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString(String.format("genre: %s", genre))))
                .andDo(print());
    }

    @WithMockUser("test-user")
    @Test
    public void givenFindMoviesByYearRequest_shouldSucceedWith200() throws Exception {
        int year = 1994;
        mockMvc.perform(get("/movies/year/{year}", year).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.movieList[*].release_date", everyItem(containsStringIgnoringCase(String.valueOf(year)))))
                .andDo(print());
    }

    @WithMockUser("test-user")
    @Test
    public void givenFindMoviesByYearRequest_shouldFailWith404() throws Exception {
        int year = 3199;
        mockMvc.perform(get("/movies/year/{year}", year).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString(String.format("year: %s", year))));
    }

    @WithMockUser("test-user")
    @Test
    public void givenCreateNewMovieRequest_shouldSucceedWith201() throws Exception {
        String json_data = """
                {
                  "title": "Nightcrawler",
                  "release_date": "2014-10-31",
                  "runtime": "118 min",
                  "director": "Dan Gilroy",
                  "genre": "Thriller",
                  "country": "United States",
                  "imdb_id": "tt287271",
                  "poster_url": "https://upload.wikimedia.org/wikipedia/en/d/d4/Nightcrawlerfilm.jpg",
                  "reviews": []
                }""";

    mockMvc.perform(post("/movies").contentType(MediaType.APPLICATION_JSON).content(json_data))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.title").value("Nightcrawler"))
            .andExpect(jsonPath("$.release_date").value("2014-10-31"))
            .andExpect(jsonPath("$.runtime").value("118 min"))
            .andExpect(jsonPath("$.director").value("Dan Gilroy"))
            .andExpect(jsonPath("$.genre").value("Thriller"))
            .andExpect(jsonPath("$.country").value("United States"))
            .andExpect(jsonPath("$.imdb_id").value("tt287271"))
            .andExpect(jsonPath("$.poster_url").value("https://upload.wikimedia.org/wikipedia/en/d/d4/Nightcrawlerfilm.jpg"))
            .andDo(print());
    }

    @WithMockUser("test-user")
    @Test
    public void givenUpdateMovieRequest_shouldSucceedWith201() throws Exception {
        Movie updatedMovie = new Movie("The Shawshank Redemption","Frank Darabont", LocalDate.of(1994, 9, 23),
                "142 min","Drama","United States","tt0111161",
                "https://upload.wikimedia.org/wikipedia/en/8/81/ShawshankRedemptionMoviePoster.jpg");

        ObjectMapper om = new ObjectMapper();
        om.findAndRegisterModules();
        String json_data = om.writeValueAsString(updatedMovie);

        Long movie_id = 3L;
        mockMvc.perform(put("/movies/{id}", movie_id).contentType(MediaType.APPLICATION_JSON).content(json_data))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("The Shawshank Redemption"))
                .andExpect(jsonPath("$.release_date").value("1994-09-23"))
                .andExpect(jsonPath("$.runtime").value("142 min"))
                .andExpect(jsonPath("$.director").value("Frank Darabont"))
                .andExpect(jsonPath("$.genre").value("Drama"))
                .andExpect(jsonPath("$.country").value("United States"))
                .andExpect(jsonPath("$.imdb_id").value("tt0111161"))
                .andExpect(jsonPath("$.poster_url")
                        .value("https://upload.wikimedia.org/wikipedia/en/8/81/ShawshankRedemptionMoviePoster.jpg"));
    }

    @WithMockUser("test-user")
    @Test
    public void givenDeleteMovieRequest_shouldSucceedWith204() throws Exception {
        Long movie_id = 3L;
        mockMvc.perform(delete("/movies/{movie_id}", movie_id).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @WithMockUser("test-user")
    @Test
    public void givenDeleteMovieRequest_shouldFailWith404() throws Exception {
        Long movie_id = 999L;
        mockMvc.perform(delete("/movies/{movie_id}", movie_id).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @WithMockUser("test-user")
    @Test
    public void givenFindReviewsByIdRequest_shouldSucceedWith200() throws Exception {
        Long movie_id = 3L;
        mockMvc.perform(get("/movies/{movie_id}/reviews", movie_id).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.reviewList").isNotEmpty());
    }

    @WithMockUser("test-user")
    @Test
    public void givenFindReviewsByIdRequest_shouldFailWith404() throws Exception {
        Long movie_id = 999L;
        mockMvc.perform(get("/movies/{movie_id}/reviews", movie_id).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    // TODO -> fix DataIntegrityViolation
//    @WithMockUser("test-user")
//    @Test
//    public void givenAddReviewToMovieRequest_shouldSucceedWith201() throws Exception {
//        Long movie_id = 1L;
//
//        User user = new User("UserA","password","USER",true, List.of());
//        user.setUser_id(3L);
//        Movie movie = new Movie("Sicario", "Denis Villeneuve", LocalDate.of(2015,10,1), "122 min",
//                "Thriller","United States","tt3397884","https://upload.wikimedia.org/wikipedia/en/4/4b/Sicario_poster.jpg");
//        movie.setMovie_id(movie_id);
//
//        Review review = new Review();
//        review.setComment("Test review comment");
//        review.setReview_date(LocalDate.now());
//        review.setIs_liked(false);
//        review.setRating(5);
//        review.setUser(user);
//        review.setMovie(movie);
//
//        ObjectMapper om = new ObjectMapper();
//        om.findAndRegisterModules();
//        String json_data = om.writeValueAsString(review);
//        System.out.println(json_data);
//
//        mockMvc.perform(post("/movies/{movie_id}/reviews", movie_id).contentType(MediaType.APPLICATION_JSON)
//                        .content(json_data))
//                .andExpect(status().isCreated())
//                .andExpect(jsonPath("$.comment").value(review.getComment()))
//                .andExpect(jsonPath("$.review_date").value(review.getReview_date()))
//                .andExpect(jsonPath("$.is_liked").value(review.isIs_liked()))
//                .andExpect(jsonPath("$.rating").value(review.getRating()))
//                .andDo(print());
//    }

    @WithMockUser("test-user")
    @Test
    public void givenAddReviewToMovieRequest_shouldFailWith404() throws Exception {
    Long movie_id = 999L;
    User user = new User("UserA","password","USER",true, Set.of());
    Movie movie = new Movie();
    Review review = new Review();
    review.setUser(user);
    review.setMovie(movie);

    ObjectMapper om = new ObjectMapper();
    om.findAndRegisterModules();
    String json_data = om.writeValueAsString(review);

    mockMvc.perform(post("/movies/{movie_id}/reviews", movie_id).contentType(MediaType.APPLICATION_JSON)
                    .content(json_data))
            .andExpect(status().isNotFound())
            .andDo(print());

    }

    @Test
    public void givenFindActorsByIdRequest_shouldSucceedWith200() throws Exception {
        Long movie_id = 1L;
        mockMvc.perform(get("/movies/{movie_id}/actors", movie_id).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.actorList").isNotEmpty());
    }

    @Test
    public void givenFindActorsByIdRequest_shouldFailWith404() throws Exception {
        Long movie_id = -999L;
        mockMvc.perform(get("/movies/{movie_id}/actors", movie_id).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString(String.format("Could not find movie: %s", movie_id))));
    }

    @Test
    public void givenFindActorByIdRequest_shouldSucceedWith200() throws Exception {
        Long movie_id = 2L;
        Long actor_id = 2L;

        mockMvc.perform(get("/movies/{movie_id}/actors/{actor_id}", movie_id, actor_id).
                contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.actor_id").value(actor_id));
    }

    @Test
    public void givenFindActorByIdRequest_shouldFailWith404() throws Exception {
        Long movie_id = -999L;
        Long actor_id = 2L;

        mockMvc.perform(get("/movies/{movie_id}/actors/{actor_id}", movie_id, actor_id).
                contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString(String.format("Could not find movie: %s", movie_id))));

        movie_id = 2L;
        actor_id = -999L;

        mockMvc.perform(get("/movies/{movie_id}/actors/{actor_id}", movie_id, actor_id).
                        contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString(String.format("actor with id: %s", actor_id))));
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void givenAddActorToMovieRequest_shouldSucceedWith200() throws Exception {
        Long movie_id = 13L;
        Long actor_id = 5L;

        mockMvc.perform(post("/movies/{movie_id}/actors", movie_id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(String.valueOf(actor_id)))
                .andExpect(status().isOk())
                .andExpect(jsonPath(String.format("$.actors[?(@.actor_id == %s)]",actor_id)).exists());
    }

    @Test
    public void givenAddActorToMovieRequest_shouldFailWith404() throws Exception {
        Long movie_id = -999L;
        Long actor_id = 5L;

        mockMvc.perform(post("/movies/{movie_id}/actors", movie_id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(String.valueOf(actor_id)))
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString(String.format("Could not find movie: %s", movie_id))));

        movie_id = 13L;
        actor_id = -999L;

        mockMvc.perform(post("/movies/{movie_id}/actors", movie_id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(String.valueOf(actor_id)))
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString(String.format("actor with id: %s", actor_id))));
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void givenDeleteActorFromMovieRequest_shouldSucceedWith200() throws Exception {
        Long movie_id = 2L;
        Long actor_id = 2L;

        mockMvc.perform(delete("/movies/{movie_id}/actors/{actor_id}", movie_id, actor_id)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath(String.format("$.actors[?(@.actor_id == %s)]", actor_id)).isEmpty());
    }

    @Test
    public void givenDeleteActorFromMovieRequest_shouldFailWith404() throws Exception {
        Long movie_id = -999L;
        Long actor_id = 2L;

        mockMvc.perform(delete("/movies/{movie_id}/actors/{actor_id}", movie_id, actor_id)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString(String.format("Could not find movie: %s", movie_id))));
    }
}
