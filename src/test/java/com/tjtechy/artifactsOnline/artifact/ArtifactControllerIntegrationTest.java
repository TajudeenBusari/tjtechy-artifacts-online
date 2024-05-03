package com.tjtechy.artifactsOnline.artifact;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tjtechy.artifactsOnline.system.StatusCode;
import org.hamcrest.Matchers;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Integration tests for Artifact API endpoints")
@Tag("integration")
@ActiveProfiles(value = "development") //only used for test case class override any active profile defined in the application.yml file


public class ArtifactControllerIntegrationTest {

  @Autowired
  MockMvc mockMvc; //we have this just to simulate the http request, we are really not using mock

  @Autowired
  ObjectMapper objectMapper;

  String token;

  @Value("${api.endpoint.base-url}")
  String baseUrl;

  @BeforeEach
  void setUp() throws Exception {

    //first login
    ResultActions resultActions = this.mockMvc.perform(post(this.baseUrl + "/users/login")
            .with(httpBasic("john", "123456")));

    //extract token from resultAction and store in String token
    MvcResult mvcResult = resultActions.andDo(print()).andReturn();
    String contentAsString = mvcResult.getResponse().getContentAsString();

    //first convert contentAsString to json Object and extract token and use this.token to assign to our variable token
    JSONObject jsonObject = new JSONObject(contentAsString);
    this.token = "Bearer " + jsonObject.getJSONObject("data").getString("token"); //remember to add space after bearer



  }

  //Find all artifacts
  //we need to annotate with DirtiesContext before running this test so that DB is reset before this test is called
  @Test
  @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)

  void testFindAllArtifactsSuccess() throws Exception {
    this.mockMvc.perform(get(this.baseUrl + "/artifacts").accept(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.flag").value(true)) //import mockMvc request builder
            .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
            .andExpect(jsonPath("$.message").value("Find All Success"))
            .andExpect(jsonPath("$.data", Matchers.hasSize(6))); //we have 6 artifacts in the db
  }

  //Add artifact
  @Test
  @DisplayName("Check addArtifact with valid input (POST)")
  void testAddArtifactSuccess() throws Exception {
    Artifact artifact = new Artifact();
    artifact.setName("Remembrall");
    artifact.setDescription("A remembral was a magical large marble-sized glass ball that contained smoke...");
    artifact.setImageUrl("imageUrl");

    //serialize into json using jackson
    String json = this.objectMapper.writeValueAsString(artifact);

    this.mockMvc.perform(post(this.baseUrl + "/artifacts")
            .contentType(MediaType.APPLICATION_JSON).header("Authorization", this.token)
            .content(json).accept(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.flag").value(true))
            .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
            .andExpect(jsonPath("$.message").value("Add Success"))
            .andExpect(jsonPath("$.data.id").isNotEmpty())
            .andExpect(jsonPath("$.data.name").value("Remembrall"))
            .andExpect(jsonPath("$.data.description").value("A remembral was a magical large marble-sized glass ball that contained smoke..."))
            .andExpect(jsonPath("$.data.imageUrl").value("imageUrl"));
    //this does not need authorization(to access all artifacts), we don't need to attached authorization in the header
    this.mockMvc.perform(get(this.baseUrl + "/artifacts").accept(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.flag").value(true))
            .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
            .andExpect(jsonPath("$.message").value("Find All Success"))
            .andExpect(jsonPath("$.data", Matchers.hasSize(7))); //after adding one more data, it now becomes 7 in DB

  }

}

/**
 * the last two annotations are optional
 * When test is luanched, we are testing from controller to the database, we're
 * not mocking service of repos classes
 * To perform integration testing when spring security is on, we need to use the
 * addBeforeEach to add the token needed to perform the test
 * Remember to add thie maven dependency: spring security test from maven repo
 */