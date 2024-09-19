package at.saekenz.cinerator.controller;

import at.saekenz.cinerator.model.follow.FollowActionDTO;
import at.saekenz.cinerator.model.movie.Movie;
import at.saekenz.cinerator.model.review.Review;
import at.saekenz.cinerator.model.user.User;
import at.saekenz.cinerator.model.user.UserCreationDTO;
import at.saekenz.cinerator.model.user.UserDTO;
import at.saekenz.cinerator.model.userlist.UserList;
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
     * @throws Exception if any errors occur the execution of the test.
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
     * @throws Exception if any errors occur the execution of the test.
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
                .andExpect(content().string(containsString("\"code\":400")))
                .andDo(print());
    }

    /**
     * Creates a request to set a user's account status to enabled.
     * The API has to return HTTP code 204.
     * @throws Exception if any errors occur the execution of the test.
     */
    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void givenEnableUserRequest_shouldSucceedWith204() throws Exception {
        Long userId = 1L;

        mockMvc.perform(put("/users/{userId}/enable", userId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    /**
     * Creates a request to set a user's account status to enabled.
     * The API has to return HTTP code 404 since no user with id 99 exists.
     * @throws Exception if any errors occur the execution of the test.
     */
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
     * @throws Exception if any errors occur the execution of the test.
     */
    @WithMockUser("test-user")
    @Test
    public void givenFindWatchlistByUserRequest_shouldSucceedWith200() throws Exception {
        Long userId = 2L;
        mockMvc.perform(get("/users/{userId}/watchlist", userId).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.movieDTOList.length()").value(3))
                .andExpect(jsonPath("$._embedded.movieDTOList[*].id", hasItems(2,3,7)));
    }

    /**
     * Attempts to retrieve the watchlist belonging to user with userId -999.
     * The API has to return HTTP code 404 since no user with such an userId exists in the database
     * @throws Exception if any errors occur the execution of the test.
     */
    @WithMockUser("test-user")
    @Test
    public void givenFindWatchlistByUserRequest_shouldFailWith404() throws Exception {
        Long userId = -999L;
        mockMvc.perform(get("/users/{userId}/watchlist", userId).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andDo(print());
    }

    /**
     * Performs request for adding a movie to a user's watchlist.
     * The API has to return HTTP code 204.
     * To check if the movie was added correctly an HTTP GET is then
     * made which has to return a list containing the newly added movie.
     * @throws Exception if any errors occur the execution of the test.
     */
    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void givenAddMovieToWatchlistRequest_shouldSucceedWith200() throws Exception {
        Long userId = 2L;
        Long movieId = 11L;

        mockMvc.perform(put("/users/{userId}/watchlist", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(String.valueOf(movieId)))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/users/{userId}/watchlist", userId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath(String.format("$._embedded.movieDTOList[?(@.id == %s)]", movieId)).exists());
    }

    /**
     * Performs request for adding a movie to a user's watchlist twice.
     * The first time the request contains an invalid userId and a valid movieId.
     * The second time the request contains a valid userId and an in valid movieId.
     * Both requests have to return HTTP code 404.
     * @throws Exception if any errors occur the execution of the test.
     */
    @Test
    public void givenAddMovieToWatchlistRequest_shouldFailWith404() throws Exception {
       Long userId = -999L;
       Long movieId = 11L;

       mockMvc.perform(put("/users/{userId}/watchlist", userId, movieId)
               .contentType(MediaType.APPLICATION_JSON)
               .content(String.valueOf(movieId)))
               .andExpect(status().isNotFound())
               .andExpect(content()
                       .string(containsString(String.format("Could not find user: %s", userId))));

       userId = 2L;
       movieId = -999L;

        mockMvc.perform(put("/users/{userId}/watchlist", userId, movieId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(String.valueOf(movieId)))
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString(String.format("Could not find movie: %s", movieId))));
    }

    /**
     *
     * Attempts to remove a movie from a user's watchlist by id.
     * Request is made twice and should return HTTP code 404 the for the second request because the
     * resource is already deleted
     * @throws Exception if any errors occur the execution of the test.
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
                .andExpect(status().isNotFound());
    }

    /**
     * Creates a request to remove a specific movie from a user's watchlist.
     * Has to return HTTP code 404 since the user with id -999 does not exist in the database
     * @throws Exception if any errors occur the execution of the test.
     */
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

    /**
     * Creates a request to fetch a specific movie from a user's watchlist.
     * Has to return HTTP code 200 and a JSON representation of the movie resource.
     * @throws Exception if any errors occur the execution of the test.
     */
    @Test
    public void givenFindMovieInWatchlistByIdRequest_shouldSucceedWith200() throws Exception {
        Long userId = 1L;
        Long movieId = 5L;

        mockMvc.perform(get("/users/{userId}/watchlist/{movieId}", userId, movieId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(movieId));
    }

    /**
     * Creates two requests to fetch a specific movie from a user's watchlist.
     * Has to return HTTP code 404 since no user/movie with id -999 exists.
     * @throws Exception if any errors occur the execution of the test.
     */
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
     * @throws Exception if any errors occur the execution of the test.
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
     * @throws Exception if any errors occur the execution of the test.
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
     * @throws Exception if any errors occur the execution of the test.
     */
    @Test
    public void givenFindMoviesLikedByUserRequest_shouldSucceedWith200AndReturnListOfMovies() throws Exception {
        Long userId = 1L;

        mockMvc.perform(get("/users/{userId}/movies/liked", userId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.movieDTOList[*].id", hasItems(1,2,5,9)));
    }

    /**
     * Creates a request to retrieve {@link Movie} objects liked by {@link User} with id = 4.
     * Returns an empty list and HTTP code 200.
     * @throws Exception if any errors occur the execution of the test.
     */
    @Test
    public void givenFindMoviesLikedByUserRequest_shouldSucceedWith200AndReturnEmptyList() throws Exception {
        Long userId = 4L;

        mockMvc.perform(get("/users/{userId}/movies/liked", userId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(is("{}")));
    }

    /**
     * Creates a request to retrieve {@link Movie} objects rated 3 by {@link User} with id = 1.
     * Has to return movies with id 1 & 9 + HTTP code 200.
     * @throws Exception if any errors occur the execution of the test.
     */
    @Test
    public void givenFindMoviesRatedByUserRequest_shouldSucceedWith200() throws Exception{
        Long userId = 1L;
        Integer rating = 3;

        mockMvc.perform(get("/users/{userId}/movies/rated/{rating}", userId, rating)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.movieDTOList[*].id", containsInAnyOrder(1,9)));
    }

    /**
     * Creates a request to retrieve {@link Movie} objects rated 3 by {@link User} with id = -999L.
     * Returns HTTP code 404 since this {@link User} does not exist in the database.
     * @throws Exception if any errors occur the execution of the test.
     */
    @Test
    public void givenFindMoviesRatedByUserRequest_shouldFailWith404() throws Exception{
        Long userId = -999L;
        Integer rating = 3;

        mockMvc.perform(get("/users/{userId}/movies/rated/{rating}", userId, rating)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

// -------------------------------------- FOLLOWERS -------------------------------------------------------------------

    /**
     * Creates a request to retrieve {@link User} objects which follow {@link User} with id = 2.
     * Returns a list of {@link User} objects and HTTP code 200.
     * @throws Exception if any errors occur the execution of the test.
     */
    @Test
    public void givenFindFollowersByUser_shouldSucceedWith200() throws Exception {
        Long userId = 2L;

        mockMvc.perform(get("/users/{userId}/followers", userId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.userDTOList[*].id", hasItems(1,3,4)));

    }

    /**
     * Creates a request to retrieve {@link User} objects which follow {@link User} with id = -999.
     * Returns HTTP code 404 since this {@link User} does not exist.
     * @throws Exception if any errors occur the execution of the test.
     */
    @Test
    public void givenFindFollowersByUser_shouldFailWith404() throws Exception {
        Long userId = -999L;

        mockMvc.perform(get("/users/{userId}/followers", userId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString(String.format("Could not find user: %s", userId))));
    }

    /**
     * Creates a request to retrieve {@link User} objects which {@link User} with id = 2 follows.
     * Returns a list of {@link User} objects and HTTP code 200.
     * @throws Exception if any errors occur the execution of the test.
     */
    @Test
    public void givenFindFollowingByUser_shouldSucceedWith200() throws Exception {
        Long userId = 2L;

        mockMvc.perform(get("/users/{userId}/following", userId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.userDTOList[*].id", hasItem(1)));
    }

    /**
     * Creates a request to retrieve {@link User} objects which {@link User} with id = -999 follows.
     * Returns HTTP code 404 since this {@link User} does not exist.
     * @throws Exception if any errors occur the execution of the test.
     */
    @Test
    public void givenFindFollowingByUser_shouldFailWith404() throws Exception {
        Long userId = -999L;

        mockMvc.perform(get("/users/{userId}/following", userId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString(String.format("Could not find user: %s", userId))));
    }

    /**
     * Creates a request to follow a {@link User} which returns HTTP code 201.
     * Checks if the {@link User} has been followed by retrieving all the user's followers.
     * @throws Exception if any errors occur the execution of the test.
     */
    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void givenFollowAnotherUserRequest_shouldSucceedWith200() throws Exception {
        Long userId = 4L;
        Long followerId = 2L;

        FollowActionDTO followDTO = new FollowActionDTO(followerId);
        String jsonData = new ObjectMapper().writeValueAsString(followDTO);

        mockMvc.perform(post("/users/{userId}/follow", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonData))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/users/{userId}/followers", userId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.userDTOList[*].id", hasItem(2)));
    }

    /**
     * Performs request for following another {@link User}.
     * The first time the request contains an invalid userId and a valid followerId.
     * The second time the request contains a valid userId and an invalid followerId.
     * Both requests have to return HTTP code 404.
     * @throws Exception if any errors occur the execution of the test.
     */
    @Test
    public void givenFollowAnotherUserRequest_shouldFailWith404() throws Exception {
        Long userId = -999L;

        FollowActionDTO followDTO = new FollowActionDTO(2L);
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonData = objectMapper.writeValueAsString(followDTO);

        mockMvc.perform(post("/users/{userId}/follow", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonData))
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString(
                        String.format("Could not find user: %s", userId))));

        userId = 2L;
        followDTO = new FollowActionDTO(-999L);
        jsonData = objectMapper.writeValueAsString(followDTO);

        mockMvc.perform(post("/users/{userId}/follow", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonData))
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString(
                        String.format("Could not find user: %s", followDTO.followerId()))));
    }

    /**
     * Creates a request to unfollow a {@link User} which returns HTTP code 204.
     * Checks if the {@link User} has been unfollowed by retrieving all the user's followers.
     * @throws Exception if any errors occur the execution of the test.
     */
    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void givenUnfollowAnotherUserRequest_shouldSucceedWith204() throws Exception {
        Long userId = 2L;

        FollowActionDTO followDTO = new FollowActionDTO(3L);
        String jsonData = new ObjectMapper().writeValueAsString(followDTO);

        mockMvc.perform(delete("/users/{userId}/unfollow", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonData))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/users/{userId}/followers", userId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.userDTOList[*].id", not(contains(3))));
    }

    /**
     * Performs request for unfollowing another {@link User}.
     * The first time the request contains an invalid userId and a valid followerId.
     * The second time the request contains a valid userId and an invalid followerId.
     * Both requests have to return HTTP code 404.
     * @throws Exception if any errors occur the execution of the test.
     */
    @Test
    public void givenUnfollowAnotherUserRequest_shouldFailWith404() throws Exception {
        Long userId = -999L;

        FollowActionDTO followDTO = new FollowActionDTO(2L);
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonData = objectMapper.writeValueAsString(followDTO);

        mockMvc.perform(delete("/users/{userId}/unfollow", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonData))
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString(
                        String.format("Could not find user: %s", userId))));

        userId = 2L;
        followDTO = new FollowActionDTO(-999L);
        jsonData = objectMapper.writeValueAsString(followDTO);

        mockMvc.perform(delete("/users/{userId}/unfollow", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonData))
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString(
                        String.format("Could not find user: %s", followDTO.followerId()))));
    }

// --------------------------------- LISTS --------------------------------------------------------------------------

    /**
     * Creates a request which fetches all {@link UserList} objects associated with the {@link User}
     * with {@code id = 4L}. The API has to return a 200 Ok status and a collection of
     * {@link UserList} resources.
     *
     * @throws Exception if any errors occur the execution of the test.
     */
    @Test
    public void givenFindListsByUserRequest_shouldSucceedWith200() throws Exception {
        Long userId = 4L;

        mockMvc.perform(get("/users/{id}/lists", userId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.userListDTOList").isNotEmpty())
                .andExpect(jsonPath("$._embedded.userListDTOList[*].userId",
                        everyItem(comparesEqualTo(4))));
    }

    /**
     * Creates a request which fetches all {@link UserList} objects associated with the {@link User}
     * with {@code id = -999L}. The API has to return a 404 Not Found status and an appropriate
     * error message.
     *
     * @throws Exception if any errors occur the execution of the test.
     */
    @Test
    public void givenFindListsByUserRequest_shouldFailWith404() throws Exception {
        Long userId = -999L;

        mockMvc.perform(get("/users/{id}/lists", userId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString(
                        String.format("Could not find user: %s", userId))));
    }

// ------------------------------- SEARCH ----------------------------------------------------------------------------

    /**
     * Creates a request which searches for {@code Users} based on parameters
     * {@code name}, {@code username}, {@code email} and {@code role}.
     * The API has to return a 200 Ok status and a list of {@link UserDTO} resources.
     *
     * @throws Exception if any errors occur the execution of the test.
     */
    @Test
    public void givenUserSearchRequest_shouldSucceedWith200AndReturnListOfUsers() throws Exception {
        String nameSearchTerm = "";
        String usernameSearchTerm = "user";
        String emailSearchTerm = "@example";
        String roleSearchTerm = "user";

        mockMvc.perform(get("/users/search?name={name}&username={username}&email={email}&role={role}",
                        nameSearchTerm, usernameSearchTerm, emailSearchTerm, roleSearchTerm)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.userDTOList[*].name", everyItem(containsStringIgnoringCase(nameSearchTerm))))
                .andExpect(jsonPath("$._embedded.userDTOList[*].username", everyItem(containsStringIgnoringCase(usernameSearchTerm))))
                .andExpect(jsonPath("$._embedded.userDTOList[*].email", everyItem(containsStringIgnoringCase(emailSearchTerm))))
                .andExpect(jsonPath("$._embedded.userDTOList[*].role", everyItem(containsStringIgnoringCase(roleSearchTerm))));

    }

    /**
     * Creates a request which searches for {@code Users} based on parameters
     * {@code name}, {@code username}, {@code email} and {@code role}.
     * The API has to return a 200 Ok status and an empty list.
     *
     * @throws Exception if any errors occur the execution of the test.
     */
    @Test
    public void givenUserSearchRequest_shouldSucceedWith200AndReturnEmptyList() throws Exception {
        String nameSearchTerm = "Lewis Hamilton";

        mockMvc.perform(get("/users/search?name={name}",
                        nameSearchTerm)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(is("{}")));
    }


}
