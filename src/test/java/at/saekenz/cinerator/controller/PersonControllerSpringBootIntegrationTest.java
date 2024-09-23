package at.saekenz.cinerator.controller;

import at.saekenz.cinerator.model.country.Country;
import at.saekenz.cinerator.model.country.CountryDTO;
import at.saekenz.cinerator.model.movie.Movie;
import at.saekenz.cinerator.model.person.Person;
import at.saekenz.cinerator.model.person.PersonDTO;
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
import java.time.Period;

import static org.hamcrest.Matchers.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class PersonControllerSpringBootIntegrationTest {

    @Autowired
    WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(webApplicationContext)
                .apply(springSecurity())
                .build();
    }

    /**
     * Creates a request which retrieves every {@link Person} stored in the database (in a paged format).
     * The API has to return a 200 Ok status and a collection of all {@link Person} objects.
     *
     * @throws Exception if any errors occur the execution of the test.
     */
    @Test
    public void givenFindAllPersonsPagedRequest_shouldSucceedWith200() throws Exception {
        int page = 3;
        int size = 3;
        String sortField = "birthDate";
        String sortDirection = "desc";

        mockMvc.perform(get("/persons?page={page}&size={size}&sortField={sortField}" +
                                "&sortDirection={sortDirection}",
                        page, size, sortField, sortDirection)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.personDTOList").isNotEmpty())
                .andExpect(jsonPath("$._links.first").exists())
                .andExpect(jsonPath("$._links.self").exists())
                .andExpect(jsonPath("$._links.next").exists())
                .andExpect(jsonPath("$._links.last").exists())
                .andExpect(jsonPath("$.page.size").value(size))
                .andExpect(jsonPath("$.page.number").value(page));
    }

    /**
     * Creates a request which fetches {@link Person} with {@code id = 2L}.
     * The API has to return a 200 Ok status and the requested {@link Person}.
     *
     * @throws Exception if any errors occur the execution of the test.
     */
    @Test
    public void givenFindPersonByIdRequest_shouldSucceedWith200() throws Exception {
        Long personId = 2L;

        mockMvc.perform(get("/persons/{id}", personId).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(personId));
    }

    /**
     * Creates a request which fetches {@link Person} with {@code id = -999L}.
     * The API has to return a 404 Not Found status and an error message.
     *
     * @throws Exception if any errors occur the execution of the test.
     */
    @Test
    public void givenFindPersonByIdRequest_shouldFailWith404() throws Exception {
        Long personId = -999L;

        mockMvc.perform(get("/persons/{id}", personId).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString(
                        String.format("Person with id %s could not be found!", personId))));
    }

// ---------------------------------- CREATE/UPDATE/DELETE ------------------------------------------------------------

    /**
     * Creates a request which adds a new {@link Person} to the database.
     * The API has to return a 201 Created and the created {@link Person}.
     *
     * @throws Exception if any errors occur the execution of the test.
     */
    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void givenCreatePersonRequest_shouldSucceedWith201() throws Exception {
        Long countryId = 1L;
        LocalDate newPersonBirthDate = LocalDate.of(1949,12,4);
        PersonDTO newPerson = new PersonDTO();
        newPerson.setName("Jeff Bridges");
        newPerson.setBirthDate(newPersonBirthDate);
        newPerson.setDeathDate(null);
        newPerson.setHeight("1.85 m (6'1'')");
        newPerson.setBirthCountry(new CountryDTO(countryId, "United States"));

        String requestBody = new ObjectMapper().findAndRegisterModules().writeValueAsString(newPerson);

        mockMvc.perform(post("/persons").contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value(newPerson.getName()))
                .andExpect(jsonPath("$.birthDate").value(newPersonBirthDate.toString()))
                .andExpect(jsonPath("$.age").value(Period.between(newPersonBirthDate,
                        LocalDate.now()).getYears()))
                .andExpect(jsonPath("$.height").value(newPerson.getHeight()))
                .andExpect(jsonPath("$.birthCountry.name").value("United States"));
    }

    /**
     * Creates a request which adds a new {@link Person} to the database. The payload includes
     * an invalid {@code name} (empty String).
     * The API has to return a 400 Bad Request status and an error message.
     *
     * @throws Exception if any errors occur the execution of the test.
     */
    @Test
    public void givenCreatePersonRequest_shouldFailWith400() throws Exception {
        Long countryId = 1L;
        LocalDate newPersonBirthDate = LocalDate.of(1949,12,4);
        PersonDTO newPerson = new PersonDTO();
        newPerson.setName(" "); // set Name to empty String -> should fail validation
        newPerson.setBirthDate(newPersonBirthDate);
        newPerson.setDeathDate(null);
        newPerson.setHeight("1.85 m (6'1'')");
        newPerson.setBirthCountry(new CountryDTO(countryId, "United States"));

        String requestBody = new ObjectMapper().findAndRegisterModules().writeValueAsString(newPerson);

        mockMvc.perform(post("/persons").contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isBadRequest());
    }

    /**
     * Creates a PUT request which updates {@link Person} with {@code id = 2L}.
     * The API has to return a 204 No Content status and a link to the updated resource in
     * its location header. To check if the update was successful, a second request is made which
     * fetches the updated {@link Person} via HTTP GET.
     *
     * @throws Exception if any errors occur the execution of the test.
     */
    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void givenUpdatePersonByIdRequest_shouldSucceedWith200() throws Exception {
        Long personId = 2L;
        PersonDTO updatedPerson = new PersonDTO(2L,
                "Vincent Cassel",
                LocalDate.of(1966,11,23),
                null,
                "1.87 m (6'1.5'')",
                new CountryDTO(2L, "France"));

        String requestBody = new ObjectMapper().findAndRegisterModules().writeValueAsString(updatedPerson);

        mockMvc.perform(put("/persons/{id}", personId).contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isNoContent())
                .andExpect(header().stringValues("Location", "http://localhost/persons/2"));

        mockMvc.perform(get("/persons/{id}", personId).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(updatedPerson.getName()))
                .andExpect(jsonPath("$.birthDate").value(updatedPerson.getBirthDate().toString()))
                .andExpect(jsonPath("$.age").value(Period.between(updatedPerson.getBirthDate(),
                        LocalDate.now()).getYears()))
                .andExpect(jsonPath("$.height").value(updatedPerson.getHeight()))
                .andExpect(jsonPath("$.birthCountry.name").value("France"));
    }

    /**
     * Creates a PUT request which updates a {@link Person} with {@code id = 2L}.
     * The API has to return a 400 Bad Request status.
     *
     * @throws Exception if any errors occur the execution of the test.
     */
    @Test
    public void givenUpdatePersonByIdRequest_shouldFailWith400() throws Exception {
        Long personId = 2L;
        PersonDTO updatedPerson = new PersonDTO(2L,
                " ", // set Name to empty String -> should fail validation
                LocalDate.of(1966,11,23),
                null,
                "1.87 m (6'1.5'')",
                new CountryDTO(2L, "France"));

        String requestBody = new ObjectMapper().findAndRegisterModules().writeValueAsString(updatedPerson);

        mockMvc.perform(put("/persons/{id}", personId).contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest());
    }

    /**
     * Creates a request which removes {@link Person} with {@code id = 1L} from the database.
     * The API has to return a 204 No Content status.
     *
     * @throws Exception if any errors occur the execution of the test.
     */
    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void givenDeletePersonByIdRequest_shouldSucceedWith204() throws Exception {
        Long personId = 1L;

        mockMvc.perform(delete("/persons/{id}", personId).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/persons/{id}", personId).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString(
                        String.format("Person with id %s could not be found!", personId))));
    }

    /**
     * Creates a request which removes {@link Person} with {@code id = -999L} from the database.
     * The API has to return a 404 Not Found status and an error message.
     *
     * @throws Exception if any errors occur the execution of the test.
     */
    @Test
    public void givenDeletePersonByIdRequest_shouldFailWith404() throws Exception {
        Long personId = -999L;

        mockMvc.perform(delete("/persons/{id}", personId).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString(
                        String.format("Person with id %s could not be found!", personId))));
    }

// ---------------------------------------- SEARCH -----------------------------------------------------------------

    /**
     * Creates a request which fetches {@link Person} resources where the {@code height} property
     * is equal/contains the {@link String} {@code 1.83}.
     * The API has to return a 200 Ok status and all {@link Person} resources that satisfy
     * this condition.
     *
     * @throws Exception if any errors occur the execution of the test.
     */
    @Test
    public void givenPersonSearchByHeightRequest_shouldSucceedWith200() throws Exception {
        String height = "1.83";

        mockMvc.perform(get("/persons/search?height={height}", height)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.personDTOList").isNotEmpty())
                .andExpect(jsonPath("$._embedded.personDTOList[*].height",
                        everyItem(containsString(height))));
    }

    /**
     * Creates a request which fetches {@link Person} resources where the {@code age} property
     * is equal to the {@link Integer} {@code 55}.
     * The API has to return a 200 Ok status and all {@link Person} resources that satisfy
     * this condition.
     *
     * @throws Exception if any errors occur the execution of the test.
     */
    @Test
    public void givenPersonSearchByAgeRequest_shouldSucceedWith200() throws Exception {
        int age = 55;

        mockMvc.perform(get("/persons/search?age={age}", age)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.personDTOList").isNotEmpty())
                .andExpect(jsonPath("$._embedded.personDTOList[*].age", everyItem(comparesEqualTo(age))));
    }

    /**
     * Creates a request which fetches {@link Person} resources where the {@code name} property
     * contains the {@link String} {@code "al"} (e.g. Al Pacino).
     * The API has to return a 200 Ok status and all {@link Person} resources that satisfy
     * this condition.
     *
     * @throws Exception if any errors occur the execution of the test.
     */
    @Test
    public void givenPersonSearchByNameRequest_shouldSucceedWith200() throws Exception {
        String name = "al";

        mockMvc.perform(get("/persons/search?name={name}", name)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.personDTOList").isNotEmpty())
                .andExpect(jsonPath("$._embedded.personDTOList[*].name",
                        everyItem(containsStringIgnoringCase(name))));
    }

    /**
     * Creates a request which fetches {@link Person} resources where the {@code birthCountry} property
     * is equal/contains the {@link String} {@code united kingdom}.
     * The API has to return a 200 Ok status and all {@link Person} resources that satisfy
     * this condition.
     *
     * @throws Exception if any errors occur the execution of the test.
     */
    @Test
    public void givenPersonSearchByBirthCountryRequest_shouldSucceedWith200() throws Exception {
        String birthCountry = "united kingdom";

        mockMvc.perform(get("/persons/search?country={birthCountry}", birthCountry)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$._embedded.personDTOList").isNotEmpty())
                .andExpect(jsonPath("$._embedded.personDTOList[*].birthCountry.name",
                        everyItem(containsStringIgnoringCase(birthCountry))));
    }

    /**
     * Creates a request which fetches {@link Person} resources where the {@code height} property
     * is equal/contains the {@link String} {@code 1.82} and the {@code birthCountry} property
     * is equal/contains the {@link String} {@code united states}.
     * The API has to return a 200 Ok status and all {@link Person} resources that satisfy
     * these conditions.
     *
     * @throws Exception if any errors occur the execution of the test.
     */
    @Test
    public void givenPersonSearchByHeightAndBirthCountry_shouldSucceedWith200() throws Exception {
        String height = "1.82";
        String birthCountry = "united states";

        mockMvc.perform(get("/persons/search?height={height}&country={birthCountry}", height, birthCountry)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.personDTOList").isNotEmpty())
                .andExpect(jsonPath("$._embedded.personDTOList[*].height", everyItem(containsString(height))))
                .andExpect(jsonPath("$._embedded.personDTOList[*].birthCountry.name",
                        everyItem(containsStringIgnoringCase(birthCountry))));
    }

// ---------------------------------------- OTHER --------------------------------------------------------------------

    /**
     * Creates a request which fetches the {@link Country} resource that acts as the birth country of
     * the {@link Person} with {@code id 15}.
     * The API has to return a 200 Ok status and the respective {@link Country} resource.
     *
     * @throws Exception if any errors occur the execution of the test.
     */
    @Test
    public void givenFindBirthCountryByPersonIdRequest_shouldSucceedWith200() throws Exception {
        Long personId = 15L;

        mockMvc.perform(get("/persons/{id}/country", personId).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").isNotEmpty());
    }

    /**
     * Creates a request which fetches the {@link Country} resource that acts as the birth country of
     * the {@link Person} with {@code id -999}.
     * The API has to return a 404 Not Found status since no {@link Person} exists with this {@code id}.
     *
     * @throws Exception if any errors occur the execution of the test.
     */
    @Test
    public void givenFindBirthCountryByPersonIdRequest_shouldFailWith404() throws Exception {
        Long personId = -999L;
        String errorMessage = String.format("Person with id %s could not be found!", personId);

        mockMvc.perform(get("/persons/{id}/country", personId).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Not Found"))
                .andExpect(jsonPath("$.status").value("404"))
                .andExpect(jsonPath("$.detail").value(errorMessage));
    }

    /**
     * Creates a request which fetches {@link Movie} resources where {@link Person} with {@code id 22}
     * had an {@code acting} role. Also creates a second request which fetches {@link Movie} resources where
     * the same {@link Person} was credited as {@code director}.
     * The API has to return a 200 Ok status and all {@link Movie} resources that match the respective criteria.
     *
     * @throws Exception if any errors occur the execution of the test.
     */
    @Test
    public void givenFindMoviesByPersonAndRoleRequest_shouldSucceedWith200() throws Exception {
        Long personId = 22L;
        String personName = "Robert De Niro";
        String role = "actor";

        mockMvc.perform(get("/persons/{id}/movies?role={role}", personId, role)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.movieDTOList").isNotEmpty());

        role = "director";

        mockMvc.perform(get("/persons/{id}/movies?role={role}", personId, role)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.movieDTOList").isNotEmpty())
                .andExpect(jsonPath("$._embedded.movieDTOList[*].director",
                        everyItem(containsStringIgnoringCase(personName))));
    }

    /**
     * Creates a request which fetches {@link Movie} resources where {@link Person} with {@code id -999}
     * had an {@code acting} role.
     * The API has to return a 404 Not Found status since no {@link Person} exists for this {@code id}.
     *
     * @throws Exception if any errors occur the execution of the test.
     */
    @Test
    public void givenFindMoviesByPersonAndRoleRequest_shouldFailWith404() throws Exception {
        Long personId = -999L;
        String role = "actor";
        String errorMessage = String.format("Person with id %s could not be found!", personId);

        mockMvc.perform(get("/persons/{id}/movies?role={role}", personId, role)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Not Found"))
                .andExpect(jsonPath("$.status").value("404"))
                .andExpect(jsonPath("$.detail").value(errorMessage));
    }
}
