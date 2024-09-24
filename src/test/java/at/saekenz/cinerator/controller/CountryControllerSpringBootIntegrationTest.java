package at.saekenz.cinerator.controller;

import at.saekenz.cinerator.model.country.Country;
import at.saekenz.cinerator.model.country.CountryDTO;
import at.saekenz.cinerator.model.movie.Movie;
import at.saekenz.cinerator.model.person.Person;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CountryControllerSpringBootIntegrationTest {

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
     * Creates a request which retrieves every {@link Country} stored in the database (in a paged format).
     * The API has to return a 200 Ok status and a collection of all {@link Country} objects.
     *
     * @throws Exception if any errors occur the execution of the test.
     */
    @Test
    public void givenFindAllCountriesPagedRequest_shouldSucceedWith200() throws Exception {
        int page = 1;
        int size = 4;
        String sortField = "name";
        String sortDirection = "desc";

        mockMvc.perform(get("/countries?page={page}&size={size}&sortField={sortField}" +
                                "&sortDirection={sortDirection}",
                        page, size, sortField, sortDirection)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.countryDTOList").isNotEmpty())
                .andExpect(jsonPath("$._links.first").exists())
                .andExpect(jsonPath("$._links.self").exists())
                .andExpect(jsonPath("$._links.next").exists())
                .andExpect(jsonPath("$._links.last").exists())
                .andExpect(jsonPath("$.page.size").value(size))
                .andExpect(jsonPath("$.page.number").value(page));
    }

    /**
     * Creates a request which fetches {@link Country} with {@code id = 2L}.
     * The API has to return a 200 Ok status and the requested {@link Country}.
     *
     * @throws Exception if any errors occur the execution of the test.
     */
    @Test
    public void givenFindCountryByIdRequest_shouldSucceedWith200() throws Exception {
        Long countryId = 2L;

        mockMvc.perform(get("/countries/{id}", countryId).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(countryId));
    }

    /**
     * Creates a request which fetches {@link Country} with {@code id = -999L}.
     * The API has to return a 404 Not Found status and an error message.
     *
     * @throws Exception if any errors occur the execution of the test.
     */
    @Test
    public void givenFindCountryByIdRequest_shouldFailWith404() throws Exception {
        Long countryId = -999L;

        mockMvc.perform(get("/countries/{id}", countryId).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Not Found"))
                .andExpect(jsonPath("$.status").value("404"))
                .andExpect(content().string(containsString(
                        String.format("Country with id %s could not be found!", countryId))));
    }

// ---------------------------------- CREATE/UPDATE/DELETE ------------------------------------------------------------

    /**
     * Creates a request which adds a new {@link Country} to the database.
     * The API has to return a 201 Created and the created {@link Country}.
     *
     * @throws Exception if any errors occur the execution of the test.
     */
    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void givenCreateCountryRequest_shouldSucceedWith201() throws Exception {
        Long countryId = 1L;
        CountryDTO newCountry = new CountryDTO(countryId, "Panem");
        String requestBody = new ObjectMapper().writeValueAsString(newCountry);

        mockMvc.perform(post("/countries").contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value(newCountry.name()));
    }

    /**
     * Creates a request which adds a new {@link Country} to the database. The payload includes
     * an invalid {@code name} (empty String).
     * The API has to return a 400 Bad Request status and an error message.
     *
     * @throws Exception if any errors occur the execution of the test.
     */
    @Test
    public void givenCreateCountryRequest_shouldFailWith400() throws Exception {
        Long countryId = 1L;
        CountryDTO newCountry = new CountryDTO(countryId, "  ");
        String requestBody = new ObjectMapper().writeValueAsString(newCountry);

        mockMvc.perform(post("/countries").contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest());
    }

    /**
     * Creates a PUT request which updates {@link Country} with {@code id = 4L}.
     * The API has to return a 204 No Content status and a link to the updated resource in
     * its location header. To check if the update was successful, a second request is made which
     * fetches the updated {@link Country} via HTTP GET.
     *
     * @throws Exception if any errors occur the execution of the test.
     */
    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void givenUpdateCountryByIdRequest_shouldSucceedWith200() throws Exception {
        Long countryId = 4L;
        CountryDTO updatedCountry = new CountryDTO(countryId, "Belgium");
        String requestBody = new ObjectMapper().writeValueAsString(updatedCountry);

        mockMvc.perform(put("/countries/{id}", countryId).contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isNoContent())
                .andExpect(header().stringValues("Location", "http://localhost/countries/4"));

        mockMvc.perform(get("/countries/{id}", countryId).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(updatedCountry.name()))
                .andExpect(jsonPath("$.id").value(countryId.toString()));
    }

    /**
     * Creates a PUT request which updates a {@link Country} with {@code id = 4L}.
     * The API has to return a 400 Bad Request status.
     *
     * @throws Exception if any errors occur the execution of the test.
     */
    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    public void givenUpdateCountryByIdRequest_shouldFailWith400() throws Exception {
        Long countryId = 4L;
        CountryDTO updatedCountry = new CountryDTO(countryId, " ");
        String requestBody = new ObjectMapper().writeValueAsString(updatedCountry);

        mockMvc.perform(put("/countries/{id}", countryId).contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest());
    }


// ---------------------------------------- OTHER --------------------------------------------------------------------

    /**
     * Creates a request which fetches {@link Person} resources that were born in {@link Country} with {@code id 1}.
     * The API has to return a 200 Ok status and all {@link Person} resources that match this condition.
     *
     * @throws Exception if any errors occur the execution of the test.
     */
    @Test
    public void givenFindPersonsByCountryIdRequest_shouldSucceedWith200() throws Exception {
        Long countryId = 1L;

        mockMvc.perform(get("/countries/{id}/persons", countryId).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.personDTOList[*].birthCountry.name",
                        everyItem(comparesEqualTo("United States"))));
    }

    /**
     * Creates a request which fetches {@link Person} resources that were born in {@link Country} with {@code id -999}.
     * The API has to return a 404 Not Found status since no {@link Country} exists with this {@code id}.
     *
     * @throws Exception if any errors occur the execution of the test.
     */
    @Test
    public void givenFindPersonsByCountryIdRequest_shouldFailWith404() throws Exception {
        Long countryId = -999L;

        mockMvc.perform(get("/countries/{id}/persons", countryId).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Not Found"))
                .andExpect(jsonPath("$.status").value("404"))
                .andExpect(content().string(containsString(
                        String.format("Country with id %s could not be found!", countryId))));
    }

    /**
     * Creates a request which fetches {@link Movie} resources that are associated with {@link Country}
     * with {@code id 1}.
     * The API has to return a 200 Ok status and all {@link Movie} resources that match this condition.
     *
     * @throws Exception if any errors occur the execution of the test.
     */
    @Test
    public void givenFindMoviesByCountryIdRequest_shouldSucceedWith200() throws Exception {
        Long countryId = 1L;

        mockMvc.perform(get("/countries/{id}/movies", countryId).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.movieDTOList[*].country",
                        everyItem(containsString("United States"))));
    }

    /**
     * Creates a request which fetches {@link Movie} resources that are associated with {@link Country}
     * with {@code id -999}.
     * The API has to return a 404 Not Found status since no {@link Country} exists with this {@code id}.
     *
     * @throws Exception if any errors occur the execution of the test.
     */
    @Test
    public void givenFindMoviesByCountryIdRequest_shouldFailWith404() throws Exception {
        Long countryId = -999L;

        mockMvc.perform(get("/countries/{id}/movies", countryId).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Not Found"))
                .andExpect(jsonPath("$.status").value("404"))
                .andExpect(content().string(containsString(
                        String.format("Country with id %s could not be found!", countryId))));
    }
}
