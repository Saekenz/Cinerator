package at.saekenz.cinerator.controller;

import at.saekenz.cinerator.model.userlist.UserList;
import at.saekenz.cinerator.model.userlist.UserListCreationDTO;
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

import static org.hamcrest.Matchers.containsString;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
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
}
