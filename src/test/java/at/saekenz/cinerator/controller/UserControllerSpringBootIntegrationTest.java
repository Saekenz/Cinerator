package at.saekenz.cinerator.controller;

import at.saekenz.cinerator.model.movie.Movie;
import at.saekenz.cinerator.model.review.Review;
import at.saekenz.cinerator.model.user.User;
import at.saekenz.cinerator.model.user.UserCreationDTO;
import at.saekenz.cinerator.model.user.UserDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Set;

import static org.hamcrest.Matchers.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class UserControllerSpringBootIntegrationTest {

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
    public void givenFindAllUsersRequest_shouldSucceedWith200() throws Exception {
        mockMvc.perform(get("/users").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.userDTOList").isNotEmpty());
    }

    @WithMockUser("test-user")
    @Test
    public void givenFindUserByIdRequest_shouldSucceedWith200() throws Exception {
        Long userId = 1L;
        mockMvc.perform(get("/users/{userId}", userId).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userId));
    }

    @WithMockUser("test-user")
    @Test
    public void givenFindUserByIdRequest_shouldFailWith404() throws Exception {
        Long userId = -999L;
        mockMvc.perform(get("/users/{userId}", userId).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString(String.format("user: %s", userId))));
    }

    @WithMockUser("test-user")
    @Test
    public void givenFindUsersByUsernameRequest_shouldSucceedWith200() throws Exception {
        String username = "UserD";
        mockMvc.perform(get("/users/username/{username}", username).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.userDTOList[*].username", everyItem(equalToIgnoringCase(username))));
    }

    @WithMockUser("test-user")
    @Test
    public void givenFindUsersByUsernameRequest_shouldFailWith404() throws Exception {
        String username = "peter";
        mockMvc.perform(get("/users/username/{username}", username).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString(String.format("username: %s", username))));
    }

    @WithMockUser("test-user")
    @Test
    public void givenFindUsersByRoleRequest_shouldSucceedWith200() throws Exception {
        String role = "admin";
        mockMvc.perform(get("/users/role/{role}", role).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.userDTOList[*].role", everyItem(equalToIgnoringCase(role))));
    }

    @WithMockUser("test-user")
    @Test
    public void givenFindUsersByRoleRequest_shouldFailWith404() throws Exception {
        String role = "wizard";
        mockMvc.perform(get("/users/role/{role}", role).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString(String.format("role: %s", role))));
    }

    @WithMockUser("test-user")
    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void givenCreateUserRequest_shouldSucceedWith201() throws Exception {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String encodedPassword = passwordEncoder.encode("password");

        UserCreationDTO user = new UserCreationDTO("michael.brown@example.com", "fencingcliff", encodedPassword);
        String userData = new ObjectMapper().writeValueAsString(user);

        mockMvc.perform(post("/users").contentType(MediaType.APPLICATION_JSON).content(userData))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value(user.getUsername()))
                .andExpect(jsonPath("$.name").value(""))
                .andExpect(jsonPath("$.email").value(user.getEmail()))
                .andExpect(jsonPath("$.bio").value(""))
                .andExpect(jsonPath("$.role").value("USER"))
                .andExpect(jsonPath("$.enabled").value(false));
    }

    @WithMockUser("test-user")
    @Test
    public void givenCreateUserRequest_shouldFailWith400() throws Exception {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String encodedPassword = passwordEncoder.encode("password");

        String userData = String.format("""
                {
                  "email": "olivia.martin@example.com",
                  "password": "%s"
                }""", encodedPassword);

        mockMvc.perform(post("/users").contentType(MediaType.APPLICATION_JSON).content(userData))
                .andExpect(status().isBadRequest());
    }

    @WithMockUser("test-user")
    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void givenDeleteUserRequest_shouldSucceedWith204() throws Exception {
        Long userId = 3L;
        mockMvc.perform(delete("/users/{userId}", userId).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @WithMockUser("test-user")
    @Test
    public void givenDeleteUserRequest_shouldFailWith404() throws Exception {
        Long userId = 999L;
        mockMvc.perform(delete("/users/{userId}", userId).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    /**
     * Create a request to update a {@link User} which expects an HTTP code 204 response from the API.
     * To verify the {@link User} was successfully updated, a get request is then performed which expects
     * an HTTP code 200 response.
     * @throws Exception
     */
    @WithMockUser("test-user")
    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void givenUpdateUserRequest_shouldSucceedWith204() throws Exception {
        UserDTO updatedUser = new UserDTO();

        updatedUser.setUsername("updatedUser123");
        updatedUser.setName("Updated Name");
        updatedUser.setEmail("updatedUser@example.com");
        updatedUser.setBio("Updated bio");

        String userData = new ObjectMapper().writeValueAsString(updatedUser);
        Long userId = 3L;

        mockMvc.perform(put("/users/{userId}", userId).contentType(MediaType.APPLICATION_JSON).content(userData))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/users/{userId}", userId).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value(updatedUser.getUsername()))
                .andExpect(jsonPath("$.name").value(updatedUser.getName()))
                .andExpect(jsonPath("$.email").value(updatedUser.getEmail()))
                .andExpect(jsonPath("$.bio").value(updatedUser.getBio()));
    }

    /**
     * Create a request to update a {@link User}. The API has to return HTTP code 400 since
     * the updated data does not contain a password value
     * @throws Exception
     */
    @Test
    public void givenUpdateUserRequest_shouldFailWith400() throws Exception {
        Long userId = 2L;
        String userData = """
                {
                  "username": "updatedUser123",
                  "name": "Updated Name",
                  "email": "updatedUser@example.com"
                }""";

        mockMvc.perform(put("/users/{userId}", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(userData))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString(("The property 'bio' in entity"))));
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void givenEnableUserRequest_shouldSucceedWith204() throws Exception {
        Long userId = 1L;

        mockMvc.perform(put("/users/{userId}/enable", userId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    public void givenEnableUserRequest_shouldFailWith404() throws Exception {
        Long userId = 99L;

        mockMvc.perform(put("/users/{userId}/enable", userId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content()
                        .string(containsString(String.format("Could not find user: %s", userId))));
    }

// ------------------------------------- WATCHLIST ------------------------------------------------------------------

    /**
     * Attempts to retrieve the watchlist belonging to user with userId 2.
     * This user's watchlist should contain movies with movieIds [2,3,7].
     * @throws Exception
     */
    @WithMockUser("test-user")
    @Test
    public void givenFindWatchlistByUserRequest_shouldSucceedWith200() throws Exception {
        Long userId = 2L;
        mockMvc.perform(get("/users/{userId}/watchlist", userId).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.movieList.length()").value(3))
                .andExpect(jsonPath("$._embedded.movieList[*].id", hasItems(2,3,7)));
    }

    @WithMockUser("test-user")
    @Test
    public void givenFindWatchlistByUserRequest_shouldFailWith404() throws Exception {
        Long userId = -999L;
        mockMvc.perform(get("/users/{userId}/watchlist", userId).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andDo(print());
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void givenAddMovieToWatchlistRequest_shouldSucceedWith200() throws Exception {
        Long userId = 2L;
        Long movieId = 11L;

        mockMvc.perform(post("/users/{userId}/watchlist", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(String.valueOf(movieId)))
                .andExpect(status().isOk());

        mockMvc.perform(get("/users/{userId}/watchlist", userId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath(String.format("$._embedded.movieList[?(@.id == %s)]", movieId)).exists());
    }

    /**
     * Performs request for adding a movie to a user's watchlist twice.
     * The first time the request contains an invalid userId and a valid movieId.
     * The second time the request contains a valid userId and an in valid movieId.
     * Both requests have to return HTTP code 404.
     * @throws Exception
     */
    @Test
    public void givenAddMovieToWatchlistRequest_shouldFailWith404() throws Exception {
       Long userId = -999L;
       Long movieId = 11L;

       mockMvc.perform(post("/users/{userId}/watchlist", userId, movieId)
               .contentType(MediaType.APPLICATION_JSON)
               .content(String.valueOf(movieId)))
               .andExpect(status().isNotFound())
               .andExpect(content()
                       .string(containsString(String.format("Could not find user: %s", userId))));

       userId = 2L;
       movieId = -999L;

        mockMvc.perform(post("/users/{userId}/watchlist", userId, movieId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(String.valueOf(movieId)))
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString(String.format("Could not find movie: %s", movieId))));
    }

    /**
     *
     * Attempts to remove a movie from a user's watchlist by id.
     * Request is made twice to ensure HTTP code 204 is returned even if the to be deleted
     * resource does not exist anymore.
     * @throws Exception
     */
    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void givenRemoveMovieFromWatchlistRequest_shouldSucceedWith204() throws Exception {
        Long userId = 3L;
        Long movieId = 6L;

        mockMvc.perform(delete("/users/{userId}/watchlist/{movieId}", userId, movieId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        mockMvc.perform(delete("/users/{userId}/watchlist/{movieId}", userId, movieId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    public void givenRemoveMovieFromWatchlistRequest_shouldFailWith404() throws Exception {
        Long userId = -999L;
        Long movieId = 11L;

        mockMvc.perform(delete("/users/{userId}/watchlist/{movieId}", userId, movieId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(String.valueOf(movieId)))
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString(String.format("Could not find user: %s", userId))));
    }

    @Test
    public void givenFindMovieInWatchlistByIdRequest_shouldSucceedWith200() throws Exception {
        Long userId = 1L;
        Long movieId = 5L;

        mockMvc.perform(get("/users/{userId}/watchlist/{movieId}", userId, movieId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(movieId));
    }

    @Test
    public void givenFindMovieInWatchlistByIdRequest_shouldFailWith404() throws Exception {
        Long userId = -999L;
        Long movieId = 5L;

        mockMvc.perform(get("/users/{userId}/watchlist/{movieId}", userId, movieId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString(String.format("Could not find user: %s", userId))));

        userId = 1L;
        movieId = -999L;

        mockMvc.perform(get("/users/{userId}/watchlist/{movieId}", userId, movieId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString(String.format("Could not find movie: %s", movieId))));
    }

// ------------------------------------ REVIEWS -----------------------------------------------------------------------

    /**
     * Creates a request to retrieve all reviews created by {@link User} with id = 1.
     * Returns a list of {@link Review} objects and HTTP code 200.
     * @throws Exception
     */
    @Test
    public void givenFindReviewsByUserRequest_shouldSucceedWith200() throws Exception {
        Long userId = 1L;

        mockMvc.perform(get("/users/{userId}/reviews", userId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.reviewList[*].id", hasItems(1,5,9,12,13)));
    }

    /**
     * Creates a request to retrieve all reviews created by {@link User} with id = -999.
     * Since this user does not exist in the database the request returns HTTP code 404.
     * @throws Exception
     */
    @Test
    public void givenFindReviewsByUserRequest_shouldFailWith404() throws Exception {
        Long userId = -999L;

        mockMvc.perform(get("/users/{userId}/reviews", userId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    /**
     * Creates a request to retrieve {@link Movie} objects liked by {@link User} with id = 1.
     * Returns a list of liked {@link Movie} objects and HTTP code 200.
     * @throws Exception
     */
    @Test
    public void givenFindMoviesLikedByUserRequest_shouldSucceedWith200AndReturnListOfMovies() throws Exception {
        Long userId = 1L;

        mockMvc.perform(get("/users/{userId}/movies/liked", userId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.movieList[*].id", hasItems(1,2,5,9)));
    }

    /**
     * Creates a request to retrieve {@link Movie} objects liked by {@link User} with id = 4.
     * Returns an empty list of and HTTP code 200.
     * @throws Exception
     */
    @Test
    public void givenFindMoviesLikedByUserRequest_shouldSucceedWith200AndReturnEmptyList() throws Exception {
        Long userId = 4L;

        mockMvc.perform(get("/users/{userId}/movies/liked", userId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(is("{}")));
    }
}
