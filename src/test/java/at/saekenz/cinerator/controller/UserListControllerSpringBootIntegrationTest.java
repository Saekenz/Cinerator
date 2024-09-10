package at.saekenz.cinerator.controller;

import at.saekenz.cinerator.model.movie.Movie;
import at.saekenz.cinerator.model.user.User;
import at.saekenz.cinerator.model.userlist.UserList;
import at.saekenz.cinerator.model.userlist.UserListCreationDTO;
import at.saekenz.cinerator.model.userlist.UserListDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.hamcrest.Matchers.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.log;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class UserListControllerSpringBootIntegrationTest {

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

    /**
     * Creates a request which retrieves every {@link UserList} stored in the database.
     * The API has to return a 200 Ok status and a collection of all {@link UserList} objects.
     *
     * @throws Exception if any errors occur the execution of the test.
     */
    @Test
    public void givenFindAllUserListsRequest_shouldSucceedWith200() throws Exception {
        mockMvc.perform(get("/lists").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.userListDTOList").isNotEmpty());
    }

    /**
     * Creates a request which fetches {@link UserList} with {@code id = 2L}.
     * The API has to return a 200 Ok status and the requested {@link UserList}.
     *
     * @throws Exception if any errors occur the execution of the test.
     */
    @Test
    public void givenFindUseListByIdRequest_shouldSucceedWith200() throws Exception {
        Long listId = 2L;
        mockMvc.perform(get("/lists/{listId}", listId).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(listId))
                .andExpect(jsonPath("$.userId").value(listId));
    }

    /**
     * Creates a request which fetches {@link UserList} with {@code id = -999L}.
     * The API has to return a 404 Not Found status and an error message.
     *
     * @throws Exception if any errors occur the execution of the test.
     */
    @Test
    public void givenFindUserListByIdRequest_shouldFailWith404() throws Exception {
        Long listId = -999L;
        mockMvc.perform(get("/lists/{listId}", listId).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString(String.format("UserList with id %s could not be found!", listId))));
    }

    /**
     * Creates a request which adds a new {@link UserList} to the database.
     * The API has to return a 201 Created and the created {@link UserList}.
     *
     * @throws Exception if any errors occur the execution of the test.
     */
    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void givenCreateUserListRequest_shouldSucceedWith201() throws Exception {
        Long userId = 3L;
        UserListCreationDTO newList = new UserListCreationDTO("Cowboy films", "Yeehaw",
                false, userId);

        String userListData = new ObjectMapper().writeValueAsString(newList);

        mockMvc.perform(post("/lists").contentType(MediaType.APPLICATION_JSON).content(userListData))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value(newList.name()))
                .andExpect(jsonPath("$.description").value(newList.description()))
                .andExpect(jsonPath("$.userId").value(newList.userId()))
                .andExpect(jsonPath("$.private").value(newList.isPrivate()));
    }

    /**
     * Creates a request which adds a new {@link UserList} to the database. The payload includes
     * an invalid {@code userId} (-999L).
     * The API has to return a 404 Not Found status and an error message.
     *
     * @throws Exception if any errors occur the execution of the test.
     */
    @Test
    public void givenCreateUserListRequest_shouldFailWith404() throws Exception {
        Long userId = -999L;
        UserListCreationDTO newList = new UserListCreationDTO("Cowboy films", "Yeehaw",
                false, userId);

        String userListData = new ObjectMapper().writeValueAsString(newList);

        mockMvc.perform(post("/lists").contentType(MediaType.APPLICATION_JSON).content(userListData))
                .andExpect(status().isNotFound())
                .andExpect(content()
                        .string(containsString(String.format("Could not find user: %s", userId))));
    }

    /**
     * Creates a PUT request which updates {@link UserList} with {@code id = 2L}.
     * The API has to return a 204 No Content status and a link to the updated resource in
     * its location header. To check if the update was successful, a second request is made which
     * fetches the updated {@link UserList} via HTTP GET.
     *
     * @throws Exception if any errors occur the execution of the test.
     */
    @Test
    public void giveUpdateUserListRequest_shouldSucceedWith204() throws Exception {
        Long userListId = 2L;
        UserListDTO userListDTO = new UserListDTO(2L,"Bad movies", "The worst of all time",
                true, 2L, null);
        String userListData = new ObjectMapper().writeValueAsString(userListDTO);

        mockMvc.perform(put("/lists/{listId}", userListId).contentType(MediaType.APPLICATION_JSON)
                        .content(userListData))
                .andDo(log())
                .andExpect(status().isNoContent())
                .andExpect(header().stringValues("Location", "http://localhost/lists/2"));

        mockMvc.perform(get("/lists/{listId}", userListId).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(userListDTO.getName()))
                .andExpect(jsonPath("$.description").value(userListDTO.getDescription()))
                .andExpect(jsonPath("$.private").value(userListDTO.isPrivate()));
    }

    /**
     * Creates a PUT request which creates a {@link UserList} with {@code id = 99L}.
     * The API has to return a 404 Not Found status since the {@link User} contained in
     * the request does not exist in the database.
     *
     * @throws Exception if any errors occur the execution of the test.
     */
    @Test
    public void giveUpdateUserListRequest_shouldFailWith404() throws Exception {
        Long userListId = 99L;
        UserListDTO userListDTO = new UserListDTO(2L,"Bad movies", "The worst of all time",
                true, -999L, null);
        String userListData = new ObjectMapper().writeValueAsString(userListDTO);

        mockMvc.perform(put("/lists/{listId}", userListId).contentType(MediaType.APPLICATION_JSON)
                        .content(userListData))
                .andDo(log())
                .andExpect(status().isNotFound())
                .andExpect(content()
                        .string(containsString(String.format("Could not find user: %s", userListDTO.getUserId()))));
    }

    /**
     * Creates a request which removes {@link UserList} with {@code id = 2L} from the database.
     * The API has to return a 204 No Content status.
     *
     * @throws Exception if any errors occur the execution of the test.
     */
    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void givenDeleteUserListRequest_shouldSucceedWith204() throws Exception {
        Long listId = 2L;

        mockMvc.perform(delete("/lists/{listId}", listId).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    /**
     * Creates a request which removes {@link UserList} with {@code id = -999L} from the database.
     * The API has to return a 404 Not Found status and an error message.
     *
     * @throws Exception if any errors occur the execution of the test.
     */
    @Test
    public void givenDeleteUserListRequest_shouldFailWith404() throws Exception {
        Long listId = -999L;

        mockMvc.perform(delete("/lists/{listId}", listId).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString(String.format("UserList with id %s could not be found!", listId))));
    }

    /**
     * Creates a request which fetches the {@link User} of {@link UserList} with {@code id = 2L}.
     * The API has to return a 200 Ok status and the {@link User} resource.
     *
     * @throws Exception if any errors occur the execution of the test.
     */
    @Test
    public void givenFindUserByUserList_shouldSucceedWith200() throws Exception {
        Long userListId = 2L;

        mockMvc.perform(get("/lists/{listId}/user", userListId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userListId));
    }

    /**
     * Creates a request which fetches the {@link User} of {@link UserList} with {@code id = -999L}.
     * The API has to return a 404 Not Found status.
     *
     * @throws Exception if any errors occur the execution of the test.
     */
    @Test
    public void givenFindUserByUserList_shouldFailWith404() throws Exception {
        Long userListId = -999L;

        mockMvc.perform(get("/lists/{listId}/user", userListId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString(String.format("UserList with id %s could not be found!", userListId))));
    }

    /**
     * Creates a request which fetches the {@link Movie} resources associated with
     * {@link UserList} with {@code id = 2L}.
     * The API has to return a 200 Ok status and a list of {@link Movie} resources.
     *
     * @throws Exception if any errors occur the execution of the test.
     */
    @Test
    public void givenFindMoviesByUserList_shouldSucceedWith200() throws Exception {
        Long userListId = 2L;

        mockMvc.perform(get("/lists/{listId}/movies", userListId).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.movieList[*].id", containsInAnyOrder(8,7,9,5,6)));
    }

    /**
     * Creates a request which fetches the {@link Movie} resources associated with
     * {@link UserList} with {@code id = -999L}.
     * The API has to return a 404 Not Found status.
     *
     * @throws Exception if any errors occur the execution of the test.
     */
    @Test
    public void givenFindMoviesByUserList_shouldFailWith404() throws Exception {
        Long userListId = -999L;

        mockMvc.perform(get("/lists/{listId}/movies", userListId).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString(String.format("UserList with id %s could not be found!", userListId))));
    }

    /**
     * Creates a request which adds a {@link Movie} to {@link UserList} with {@code id = 2L}.
     * The API has to return a 204 No Content status and link to the {@link UserList} resource
     * in the location header. To check if the operation was successful, a second request is made which
     * fetches the {@link UserList}'s movies via HTTP GET.
     *
     * @throws Exception if any errors occur the execution of the test.
     */
    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void givenAddMovieToUserList_shouldSucceedWith204() throws Exception {
        Long userListId = 2L;
        Long movieId = 12L;

        mockMvc.perform(put("/lists/{listId}/movies", userListId)
                .contentType(MediaType.APPLICATION_JSON)
                        .content(String.valueOf(movieId)))
                .andExpect(status().isNoContent())
                .andExpect(header().stringValues("Location", "http://localhost/lists/2"));

        mockMvc.perform(get("/lists/{listId}/movies", userListId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.movieList[*].id", hasItem(12)));
    }

    /**
     * Creates a request which adds a {@link Movie} to {@link UserList} with {@code id = 2L}.
     * The API has to return a 400 Bad Request since the {@link Movie} was already added previously.
     *
     * @throws Exception if any errors occur the execution of the test.
     */
    @Test
    public void givenAddMovieToUserList_shouldFailWith400() throws Exception {
        Long userListId = 2L;
        Long movieId = 8L;

        mockMvc.perform(put("/lists/{listId}/movies", userListId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(String.valueOf(movieId)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(
                        containsString(String.format("Adding movie (id = %s) failed. (Movie was already added)", movieId))));
    }

    /**
     * Create a request which removes a {@link Movie} from {@link UserList} with {@code id = 2L}.
     * The API has to return a 204 No Content status. To check if the operation was successful, a second request is made which
     * fetches the {@link UserList}'s movies via HTTP GET.
     *
     * @throws Exception if any errors occur the execution of the test.
     */
    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void givenDeleteMovieFromUserList_shouldSucceedWith204() throws Exception {
        Long userListId = 2L;
        Long movieId = 8L;

        mockMvc.perform(delete("/lists/{userListId}/movies/{movieId}", userListId, movieId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/lists/{listId}/movies", userListId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.movieList[*].id", not(hasItem(8))));
    }

    /**
     * Create a request which removes a {@link Movie} from {@link UserList} with {@code id = 2L}.
     * The API has to return a 404 Not Found status because the {@link Movie} with {@code id = -999L}
     * does not exist in this {@link UserList}.
     *
     * @throws Exception if any errors occur the execution of the test.
     */
    @Test
    public void givenDeleteMovieFromUserList_shouldFailWith404() throws Exception {
        Long userListId = 2L;
        Long movieId = -999L;

        mockMvc.perform(delete("/lists/{userListId}/movies/{movieId}", userListId, movieId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content()
                        .string(containsString(String.format("Could not find movie: %s", movieId))));
    }
}
