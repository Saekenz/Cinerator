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
import static org.hamcrest.Matchers.matchesPattern;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
                .andExpect(content().string(containsString("{\"userList\":")))
                .andExpect(status().isOk());
    }

    @WithMockUser("test-user")
    @Test
    public void givenFindUserByIdRequest_shouldSucceedWith200() throws Exception {
        Long user_id = 1L;
        mockMvc.perform(get("/users/{user_id}", user_id).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString(String.format("\"user_id\":%s",user_id))));
    }

    @WithMockUser("test-user")
    @Test
    public void givenFindUserByIdRequest_shouldFailWith404() throws Exception {
        Long user_id = -999L;
        mockMvc.perform(get("/users/{user_id}", user_id).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString(String.format("user: %s", user_id))));
    }

    @WithMockUser("test-user")
    @Test
    public void givenFindUsersByUsernameRequest_shouldSucceedWith200() throws Exception {
        String username = "UserD";
        mockMvc.perform(get("/users/username/{username}", username).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString(String.format("\"username\":\"%s\"",username))));
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
                .andExpect(content().string(matchesPattern("(?i).*\"role\":\"" + role + "\".*")));
    }

    @WithMockUser("test-user")
    @Test
    public void givenFindUsersByRoleRequest_shouldFailWith404() throws Exception {
        String role = "wizard";
        mockMvc.perform(get("/users/role/{role}", role).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString(String.format("role: %s", role))));
    }
}
