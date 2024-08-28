package at.saekenz.cinerator.controller;

import at.saekenz.cinerator.model.actor.Actor;
import com.fasterxml.jackson.databind.JsonNode;
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
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDate;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertTrue;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ActorControllerSpringBootIntegrationTest {

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

    @Test
    public void givenFindAllActorsRequest_shouldSucceedWith200() throws Exception {
        mockMvc.perform(get("/actors").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.actorList").isNotEmpty());
    }

    @Test
    public void givenFindActorByIdRequest_shouldSucceedWith200() throws Exception {
        Long actorId = 1L;
        mockMvc.perform(get("/actors/{actor_id}", actorId).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.actor_id").value(actorId));
    }

    @Test
    public void givenFindActorByIdRequest_shouldFailWith404() throws Exception {
        Long actorId = -999L;
        mockMvc.perform(get("/actors/{actor_id}", actorId).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void givenFindActorsByNameRequest_shouldSucceedWith200() throws Exception {
        String name = "Leonardo DiCaprio";
        mockMvc.perform(get("/actors/name/{name}", name).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.actorList[*].name", everyItem(containsStringIgnoringCase(name))));
    }

    @Test
    public void givenFindActorsByNameRequest_shouldFailWith404() throws Exception {
        String name = "Lionel Messi";
        mockMvc.perform(get("/actors/name/{name}", name).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString(String.format("name: %s", name))));
    }

    @Test
    public void givenFindActorsByBirthDateRequest_shouldSucceedWith200() throws Exception {
        LocalDate birthday = LocalDate.of(1974, 4, 28); // Penelope Cruz
        mockMvc.perform(get("/actors/birth_date/{birth_date}", birthday).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.actorList[*].birth_date", everyItem(containsStringIgnoringCase(birthday.toString()))));
    }

    @Test
    public void givenFindActorsByBirthDateRequest_shouldFailWith404() throws Exception {
        LocalDate birthday = LocalDate.now();
        mockMvc.perform(get("/actors/birth_date/{birth_date}", birthday).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString(String.format("birth_date: %s", birthday))));
    }

    @Test
    public void givenFindActorsByBirthCountryRequest_shouldSucceedWith200() throws Exception {
        String country = "Spain";
        mockMvc.perform(get("/actors/birth_country/{birth_country}", country).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.actorList[*].birth_country", everyItem(containsStringIgnoringCase(country))));
    }

    @Test
    public void givenFindActorsByBirthCountryRequest_shouldFailWith404() throws Exception {
        String country = "Wonderland";
        mockMvc.perform(get("/actors/birth_country/{birth_country}", country).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString(String.format("birth_country: %s", country))));
    }

    @Test
    public void givenFindActorsByAgeRequest_shouldSucceedWith200() throws Exception {
        int age = 50;
        mockMvc.perform(get("/actors/age/{age}", age).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.actorList[*].age", everyItem(comparesEqualTo(age))));
    }

    @Test
    public void givenFindActorsByAgeRequest_shouldFailWith404() throws Exception {
        int age = 399;
        mockMvc.perform(get("/actors/age/{age}", age).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString(String.format("age: %s", age))));
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void givenCreateActorRequest_shouldSucceedWith201() throws Exception {
        Actor actor = new Actor("Brad Pitt",LocalDate.of(1963,12,18),"United States");
        ObjectMapper om = new ObjectMapper().findAndRegisterModules();
        String actor_data = om.writeValueAsString(actor);

        mockMvc.perform(post("/actors").contentType(MediaType.APPLICATION_JSON).content(actor_data))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value(actor.getName()))
                .andExpect(jsonPath("$.birth_date").value(actor.getBirth_date().toString()))
                .andExpect(jsonPath("$.birth_country").value(actor.getBirth_country()))
                .andExpect(jsonPath("$.age").value(actor.getAge()));
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void givenUpdateActorRequest_shouldSucceedWith201() throws Exception {
        Long old_actor_id = 5L; // replace "Cate Blanchett"
        Actor actor = new Actor("Brad Pitt",LocalDate.of(1963,12,18),"United States");
        ObjectMapper om = new ObjectMapper().findAndRegisterModules();
        String actor_data = om.writeValueAsString(actor);

        mockMvc.perform(put("/actors/{actor_id}",old_actor_id).contentType(MediaType.APPLICATION_JSON)
                        .content(actor_data))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value(actor.getName()))
                .andExpect(jsonPath("$.birth_date").value(actor.getBirth_date().toString()))
                .andExpect(jsonPath("$.birth_country").value(actor.getBirth_country()))
                .andExpect(jsonPath("$.age").value(actor.getAge()));
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void givenDeleteActorRequest_shouldSucceedWith204() throws Exception {
        Long actor_id = 4L;
        mockMvc.perform(delete("/actors/{actor_id}",actor_id).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    public void givenDeleteActorRequest_shouldFailWith404() throws Exception {
        Long actor_id = -999L;
        mockMvc.perform(delete("/actors/{actor_id}",actor_id).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void givenActorSearchRequest_shouldSucceedWith200() throws Exception {
        Actor actor = new Actor("Daniel Day-Lewis", LocalDate.of(1957, 4, 29), "United Kingdom");
        mockMvc.perform(get("/actors/search?name={name}&birth_date={birth_date}&birth_country={birth_country}&age={age}",
                actor.getName(),actor.getBirth_date(),actor.getBirth_country(), actor.getAge())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.actorList[*].name", everyItem(containsStringIgnoringCase(actor.getName()))))
                .andExpect(jsonPath("$._embedded.actorList[*].birth_date", everyItem(containsString(actor.getBirth_date().toString()))))
                .andExpect(jsonPath("$._embedded.actorList[*].birth_country", everyItem(containsStringIgnoringCase(actor.getBirth_country()))))
                .andExpect(jsonPath("$._embedded.actorList[*].age", everyItem(comparesEqualTo(actor.getAge()))))
                .andDo(print());
    }

    @Test
    public void givenFindMoviesByIdRequest_shouldSucceedWith200() throws Exception {
        long actor_id = 2L;
        MvcResult result = mockMvc.perform(get("/actors/{actor_id}/movies",actor_id)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        String jsonResponse = result.getResponse().getContentAsString();
        JsonNode rootNode = new ObjectMapper().readTree(jsonResponse);

        JsonNode movieList = rootNode.path("_embedded").path("movieList");
        boolean containsActor = false;

        for (JsonNode movie : movieList) {
            JsonNode actors = movie.path("actors");

            for (JsonNode actor : actors) {
                if (actor.path("actor_id").asLong() == actor_id) {
                    containsActor = true;
                    break;
                }
            }
            if (!containsActor) { break; }
        }

        assertTrue(String.format("Actor with actor_id = %s not present in all movies!", actor_id), containsActor);
    }

    @Test
    public void givenFindMoviesByIdRequest_shouldFailWith404() throws Exception {
        Long actor_id = -999L;
        mockMvc.perform(get("/actors/{actor_id}/movies",actor_id).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString(String.format("actor with id: %s", actor_id))));
    }
}

