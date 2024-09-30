package at.saekenz.cinerator.controller;

import at.saekenz.cinerator.model.country.Country;
import at.saekenz.cinerator.model.genre.Genre;
import at.saekenz.cinerator.model.movie.Movie;
import at.saekenz.cinerator.model.movie.MovieCreationDTO;
import at.saekenz.cinerator.model.review.Review;
import at.saekenz.cinerator.model.review.ReviewDTO;
import at.saekenz.cinerator.model.review.ReviewUpdateDTO;
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
    public void givenFindFindAllMoviesRequest_shouldSucceedWith200() throws Exception {
        mockMvc.perform(get("/movies").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.movieDTOList").isNotEmpty());
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
        Long movieId = 999L;
        mockMvc.perform(get("/movies/{movieId}", movieId).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Not Found"))
                .andExpect(jsonPath("$.status").value("404"))
                .andExpect(content().string(containsString(
                        String.format("Movie with id %s could not be found!", movieId))));
    }

    @WithMockUser("test-user")
    @Test
    public void givenFindMoviesByTitleRequest_shouldSucceedWith200() throws Exception {
        String title = "Sicario";
        mockMvc.perform(get("/movies/title/{title}", title).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.movieDTOList[*].title", everyItem(containsStringIgnoringCase(title))));
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
    public void givenFindMoviesByCountryRequest_shouldSucceedWith200() throws Exception {
        String country = "France";
        mockMvc.perform(get("/movies/country/{country}", country).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.movieDTOList[*].country", everyItem(containsStringIgnoringCase(country))));
    }

    @Test
    public void givenFindMoviesByCountryRequest_shouldSucceedWith200AndReturnEmptyList() throws Exception {
        String country = "Wonderland";
        mockMvc.perform(get("/movies/country/{country}", country).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(is("{}")));
    }

    @WithMockUser("test-user")
    @Test
    public void givenFindMoviesByGenreRequest_shouldSucceedWith200AndReturnListOfMovies() throws Exception {
        String genre = "Drama";
        mockMvc.perform(get("/movies/genre/{genre}", genre).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.movieDTOList[*].genre", everyItem(containsStringIgnoringCase(genre))));
    }

    @WithMockUser("test-user")
    @Test
    public void givenFindMoviesByGenreRequest_shouldSucceedWith200AndReturnEmptyList()throws Exception {
        String genre = "Ice Cream";
        mockMvc.perform(get("/movies/genre/{genre}", genre).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(is("{}")));
    }

    @WithMockUser("test-user")
    @Test
    public void givenFindMoviesByYearRequest_shouldSucceedWith200() throws Exception {
        int year = 1994;
        mockMvc.perform(get("/movies/year/{year}", year).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.movieDTOList[*].releaseDate", everyItem(containsStringIgnoringCase(String.valueOf(year)))))
                .andDo(print());
    }

    @WithMockUser("test-user")
    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void givenCreateNewMovieRequest_shouldSucceedWith201() throws Exception {
        MovieCreationDTO movie = new MovieCreationDTO("Nightcrawler", LocalDate.of(2014,10,31),
                "118 min", "tt287271",
                "https://upload.wikimedia.org/wikipedia/en/d/d4/Nightcrawlerfilm.jpg",
                Set.of(6L,8L,24L), Set.of(1L));

        ObjectMapper om = new ObjectMapper().findAndRegisterModules();
        String movieJsonData = om.writeValueAsString(movie);

        mockMvc.perform(post("/movies").contentType(MediaType.APPLICATION_JSON)
                        .content(movieJsonData)
                        .characterEncoding("utf-8"))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.title").value("Nightcrawler"))
            .andExpect(jsonPath("$.releaseDate").value("2014-10-31"))
            .andExpect(jsonPath("$.runtime").value("118 min"))
            .andExpect(jsonPath("$.genre", containsStringIgnoringCase("Thriller")))
            .andExpect(jsonPath("$.country", containsString("United States")))
            .andExpect(jsonPath("$.imdbId").value("tt287271"))
            .andExpect(jsonPath("$.posterUrl").value("https://upload.wikimedia.org/wikipedia/en/d/d4/Nightcrawlerfilm.jpg"))
            .andDo(print());
    }

    /**
     * Creates a request for adding a new {@link Movie} that is missing the 'director' property.
     * The request has to return HTTP code 400 since this property must not be null.
     * @throws Exception if any errors occur the execution of the test.
     */
    @Test
    public void givenCreateNewMovieRequest_shouldFailWith400() throws Exception {
        String movieJsonData = """
                {
                  "title": "Nightcrawler",
                  "releaseDate": "2014-10-31",
                  "genre": "Thriller",
                  "country": "United States",
                  "imdbId": "tt287271",
                  "posterUrl": "https://upload.wikimedia.org/wikipedia/en/d/d4/Nightcrawlerfilm.jpg",
                  "reviews": []
                }""";
        mockMvc.perform(post("/movies").contentType(MediaType.APPLICATION_JSON).content(movieJsonData))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Bad Request"))
                .andExpect(jsonPath("$.status").value("400"))
                .andExpect(content().string(containsString("Invalid request content.")));
    }

    /**
     * Creates a request to update a {@link Movie}.
     * The {@link Movie} will be updated since it is already stored in the database.
     * The request has to return HTTP code 204.
     * @throws Exception if any errors occur the execution of the test.
     */
    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void givenUpdateMovieRequest_shouldSucceedWith204() throws Exception {
        MovieCreationDTO updatedMovie = new MovieCreationDTO("The Shawshank Redemption",
                LocalDate.of(1994, 9, 23), "142 min", "tt0111161",
                "https://upload.wikimedia.org/wikipedia/en/8/81/ShawshankRedemptionMoviePoster.jpg",
                Set.of(6L, 8L, 24L), Set.of(1L));

        ObjectMapper om = new ObjectMapper();
        om.findAndRegisterModules();
        String json_data = om.writeValueAsString(updatedMovie);

        Long movieId = 3L;
        mockMvc.perform(put("/movies/{id}", movieId).contentType(MediaType.APPLICATION_JSON).content(json_data))
                .andExpect(status().isNoContent());
    }

    @WithMockUser("test-user")
    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
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

// --------------------------------------- REVIEWS --------------------------------------------------------------------

    @Test
    public void givenFindReviewsByMovieRequest_shouldSucceedWith200() throws Exception {
        Long movieId = 3L;
        mockMvc.perform(get("/movies/{movieId}/reviews", movieId).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.reviewDTOList").isNotEmpty())
                .andExpect(jsonPath("$._embedded.reviewDTOList[*].movieId",
                        everyItem(comparesEqualTo(movieId.intValue()))));
    }

    @WithMockUser("test-user")
    @Test
    public void givenFindReviewsByMovieRequest_shouldFailWith404() throws Exception {
        Long movieId = 999L;
        mockMvc.perform(get("/movies/{movieId}/reviews", movieId).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    /**
     * Performs a request for retrieving a specific {@link Review} of a specific {@link Movie}.
     * The request has to return HTTP status code 200.
     * @throws Exception if any errors occur the execution of the test.
     */
    @Test
    public void givenFindReviewByIdRequest_shouldSucceedWith200() throws Exception {
        Long movieId = 5L;
        Long reviewId = 5L;

        mockMvc.perform(get("/movies/{movieId}/reviews/{reviewId}", movieId, reviewId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.movieId").value(movieId))
                .andExpect(jsonPath("$.id").value(reviewId));
    }

    /**
     * Performs a request to retrieve {@link Review} data for a specific {@link Movie}.
     * The first time the request contains an invalid movieId and a valid reviewId.
     * The second time the request contains a valid movieId and an invalid reviewId.
     * Both requests have to return HTTP code 404.
     * @throws Exception if any errors occur the execution of the test.
     */
    @Test
    public void givenFindReviewByIdRequest_shouldFailWith404() throws Exception {
        Long movieId = 999L;
        Long reviewId = 3L;

        mockMvc.perform(get("/movies/{movieId}/reviews/{reviewId}", movieId, reviewId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Not Found"))
                .andExpect(jsonPath("$.status").value("404"))
                .andExpect(content().string(containsString(
                        String.format("Review with id %s could not be found for Movie with id %s!",
                                reviewId, movieId))));

        movieId = 2L;
        reviewId = 999L;

        mockMvc.perform(get("/movies/{movieId}/reviews/{reviewId}", movieId, reviewId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Not Found"))
                .andExpect(jsonPath("$.status").value("404"))
                .andExpect(content().string(containsString(
                        String.format("Review with id %s could not be found for Movie with id %s!",
                                reviewId, movieId))));
    }

    /**
     * Attempts to create a new {@link Review} and add it to the {@link Movie} specified by movieId.
     * The request made in the method has to return HTTP code 201.
     * @throws Exception if any errors occur the execution of the test.
     */
//    @Test
//    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
//    public void givenAddReviewToMovieRequest_shouldSucceedWith201() throws Exception {
//        Long movieId = 1L;
//        ReviewDTO reviewDTO = new ReviewDTO("Test create and insert review with reviewDTO.",
//                2, LocalDate.of(2024,9,1), true, 3L);
//
//        ObjectMapper om = new ObjectMapper().findAndRegisterModules();
//        String reviewJsonData = om.writeValueAsString(reviewDTO);
//
//        mockMvc.perform(post("/movies/{movieId}/reviews", movieId).contentType(MediaType.APPLICATION_JSON)
//                        .content(reviewJsonData))
//                .andExpect(status().isCreated())
//                .andExpect(jsonPath("$.comment").value(reviewDTO.getComment()))
//                .andExpect(jsonPath("$.reviewDate").value(reviewDTO.getReviewDate().toString()))
//                .andExpect(jsonPath("$.liked").value(reviewDTO.isLiked()))
//                .andExpect(jsonPath("$.rating").value(reviewDTO.getRating()))
//                .andExpect(jsonPath("$.userId").value(reviewDTO.getUserId()))
//                .andExpect(jsonPath("$.movieId").value(movieId));
//    }

    /**
     * Performs request for creating and adding a review to a movie twice.
     * The first time the request contains an invalid movieId and a valid userId.
     * The second time the request contains a valid movieId and an invalid userId.
     * Both requests have to return HTTP code 404.
     * @throws Exception if any errors occur the execution of the test.
     */
//    @Test
//    public void givenAddReviewToMovieRequest_shouldFailWith404() throws Exception {
//        Long movieId = -999L;
//        ReviewDTO reviewDTO = new ReviewDTO("Test create and insert review with reviewDTO.",
//                2, LocalDate.of(2024,9,1), true, 3L);
//
//        ObjectMapper om = new ObjectMapper().findAndRegisterModules();
//        String reviewJsonData = om.writeValueAsString(reviewDTO);
//
//        mockMvc.perform(post("/movies/{movieId}/reviews", movieId).contentType(MediaType.APPLICATION_JSON)
//                        .content(reviewJsonData))
//                .andExpect(status().isNotFound())
//                .andExpect(content().string(containsString(String.format("Could not find movie: %s", movieId))));
//
//        movieId = 4L;
//        reviewDTO.setUserId(-999L);
//
//        reviewJsonData = om.writeValueAsString(reviewDTO);
//        mockMvc.perform(post("/movies/{movieId}/reviews", movieId).contentType(MediaType.APPLICATION_JSON)
//                        .content(reviewJsonData))
//                .andExpect(status().isNotFound())
//                .andExpect(content().string(containsString(String.format("Could not find user: %s", reviewDTO.getUserId()))));
//    }

    /**
     * Performs a request for updating a specific {@link Review} of a specific {@link Movie}.
     * Verifies that data has been updated by also performing a request to retrieve the data of the updated {@link Movie}.
     * The update request has to return HTTP status code 204 while the get request has to return HTTP code 200.
     * @throws Exception if any errors occur the execution of the test.
     */
//    @Test
//    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
//    public void givenEditReviewForMovieRequest_shouldSucceedWith204() throws Exception {
//        Long movieId = 3L;
//        Long reviewId = 3L;
//        ReviewUpdateDTO updateDTO = new ReviewUpdateDTO("Update test comment!", 1, false);
//        String updateReviewJsonData = new ObjectMapper().writeValueAsString(updateDTO);
//
//        mockMvc.perform(put("/movies/{movieId}/reviews/{reviewId}", movieId, reviewId)
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(updateReviewJsonData))
//                .andExpect(status().isNoContent());
//
//        mockMvc.perform(get("/reviews/{reviewId}", reviewId)
//                .contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.comment").value(updateDTO.getComment()))
//                .andExpect(jsonPath("$.rating").value(updateDTO.getRating()))
//                .andExpect(jsonPath("$.liked").value(updateDTO.isLiked()));
//    }

    /**
     * Performs a request to update {@link Review} data for a specific {@link Movie}.
     * The first time the request contains an invalid movieId and a valid reviewId.
     * The second time the request contains a valid movieId and an invalid reviewId.
     * Both requests have to return HTTP code 404.
     * @throws Exception if any errors occur the execution of the test.
     */
//    @Test
//    public void givenEditReviewForMovieRequest_shouldFailWith404() throws Exception {
//        Long movieId = 999L;
//        Long reviewId = 3L;
//        ReviewUpdateDTO updateDTO = new ReviewUpdateDTO("Update test comment!", 1, false);
//        String updateReviewJsonData = new ObjectMapper().writeValueAsString(updateDTO);
//
//        mockMvc.perform(put("/movies/{movieId}/reviews/{reviewId}", movieId, reviewId)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(updateReviewJsonData))
//                .andExpect(status().isNotFound())
//                .andExpect(content().string(containsString(String.format("Could not find movie: %s", movieId))));
//
//        movieId = 2L;
//        reviewId = 999L;
//
//        mockMvc.perform(put("/movies/{movieId}/reviews/{reviewId}", movieId, reviewId)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(updateReviewJsonData))
//                .andExpect(status().isNotFound())
//                .andExpect(content().string(containsString(String.format("Could not find review: %s", reviewId))));
//    }

    /**
     * Performs a request for deleting a specific {@link Review} of a specific {@link Movie}.
     * Verifies that the {@link Review} has been deleted by then querying for the deleted {@link Review}.
     * The delete request has to return HTTP status code 204 while the get request has to return HTTP code 404.
     * @throws Exception if any errors occur the execution of the test.
     */
    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void givenRemoveReviewByIdRequest_shouldSucceedWith204() throws Exception {
        Long movieId = 7L;
        Long reviewId = 7L;

        mockMvc.perform(delete("/movies/{movieId}/reviews/{reviewId}", movieId, reviewId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/reviews/{reviewId}", reviewId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    /**
     * Performs a request to delete a {@link Review} for a specific {@link Movie}.
     * The first time the request contains an invalid movieId and a valid reviewId.
     * The second time the request contains a valid movieId and an invalid reviewId.
     * Both requests have to return HTTP code 404.
     * @throws Exception if any errors occur the execution of the test.
     */
    @Test
    public void givenRemoveReviewByIdRequest_shouldFailWith404() throws Exception {
        Long movieId = 999L;
        Long reviewId = 3L;

        mockMvc.perform(delete("/movies/{movieId}/reviews/{reviewId}", movieId, reviewId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Not Found"))
                .andExpect(jsonPath("$.status").value("404"))
                .andExpect(content().string(containsString(
                        String.format("Review with id 3 could not be found for Movie with id 999!",
                                reviewId,movieId))));

        movieId = 2L;
        reviewId = 999L;

        mockMvc.perform(delete("/movies/{movieId}/reviews/{reviewId}", movieId, reviewId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Not Found"))
                .andExpect(jsonPath("$.status").value("404"))
                .andExpect(content().string(containsString(
                        String.format("Review with id %s could not be found for Movie with id %s!", reviewId, movieId))));
    }

// ------------------------------------------- PERSONS ----------------------------------------------------------------

    @Test
    public void givenFindActorsByMovieRequest_shouldSucceedWith200() throws Exception {
        Long movieId = 5L;
        mockMvc.perform(get("/movies/{movieId}/actors", movieId).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.personDTOList[*].id", containsInAnyOrder(1)));
    }

    @Test
    public void givenFindActorsByMovieRequest_shouldFailWith404() throws Exception {
        Long movieId = 999L;
        mockMvc.perform(get("/movies/{movieId}/actors", movieId).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Not Found"))
                .andExpect(jsonPath("$.status").value("404"))
                .andExpect(content().string(containsString(
                        String.format("Movie with id %s could not be found!", movieId))));
    }

    @Test
    public void givenFindDirectorsByMovieRequest_shouldSucceedWith200() throws Exception {
        Long movieId = 14L;
        mockMvc.perform(get("/movies/{movieId}/directors", movieId).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.personDTOList[*].id", containsInAnyOrder(19,20)));
    }

    @Test
    public void givenFindDirectorsByMovieRequest_shouldFailWith404() throws Exception {
        Long movieId = 999L;
        mockMvc.perform(get("/movies/{movieId}/directors", movieId).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Not Found"))
                .andExpect(jsonPath("$.status").value("404"))
                .andExpect(content().string(containsString(
                        String.format("Movie with id %s could not be found!", movieId))));
    }

// -------------------------------------------------------------------------------------------------------------------

    @Test
    public void givenFindFindAllMoviesPaged_shouldSucceedWith200() throws Exception {
        int page = 3;
        int size = 3;
        String sortBy = "title";
        String sortDirection = "desc";

        mockMvc.perform(get("/movies?page={page}&size={size}&sortBy={sortBy}&sortDirection={sortDirection}",
                        page, size, sortBy, sortDirection)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.movieDTOList").isNotEmpty())
                .andExpect(jsonPath("$._links").isNotEmpty())
                .andExpect(jsonPath("$.page.size").value(size))
                .andExpect(jsonPath("$.page.number").value(page));
    }

    /**
     * Creates a request which fetches the {@link Genre} resources associated with
     * {@link Movie} with {@code id = 5L}.
     * The API has to return a 200 Ok status and a list of {@link Genre} resources.
     *
     * @throws Exception if any errors occur the execution of the test.
     */
    @Test
    public void givenFindGenresByMovieRequest_shouldSucceedWith200() throws Exception {
        Long movieId = 5L;

        mockMvc.perform(get("/movies/{movieId}/genres", movieId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.genreDTOList").isNotEmpty());
    }

    /**
     * Creates a request which fetches the {@link Genre} resources associated with
     * {@link Movie} with {@code id = 999L}.
     * The API has to return a 404 Not Found status and an error message.
     *
     * @throws Exception if any errors occur the execution of the test.
     */
    @Test
    public void givenFindGenresByMovieRequest_shouldFailWith404() throws Exception {
        Long movieId = 999L;

        mockMvc.perform(get("/movies/{movieId}/genres", movieId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.title").value("Not Found"))
                .andExpect(jsonPath("$.status").value("404"))
                .andExpect(content().string(containsString(
                        String.format("Movie with id %s could not be found!", movieId))));
    }

    /**
     * Creates a request which fetches the {@link Country} resources associated with
     * {@link Movie} with {@code id = 5L}.
     * The API has to return a 200 Ok status and a list of {@link Country} resources.
     *
     * @throws Exception if any errors occur the execution of the test.
     */
    @Test
    public void givenFindCountriesByMovieRequest_shouldSucceedWith200() throws Exception {
        Long movieId = 5L;

        mockMvc.perform(get("/movies/{movieId}/countries", movieId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.countryDTOList").isNotEmpty());
    }

    /**
     * Creates a request which fetches the {@link Country} resources associated with
     * {@link Movie} with {@code id = 999L}.
     * The API has to return a 404 Not Found status and an error message.
     *
     * @throws Exception if any errors occur the execution of the test.
     */
    @Test
    public void givenFindCountriesByMovieRequest_shouldFailWith404() throws Exception {
        Long movieId = 999L;

        mockMvc.perform(get("/movies/{movieId}/countries", movieId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Not Found"))
                .andExpect(jsonPath("$.status").value("404"))
                .andExpect(content().string(containsString(
                        String.format("Movie with id %s could not be found!", movieId))));
    }
}
