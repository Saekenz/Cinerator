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

import java.time.LocalDateTime;

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
     * Creates a request which retrieves every {@link UserList} stored in the database (in a paged format).
     * The API has to return a 200 Ok status and a collection of all {@link UserList} objects.
     *
     * @throws Exception if any errors occur the execution of the test.
     */
    @Test
    public void givenFindAllUserListsPagedRequest_shouldSucceedWith200() throws Exception {
        int page = 0;
        int size = 2;
        String sortField = "name";
        String sortDirection = "desc";
        mockMvc.perform(get("/lists?page={page}&size={size}&sortField={sortField}" +
                        "&sortDirection={sortDirection}", page, size, sortField, sortDirection)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.userListDTOList").isNotEmpty())
                .andExpect(jsonPath("$._links.first").exists())
                .andExpect(jsonPath("$._links.self").exists())
                .andExpect(jsonPath("$._links.next").exists())
                .andExpect(jsonPath("$._links.last").exists())
                .andExpect(jsonPath("$.page.size").value(size))
                .andExpect(jsonPath("$.page.number").value(page));
    }

    /**
     * Creates a request which fetches {@link UserList} with {@code id = 2L}.
     * The API has to return a 200 Ok status and the requested {@link UserList}.
     *
     * @throws Exception if any errors occur the execution of the test.
     */
    @Test
    public void givenFindUserListByIdRequest_shouldSucceedWith200() throws Exception {
        Long listId = 2L;
        mockMvc.perform(get("/lists/{listId}", listId).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(listId))
                .andExpect(jsonPath("$.userId").value(listId));
    }

    /**
     * Creates a request which fetches {@link UserList} with {@code id = -999L}.
     * The API has to return a 400 Bad Request status and an error message.
     *
     * @throws Exception if any errors occur the execution of the test.
     */
    @Test
    public void givenFindUserListByIdRequest_shouldFailWith400() throws Exception {
        Long listId = -999L;
        mockMvc.perform(get("/lists/{listId}", listId).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Bad Request"))
                .andExpect(jsonPath("$.status").value("400"))
                .andExpect(content().string(containsString("Validation failure")));
    }

    /**
     * Creates a request which fetches {@link UserList} with {@code id = 999L}.
     * The API has to return a 404 Not Found status and an error message.
     *
     * @throws Exception if any errors occur the execution of the test.
     */
    @Test
    public void givenFindUserListByIdRequest_shouldFailWith404() throws Exception {
        Long listId = 999L;
        mockMvc.perform(get("/lists/{listId}", listId).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Not Found"))
                .andExpect(jsonPath("$.status").value("404"))
                .andExpect(content().string(containsString(
                        String.format("UserList with id %s could not be found!", listId))));
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
     * an invalid {@code userId} (999L).
     * The API has to return a 404 Not Found status and an error message.
     *
     * @throws Exception if any errors occur the execution of the test.
     */
    @Test
    public void givenCreateUserListRequest_shouldFailWith404() throws Exception {
        Long userId = 999L;
        UserListCreationDTO newList = new UserListCreationDTO("Cowboy films", "Yeehaw",
                false, userId);

        String userListData = new ObjectMapper().writeValueAsString(newList);

        mockMvc.perform(post("/lists").contentType(MediaType.APPLICATION_JSON).content(userListData))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Not Found"))
                .andExpect(jsonPath("$.status").value("404"))
                .andExpect(content().string(containsString(
                        String.format("User with id %s could not be found!", userId))));
    }

    /**
     * Creates a request which adds a new {@link UserList} to the database. The payload includes
     * an invalid {@code userId} (-999L).
     * The API has to return a 400 Bad Request status and an error message.
     *
     * @throws Exception if any errors occur the execution of the test.
     */
    @Test
    public void givenCreateUserListRequest_shouldFailWith400() throws Exception {
        Long userId = -999L;
        UserListCreationDTO newList = new UserListCreationDTO("Cowboy films", "Yeehaw",
                false, userId);

        String userListData = new ObjectMapper().writeValueAsString(newList);

        mockMvc.perform(post("/lists").contentType(MediaType.APPLICATION_JSON).content(userListData))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Bad Request"))
                .andExpect(jsonPath("$.status").value("400"))
                .andExpect(content().string(containsString("Invalid request content.")));
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
                true, 2L, LocalDateTime.now());
        String userListData = new ObjectMapper().findAndRegisterModules().writeValueAsString(userListDTO);

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
     * Creates a PUT request which updates a {@link UserList} with {@code id = 1L}.
     * The API has to return a 400 Bad Request status since the validation will fail for {@link User} with
     * {@code id = -999L} contained in the request body.
     *
     * @throws Exception if any errors occur the execution of the test.
     */
    @Test
    public void giveUpdateUserListRequest_shouldFailWith400() throws Exception {
        Long userListId = 1L;
        UserListDTO userListDTO = new UserListDTO(2L,"Bad movies", "The worst of all time",
                true, -999L, LocalDateTime.now());
        String userListData = new ObjectMapper().findAndRegisterModules().writeValueAsString(userListDTO);

        mockMvc.perform(put("/lists/{listId}", userListId).contentType(MediaType.APPLICATION_JSON)
                        .content(userListData))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Bad Request"))
                .andExpect(jsonPath("$.status").value("400"))
                .andExpect(content().string(containsString("Validation failure")));
    }

    /**
     * Creates a PUT request which updates a {@link UserList} with {@code id = 99L}.
     * The API has to return a 404 Not Found status since the {@link User} contained in
     * the request does not exist in the database.
     *
     * @throws Exception if any errors occur the execution of the test.
     */
    @Test
    public void giveUpdateUserListRequest_shouldFailWith404() throws Exception {
        Long userListId = 1L;
        UserListDTO userListDTO = new UserListDTO(2L,"Bad movies", "The worst of all time",
                true, 999L, LocalDateTime.now());
        String userListData = new ObjectMapper().findAndRegisterModules().writeValueAsString(userListDTO);

        mockMvc.perform(put("/lists/{listId}", userListId).contentType(MediaType.APPLICATION_JSON)
                        .content(userListData))
                .andDo(log())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Not Found"))
                .andExpect(jsonPath("$.status").value("404"))
                .andExpect(content().string(containsString(
                        String.format("User with id %s could not be found!", 999L))));
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
     * Creates a request which removes {@link UserList} with {@code id = 999L} from the database.
     * The API has to return a 404 Not Found status and an error message.
     *
     * @throws Exception if any errors occur the execution of the test.
     */
    @Test
    public void givenDeleteUserListRequest_shouldFailWith404() throws Exception {
        Long listId = 999L;

        mockMvc.perform(delete("/lists/{listId}", listId).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Not Found"))
                .andExpect(jsonPath("$.status").value("404"))
                .andExpect(content().string(containsString(
                        String.format("UserList with id %s could not be found!", listId))));
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
     * Creates a request which fetches the {@link User} of {@link UserList} with {@code id = 999L}.
     * The API has to return a 404 Not Found status.
     *
     * @throws Exception if any errors occur the execution of the test.
     */
    @Test
    public void givenFindUserByUserList_shouldFailWith404() throws Exception {
        Long userListId = 999L;

        mockMvc.perform(get("/lists/{listId}/user", userListId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Not Found"))
                .andExpect(jsonPath("$.status").value("404"))
                .andExpect(content().string(containsString(
                        String.format("UserList with id %s could not be found!", userListId))));
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
                .andExpect(jsonPath("$._embedded.movieDTOList[*].id", containsInAnyOrder(8,7,9,5,6)));
    }

    /**
     * Creates a request which fetches the {@link Movie} resources associated with
     * {@link UserList} with {@code id = 999L}.
     * The API has to return a 404 Not Found status.
     *
     * @throws Exception if any errors occur the execution of the test.
     */
    @Test
    public void givenFindMoviesByUserList_shouldFailWith404() throws Exception {
        Long userListId = 999L;

        mockMvc.perform(get("/lists/{listId}/movies", userListId).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Not Found"))
                .andExpect(jsonPath("$.status").value("404"))
                .andExpect(content().string(containsString(
                        String.format("UserList with id %s could not be found!", userListId))));
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
                .andExpect(jsonPath("$._embedded.movieDTOList[*].id", hasItem(12)));
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
                .andExpect(jsonPath("$.title").value("Bad Request"))
                .andExpect(jsonPath("$.status").value("400"))
                .andExpect(jsonPath("$.detail").value(containsString(
                        String.format("Movie with id %s already exists in UserList with id %s!",
                                movieId, userListId))));
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
    public void givenRemoveMovieFromUserList_shouldSucceedWith204() throws Exception {
        Long userListId = 2L;
        Long movieId = 8L;

        mockMvc.perform(delete("/lists/{userListId}/movies/{movieId}", userListId, movieId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/lists/{listId}/movies", userListId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.movieDTOList[*].id", not(hasItem(8))));
    }

    /**
     * Create a request which removes a {@link Movie} from {@link UserList} with {@code id = 2L}.
     * The API has to return a 404 Not Found status because the {@link Movie} with {@code id = 999L}
     * does not exist in this {@link UserList}.
     *
     * @throws Exception if any errors occur the execution of the test.
     */
    @Test
    public void givenRemoveMovieFromUserList_shouldFailWith404() throws Exception {
        Long userListId = 2L;
        Long movieId = 999L;

        mockMvc.perform(delete("/lists/{userListId}/movies/{movieId}", userListId, movieId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.title").value("Not Found"))
                .andExpect(jsonPath("$.status").value("404"))
                .andExpect(content().string(containsString(
                        String.format("Movie with id %s was not found in UserList with id %s!", movieId, userListId))));
    }

    /**
     * Creates a request which searches for {@code UserLists} based on parameters
     * {@code name}, {@code description} and {@code userId}.
     * The API has to return a 200 Ok status and a list of {@link UserList} resources.
     *
     * @throws Exception if any errors occur the execution of the test.
     */
    @Test
    public void givenUserListSearchRequest_shouldSucceedWith200AndReturnListOfUserLists() throws Exception {
        String nameSearchTerm = "good m";
        String descriptionSearchTerm = "Gers";
        Long userId = 4L;

        mockMvc.perform(get("/lists/search?name={name}&description={description}&userId={userId}",
                        nameSearchTerm,descriptionSearchTerm,userId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.userListDTOList[*].name", everyItem(containsStringIgnoringCase(nameSearchTerm))))
                .andExpect(jsonPath("$._embedded.userListDTOList[*].description", everyItem(containsStringIgnoringCase(descriptionSearchTerm))))
                .andExpect(jsonPath("$._embedded.userListDTOList[*].userId", everyItem(comparesEqualTo(4))));
    }

    /**
     * Creates a request which searches for {@code UserLists} based on parameters
     * {@code name}, {@code description} and {@code userId}.
     * The API has to return a 200 Ok status and an empty list.
     *
     * @throws Exception if any errors occur the execution of the test.
     */
    @Test
    public void givenUserListSearchRequest_shouldSucceedWith200AndReturnEmptyList() throws Exception {
        String actorName = "Oscar nominated movies";

        mockMvc.perform(get("/lists/search?name={name}",
                        actorName)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(is("{}")));
    }
}
