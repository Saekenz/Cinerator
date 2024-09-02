package at.saekenz.cinerator.controller;

import at.saekenz.cinerator.model.movie.Movie;
import at.saekenz.cinerator.model.review.Review;
import at.saekenz.cinerator.model.review.ReviewDTO;
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
    public void givenFindMovieByIdRequest_shouldSucceedWith200() throws Exception {
        Long movieId = 1L;
        mockMvc.perform(get("/movies/{movieId}", movieId).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(movieId));
    }

    @WithMockUser("test-user")
    @Test
    public void givenFindMovieByIdRequest_shouldFailWith404() throws Exception {
        Long movieId = -999L;
        mockMvc.perform(get("/movies/{movieId}", movieId).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString(String.format("movie: %s", movieId))));
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
        mockMvc.perform(get("/movies/imdbId/{imdbId}", imdbId).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.imdbId").value(imdbId));
    }

    @WithMockUser("test-user")
    @Test
    public void givenFindMoviesByImdbId_shouldFailWith404() throws Exception {
        String imdbId = "tt1375665"; // not a valid imdbId
        mockMvc.perform(get("/movies/imdbId/{imdbId}", imdbId).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString(String.format("imdbId: %s", imdbId))));
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
                .andExpect(jsonPath("$._embedded.movieList[*].releaseDate", everyItem(containsStringIgnoringCase(String.valueOf(year)))))
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
                  "releaseDate": "2014-10-31",
                  "runtime": "118 min",
                  "director": "Dan Gilroy",
                  "genre": "Thriller",
                  "country": "United States",
                  "imdbId": "tt287271",
                  "posterUrl": "https://upload.wikimedia.org/wikipedia/en/d/d4/Nightcrawlerfilm.jpg",
                  "reviews": []
                }""";

    mockMvc.perform(post("/movies").contentType(MediaType.APPLICATION_JSON).content(json_data))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.title").value("Nightcrawler"))
            .andExpect(jsonPath("$.releaseDate").value("2014-10-31"))
            .andExpect(jsonPath("$.runtime").value("118 min"))
            .andExpect(jsonPath("$.director").value("Dan Gilroy"))
            .andExpect(jsonPath("$.genre").value("Thriller"))
            .andExpect(jsonPath("$.country").value("United States"))
            .andExpect(jsonPath("$.imdbId").value("tt287271"))
            .andExpect(jsonPath("$.posterUrl").value("https://upload.wikimedia.org/wikipedia/en/d/d4/Nightcrawlerfilm.jpg"))
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

        Long movieId = 3L;
        mockMvc.perform(put("/movies/{id}", movieId).contentType(MediaType.APPLICATION_JSON).content(json_data))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("The Shawshank Redemption"))
                .andExpect(jsonPath("$.releaseDate").value("1994-09-23"))
                .andExpect(jsonPath("$.runtime").value("142 min"))
                .andExpect(jsonPath("$.director").value("Frank Darabont"))
                .andExpect(jsonPath("$.genre").value("Drama"))
                .andExpect(jsonPath("$.country").value("United States"))
                .andExpect(jsonPath("$.imdbId").value("tt0111161"))
                .andExpect(jsonPath("$.posterUrl")
                        .value("https://upload.wikimedia.org/wikipedia/en/8/81/ShawshankRedemptionMoviePoster.jpg"));
    }

    @WithMockUser("test-user")
    @Test
    public void givenDeleteMovieRequest_shouldSucceedWith204() throws Exception {
        Long movieId = 3L;
        mockMvc.perform(delete("/movies/{movieId}", movieId).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @WithMockUser("test-user")
    @Test
    public void givenDeleteMovieRequest_shouldFailWith404() throws Exception {
        Long movieId = 999L;
        mockMvc.perform(delete("/movies/{movieId}", movieId).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @WithMockUser("test-user")
    @Test
    public void givenFindReviewsByIdRequest_shouldSucceedWith200() throws Exception {
        Long movieId = 3L;
        mockMvc.perform(get("/movies/{movieId}/reviews", movieId).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.reviewList").isNotEmpty());
    }

    @WithMockUser("test-user")
    @Test
    public void givenFindReviewsByIdRequest_shouldFailWith404() throws Exception {
        Long movieId = 999L;
        mockMvc.perform(get("/movies/{movieId}/reviews", movieId).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    /**
     * Attempts to create a new {@link Review} and add it to the {@link Movie} specified by movieId.
     * The request made in the method has to return HTTP code 201.
     * @throws Exception
     */
    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void givenAddReviewToMovieRequest_shouldSucceedWith201() throws Exception {
        Long movieId = 1L;
        ReviewDTO reviewDTO = new ReviewDTO("Test create and insert review with reviewDTO.",
                2, LocalDate.of(2024,9,1), true, 3L);

        ObjectMapper om = new ObjectMapper().findAndRegisterModules();
        String reviewJsonData = om.writeValueAsString(reviewDTO);

        mockMvc.perform(post("/movies/{movieId}/reviews", movieId).contentType(MediaType.APPLICATION_JSON)
                        .content(reviewJsonData))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.comment").value(reviewDTO.getComment()))
                .andExpect(jsonPath("$.reviewDate").value(reviewDTO.getReviewDate().toString()))
                .andExpect(jsonPath("$.liked").value(reviewDTO.isLiked()))
                .andExpect(jsonPath("$.rating").value(reviewDTO.getRating()))
                .andExpect(jsonPath("$.userId").value(reviewDTO.getUserId()))
                .andExpect(jsonPath("$.movieId").value(movieId));
    }

    /**
     * Performs request for creating and adding a review to a movie twice.
     * The first time the request contains an invalid movieId and a valid userId.
     * The second time the request contains a valid movieId and an invalid userId.
     * Both requests have to return HTTP code 404.
     * @throws Exception
     */
    @Test
    public void givenAddReviewToMovieRequest_shouldFailWith404() throws Exception {
        Long movieId = -999L;
        ReviewDTO reviewDTO = new ReviewDTO("Test create and insert review with reviewDTO.",
                2, LocalDate.of(2024,9,1), true, 3L);

        ObjectMapper om = new ObjectMapper().findAndRegisterModules();
        String reviewJsonData = om.writeValueAsString(reviewDTO);

        mockMvc.perform(post("/movies/{movieId}/reviews", movieId).contentType(MediaType.APPLICATION_JSON)
                        .content(reviewJsonData))
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString(String.format("Could not find movie: %s", movieId))));

        movieId = 4L;
        reviewDTO.setUserId(-999L);
        reviewJsonData = om.writeValueAsString(reviewDTO);
        mockMvc.perform(post("/movies/{movieId}/reviews", movieId).contentType(MediaType.APPLICATION_JSON)
                        .content(reviewJsonData))
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString(String.format("Could not find user: %s", reviewDTO.getUserId()))));
    }

    @Test
    public void givenFindActorsByIdRequest_shouldSucceedWith200() throws Exception {
        Long movieId = 1L;
        mockMvc.perform(get("/movies/{movieId}/actors", movieId).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.actorList").isNotEmpty());
    }

    @Test
    public void givenFindActorsByIdRequest_shouldFailWith404() throws Exception {
        Long movieId = -999L;
        mockMvc.perform(get("/movies/{movieId}/actors", movieId).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString(String.format("Could not find movie: %s", movieId))));
    }

    @Test
    public void givenFindActorByIdRequest_shouldSucceedWith200() throws Exception {
        Long movieId = 2L;
        Long actorId = 2L;

        mockMvc.perform(get("/movies/{movieId}/actors/{actorId}", movieId, actorId).
                contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(actorId));
    }

    @Test
    public void givenFindActorByIdRequest_shouldFailWith404() throws Exception {
        Long movieId = -999L;
        Long actorId = 2L;

        mockMvc.perform(get("/movies/{movieId}/actors/{actorId}", movieId, actorId).
                contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString(String.format("Could not find movie: %s", movieId))));

        movieId = 2L;
        actorId = -999L;

        mockMvc.perform(get("/movies/{movieId}/actors/{actorId}", movieId, actorId).
                        contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString(String.format("actor with id: %s", actorId))));
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void givenAddActorToMovieRequest_shouldSucceedWith200() throws Exception {
        Long movieId = 13L;
        Long actorId = 5L;

        mockMvc.perform(post("/movies/{movieId}/actors", movieId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(String.valueOf(actorId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath(String.format("$.actors[?(@.id == %s)]",actorId)).exists());
    }

    @Test
    public void givenAddActorToMovieRequest_shouldFailWith404() throws Exception {
        Long movieId = -999L;
        Long actorId = 5L;

        mockMvc.perform(post("/movies/{movieId}/actors", movieId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(String.valueOf(actorId)))
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString(String.format("Could not find movie: %s", movieId))));

        movieId = 13L;
        actorId = -999L;

        mockMvc.perform(post("/movies/{movieId}/actors", movieId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(String.valueOf(actorId)))
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString(String.format("actor with id: %s", actorId))));
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void givenDeleteActorFromMovieRequest_shouldSucceedWith200() throws Exception {
        Long movieId = 2L;
        Long actorId = 2L;

        mockMvc.perform(delete("/movies/{movieId}/actors/{actorId}", movieId, actorId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath(String.format("$.actors[?(@.id == %s)]", actorId)).isEmpty());
    }

    @Test
    public void givenDeleteActorFromMovieRequest_shouldFailWith404() throws Exception {
        Long movieId = -999L;
        Long actorId = 2L;

        mockMvc.perform(delete("/movies/{movieId}/actors/{actorId}", movieId, actorId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString(String.format("Could not find movie: %s", movieId))));
    }

    @Test
    public void givenFindAllMoviesPaged_shouldSucceedWith200() throws Exception {
        int page = 3;
        int size = 3;
        String sortBy = "title";
        String sortDirection = "desc";

        mockMvc.perform(get("/movies/all?page={page}&size={size}&sortBy={sortBy}&sortDirection={sortDirection}",
                        page, size, sortBy, sortDirection)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.movieList").isNotEmpty())
                .andExpect(jsonPath("$._links").isNotEmpty())
                .andExpect(jsonPath("$.page.size").value(size))
                .andExpect(jsonPath("$.page.number").value(page));
    }
}
