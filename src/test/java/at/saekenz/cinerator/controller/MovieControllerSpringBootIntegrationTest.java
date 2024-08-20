package at.saekenz.cinerator.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
                .andExpect(content().string(containsString("{\"movieList\":")));
    }

    @WithMockUser("test-user")
    @Test
    public void givenFindAllMoviesRequest_shouldFailWith404() throws Exception {
        mockMvc.perform(get("/movis").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @WithMockUser("test-user")
    @Test
    public void givenFindMoviesByTitleRequest_shouldSucceedWith200() throws Exception {
        String title = "Sicario";
        mockMvc.perform(get("/movies/title/{title}", title).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString(String.format("\"title\":\"%s\"",title))));
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
                .andExpect(content().string(containsString(String.format("\"imdb_id\":\"%s\"", imdbId))));
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
                .andExpect(content().string(containsString(String.format("\"director\":\"%s\"", director))));
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
                .andExpect(content().string(containsString(String.format("\"country\":\"%s\"", country))));
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
                .andExpect(content().string(containsString(String.format("\"genre\":\"%s\"", genre))));
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
                .andExpect(content().string(containsString(String.format("\"release_date\":\"%s", year))))
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
        String json_data = "{\n" +
                "  \"title\": \"Nightcrawler\",\n" +
                "  \"release_date\": \"2014-10-31\",\n" +
                "  \"director\": \"Dan Gilroy\",\n" +
                "  \"genre\": \"Thriller\",\n" +
                "  \"country\": \"United States\",\n" +
                "  \"imdb_id\": \"tt287271\",\n" +
                "  \"poster_url\": \"https://upload.wikimedia.org/wikipedia/en/d/d4/Nightcrawlerfilm.jpg\",\n" +
                "  \"reviews\": []\n" +
                "}";

    mockMvc.perform(post("/movies").contentType(MediaType.APPLICATION_JSON).content(json_data))
            .andExpect(status().isCreated())
            .andExpect(content().string(containsString("\"title\":\"Nightcrawler\"")))
            .andExpect(content().string(containsString("\"release_date\":\"2014-10-31\"")))
            .andExpect(content().string(containsString("\"director\":\"Dan Gilroy\"")))
            .andExpect(content().string(containsString("\"genre\":\"Thriller\"")))
            .andDo(print());
    }

    //TODO -> move to user test class
    @WithMockUser("test-user")
//    @Test
    public void testFindAllUsers() throws Exception {
        mockMvc.perform(get("/users").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}
