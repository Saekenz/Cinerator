package at.saekenz.cinerator.controller;

import at.saekenz.cinerator.model.actor.Actor;
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

import java.time.LocalDate;

import static org.hamcrest.Matchers.*;
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
        mockMvc.perform(get("/actors/{actorId}", actorId).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(actorId));
    }

    @Test
    public void givenFindActorByIdRequest_shouldFailWith404() throws Exception {
        Long actorId = -999L;
        mockMvc.perform(get("/actors/{actorId}", actorId).contentType(MediaType.APPLICATION_JSON))
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
        mockMvc.perform(get("/actors/birthDate/{birthDate}", birthday).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.actorList[*].birthDate", everyItem(containsStringIgnoringCase(birthday.toString()))));
    }

    @Test
    public void givenFindActorsByBirthDateRequest_shouldFailWith404() throws Exception {
        LocalDate birthday = LocalDate.now();
        mockMvc.perform(get("/actors/birthDate/{birthDate}", birthday).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString(String.format("birthDate: %s", birthday))));
    }

    @Test
    public void givenFindActorsByBirthCountryRequest_shouldSucceedWith200() throws Exception {
        String country = "Spain";
        mockMvc.perform(get("/actors/birthCountry/{birthCountry}", country).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.actorList[*].birthCountry", everyItem(containsStringIgnoringCase(country))));
    }

    @Test
    public void givenFindActorsByBirthCountryRequest_shouldFailWith404() throws Exception {
        String country = "Wonderland";
        mockMvc.perform(get("/actors/birthCountry/{birthCountry}", country).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString(String.format("birthCountry: %s", country))));
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
        String actorData = om.writeValueAsString(actor);

        mockMvc.perform(post("/actors").contentType(MediaType.APPLICATION_JSON).content(actorData))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value(actor.getName()))
                .andExpect(jsonPath("$.birthDate").value(actor.getBirthDate().toString()))
                .andExpect(jsonPath("$.birthCountry").value(actor.getBirthCountry()))
                .andExpect(jsonPath("$.age").value(actor.getAge()));
    }

    /**
     * Creates a request for adding a new {@link Actor} that is missing the 'birthDate' property.
     * The request has to return HTTP code 400 since this property must not be null.
     * @throws Exception if any errors occur the execution of the test.
     */
    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void givenCreateActorRequest_shouldFailWith400() throws Exception {
        String actorJsonData = """
                {
                  "name": "Scarlett Johansson",
                  "birthCountry": "United States",
                  "age": 39
                }""";

        mockMvc.perform(post("/actors").contentType(MediaType.APPLICATION_JSON).content(actorJsonData))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("\"description\":\"birth_date\\\" of relation \\\"actors\"")));
    }

    /**
     * Creates a request to update an {@link Actor}.
     * The {@link Actor} will be created instead of updated since it is not yet stored in the database.
     * The request has to return HTTP code 201.
     * @throws Exception if any errors occur the execution of the test.
     */
    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void givenUpdateActorRequest_shouldSucceedWith201() throws Exception {
        Long oldActorId = 99L; // id not yet stored in the database
        Actor actor = new Actor("Brad Pitt",LocalDate.of(1963,12,18),"United States");
        ObjectMapper om = new ObjectMapper().findAndRegisterModules();
        String actorData = om.writeValueAsString(actor);

        mockMvc.perform(put("/actors/{actorId}",oldActorId).contentType(MediaType.APPLICATION_JSON)
                        .content(actorData))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value(actor.getName()))
                .andExpect(jsonPath("$.birthDate").value(actor.getBirthDate().toString()))
                .andExpect(jsonPath("$.birthCountry").value(actor.getBirthCountry()))
                .andExpect(jsonPath("$.age").value(actor.getAge()));
    }

    /**
     * Creates a request to update a {@link Actor}.
     * The {@link Actor} will be updated since it is already stored in the database.
     * The request has to return HTTP code 204.
     * @throws Exception if any errors occur the execution of the test.
     */
    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void givenUpdateActorRequest_shouldSucceedWith204() throws Exception {
        Long oldActorId = 5L; // replace "Cate Blanchett"
        Actor actor = new Actor("Brad Pitt",LocalDate.of(1963,12,18),"United States");
        ObjectMapper om = new ObjectMapper().findAndRegisterModules();
        String actorData = om.writeValueAsString(actor);

        mockMvc.perform(put("/actors/{actorId}",oldActorId).contentType(MediaType.APPLICATION_JSON)
                        .content(actorData))
                .andExpect(status().isNoContent());
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void givenDeleteActorRequest_shouldSucceedWith204() throws Exception {
        Long actorId = 4L;
        mockMvc.perform(delete("/actors/{actorId}",actorId).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    public void givenDeleteActorRequest_shouldFailWith404() throws Exception {
        Long actorId = -999L;
        mockMvc.perform(delete("/actors/{actorId}",actorId).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void givenActorSearchRequest_shouldSucceedWith200AndReturnListOfActors() throws Exception {
        Actor actor = new Actor("Daniel Day-Lewis", LocalDate.of(1957, 4, 29), "United Kingdom");
        mockMvc.perform(get("/actors/search?name={name}&birthDate={birthDate}&birthCountry={birthCountry}&age={age}",
                actor.getName(),actor.getBirthDate(),actor.getBirthCountry(), actor.getAge())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.actorList[*].name", everyItem(containsStringIgnoringCase(actor.getName()))))
                .andExpect(jsonPath("$._embedded.actorList[*].birthDate", everyItem(containsString(actor.getBirthDate().toString()))))
                .andExpect(jsonPath("$._embedded.actorList[*].birthCountry", everyItem(containsStringIgnoringCase(actor.getBirthCountry()))))
                .andExpect(jsonPath("$._embedded.actorList[*].age", everyItem(comparesEqualTo(actor.getAge()))))
                .andDo(print());
    }

    @Test
    public void givenActorSearchRequest_shouldSucceedWith200AndReturnEmptyList() throws Exception {
        String actorName = "Brad Pitt";

        mockMvc.perform(get("/actors/search?name={name}",
                        actorName)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(is("{}")));
    }

    @Test
    public void givenFindMoviesByIdRequest_shouldSucceedWith200AndReturnListOfMovies() throws Exception {
        long actorId = 2L;
        mockMvc.perform(get("/actors/{actorId}/movies",actorId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void givenFindMoviesByIdRequest_shouldSucceedWith200AndReturnEmptyList() throws Exception {
        Actor newActor = new Actor("Scarlett Johansson",
                LocalDate.of(1984, 11, 22),
                "United States");
        Long actorId = 6L;
        ObjectMapper om = new ObjectMapper().findAndRegisterModules();
        String actorData = om.writeValueAsString(newActor);

        mockMvc.perform(post("/actors").contentType(MediaType.APPLICATION_JSON).content(actorData))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/actors/{actorId}/movies",actorId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(is("{}")));
    }

    @Test
    public void givenFindMoviesByIdRequest_shouldFailWith404() throws Exception {
        Long actorId = -999L;
        mockMvc.perform(get("/actors/{actorId}/movies",actorId).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString(String.format("actor with id: %s", actorId))));
    }

    @Test
    public void givenFindMovieByIdRequest_shouldSucceedWith200() throws Exception {
        Long actorId = 2L;
        Long movieId = 2L;

        mockMvc.perform(get("/actors/{actorId}/movies/{movieId}",actorId,movieId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(movieId));
    }

    @Test
    public void givenFindMovieByIdRequest_shouldFailWith404() throws Exception {
        Long actorId = -999L;
        Long movieId = 2L;

        mockMvc.perform(get("/actors/{actorId}/movies/{movieId}",actorId,movieId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString(String.format("actor with id: %s", actorId))));
    }
}

