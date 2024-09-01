package at.saekenz.cinerator.controller;

import at.saekenz.cinerator.model.user.User;
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
                .andExpect(jsonPath("$._embedded.userList").isNotEmpty());
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
                .andExpect(jsonPath("$._embedded.userList[*].username", everyItem(equalToIgnoringCase(username))));
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
                .andExpect(jsonPath("$._embedded.userList[*].role", everyItem(equalToIgnoringCase(role))));
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
    public void givenCreateUserRequest_shouldSucceedWith201() throws Exception {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String encodedPassword = passwordEncoder.encode("password");

        User user = new User("fencingcliff",encodedPassword,"USER",true, Set.of());
        String userData = new ObjectMapper().writeValueAsString(user);

        mockMvc.perform(post("/users").contentType(MediaType.APPLICATION_JSON).content(userData))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value("fencingcliff"))
                .andExpect(jsonPath("$.password").value(encodedPassword))
                .andExpect(jsonPath("$.role").value("USER"))
                .andExpect(jsonPath("$.enabled").value(true))
                .andDo(print());
    }

    @WithMockUser("test-user")
    @Test
    public void givenCreateUserRequest_shouldFailWith400() throws Exception {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String encodedPassword = passwordEncoder.encode("password");

        String userData = String.format("""
                {
                  "username": "haumeaweb",
                  "password": "%s",
                  "role": "USER",
                  "enabled": "peter",
                  "watchlist": [],
                  "reviews": []
                }""", encodedPassword);

        mockMvc.perform(post("/users").contentType(MediaType.APPLICATION_JSON).content(userData))
                .andExpect(status().isBadRequest())
                .andDo(print());
    }

    @WithMockUser("test-user")
    @Test
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

    @WithMockUser("test-user")
    @Test
    public void givenUpdateUserRequest_shouldSucceedWith201() throws Exception {
        User user = new User("updatedUser123","newPassword123","ADMIN",true,Set.of());
        String userData = new ObjectMapper().writeValueAsString(user);
        Long userId = 4L;

        mockMvc.perform(put("/users/{userId}", userId).contentType(MediaType.APPLICATION_JSON).content(userData))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value("updatedUser123"))
                .andExpect(jsonPath("$.password").value("newPassword123"))
                .andExpect(jsonPath("$.role").value("ADMIN"))
                .andExpect(jsonPath("$.enabled").value(true))
                .andDo(print());
    }

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

        mockMvc.perform(post("/users/{userId}/watchlist", userId, movieId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(String.valueOf(movieId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath(String.format("$.watchlist[?(@.id == %s)]", movieId)).exists());
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

    @Test
    public void givenFindReviewsByUserRequest_shouldSucceedWith200() throws Exception {
        Long userId = 1L;

        mockMvc.perform(get("/users/{userId}/reviews", userId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.reviewList[*].id", hasItems(1,5,9,12,13)));
    }

    @Test
    public void givenFindReviewsByUserRequest_shouldFailWith404() throws Exception {
        Long userId = -999L;

        mockMvc.perform(get("/users/{userId}/reviews", userId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}
