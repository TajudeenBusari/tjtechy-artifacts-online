package com.tjtechy.artifactsOnline.artifact;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tjtechy.artifactsOnline.artifact.dto.ArtifactDto;
import com.tjtechy.artifactsOnline.system.StatusCode;
import com.tjtechy.artifactsOnline.system.exception.ObjectNotFoundException;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.mock.http.server.reactive.MockServerHttpRequest.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)//turns off spring security on this unit test so test can pass
@ActiveProfiles(value = "development") //only used for test case class override any active profile defined in the application.ym file
class ArtifactControllerTest {

  @Autowired
  MockMvc mockMvc;

  @MockBean
  ArtifactService artifactService;

  @Autowired
  ObjectMapper objectMapper;

  List<Artifact> artifacts;

  //inject the base url from application-production.yml file
  @Value("${api.endpoint.base-url}")
  String baseUrl;

  @BeforeEach
  void setUp() {

    this.artifacts = new ArrayList<>();
    //this list get called before each test method get called
    Artifact artifact1 = new Artifact();
    artifact1.setId("125080601744904191");
    artifact1.setName("Deluminator");
    artifact1.setDescription("A deluminator is a device invented by Albus Dumbledore that resembles...");
    artifact1.setImageUrl("ImageUrl");
    this.artifacts.add(artifact1);

    Artifact artifact2 = new Artifact();
    artifact2.setId("125080601744904192");
    artifact2.setName("Invisibility Cloak");
    artifact2.setDescription("A Invisibility cloak is to make the wearer invisible");
    artifact2.setImageUrl("ImageUrl");
    this.artifacts.add(artifact2);

    Artifact artifact3 = new Artifact();
    artifact3.setId("125080601744904193");
    artifact3.setName("Elder Wand");
    artifact3.setDescription("The elder wand is know as Deathstick");
    artifact3.setImageUrl("ImageUrl");
    this.artifacts.add(artifact3);

    Artifact artifact4 = new Artifact();
    artifact4.setId("125080601744904194");
    artifact4.setName("The Marauder's Map");
    artifact4.setDescription("The magical map is created by Remus Lupin");
    artifact4.setImageUrl("ImageUrl");
    this.artifacts.add(artifact4);
  }

  @AfterEach
  void tearDown() {
  }
  //1. Find artifact by Id

  //positive scenario
  @Test
  void testFindArtifactByIdSuccess() throws Exception {
    //Given
    given(this.artifactService.findById("125080601744904191")).willReturn(this.artifacts.get(0));

    //When and Then
    this.mockMvc.perform(get(this.baseUrl + "/artifacts/125080601744904191").accept(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.flag").value(true))
            .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
            .andExpect(jsonPath("$.message").value("Find One Success"))
            .andExpect(jsonPath("$.data.id").value("125080601744904191"))
            .andExpect(jsonPath("$.data.name").value("Deluminator"));
  }

  //negative scenario
  @Test
  void testFindArtifactByIdNotFound() throws Exception {
    //Given
    given(this.artifactService.findById("125080601744904191"))
            .willThrow(new ObjectNotFoundException("artifact", "125080601744904191"));

    //When and Then
    this.mockMvc.perform(get(this.baseUrl + "/artifacts/125080601744904191").accept(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.flag").value(false))
            .andExpect(jsonPath("$.code").value(StatusCode.NOT_FOUND))
            .andExpect(jsonPath("$.message").value("Could not find artifact with Id 125080601744904191"))
            .andExpect(jsonPath("$.data").isEmpty());

  }

  //2. Find all artifacts

  //positive scenario
  @Test
  void testFindAllArtifactsSuccess() throws Exception {
    //Given
    given(this.artifactService.findAll()).willReturn(this.artifacts);

    //When and Then
    this.mockMvc.perform(get(this.baseUrl + "/artifacts").accept(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.flag").value(true))
            .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
            .andExpect(jsonPath("$.message").value("Find All Success"))
            .andExpect(jsonPath("$.data", Matchers.hasSize(this.artifacts.size())))
            .andExpect(jsonPath("$.data[0].id").value("125080601744904191"))
            .andExpect(jsonPath("$.data[0].name").value("Deluminator"))
            .andExpect(jsonPath("$.data[1].id").value("125080601744904192"))
            .andExpect(jsonPath("$.data[1].name").value("Invisibility Cloak"));
  }

  //3. create/add artifacts

  //positive scenario
  @Test
  void testAddArtifactSuccess() throws Exception {
    //Given
    //create a Dto obj and covert to json string using Jackson before sending
    //jackson must be autowired into this test class
    ArtifactDto artifactDto = new ArtifactDto(
            null,
            "Remembrall",
            "A Remembrall was a magical large marble-sized glass ball...",
            "ImageUrl",
            null);
    String json = this.objectMapper.writeValueAsString(artifactDto);
    //define a fake data that will be returned by artifactService.save()
    Artifact savedArtifact = new Artifact();
    savedArtifact.setId("12350808601744904197");
    savedArtifact.setName("Remembrall");
    savedArtifact.setDescription("A Remembrall was a magical large marble-sized glass ball...");
    savedArtifact.setImageUrl("ImageUrl");

    given(this.artifactService.save(Mockito.any(Artifact.class))).willReturn(savedArtifact);

    //When and When

    this.mockMvc.perform(post(this.baseUrl + "/artifacts").contentType(MediaType.APPLICATION_JSON).content(json).accept(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.flag").value(true))
            .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
            .andExpect(jsonPath("$.message").value("Add Success"))
            .andExpect(jsonPath("$.data.id").isNotEmpty())
            .andExpect(jsonPath("$.data.name").value(savedArtifact.getName()))
            .andExpect(jsonPath("$.data.description").value(savedArtifact.getDescription()))
            .andExpect(jsonPath("$.data.imageUrl").value(savedArtifact.getImageUrl()));
  }

  //4. update artifact
  // positive scenario
  @Test
  void testUpdateArtifactSuccess() throws Exception {
    //Given
    //prepare a fake data for MockMvc putRequest
    //serialize it (using JACKSON) to Json string and give it back to Mock Mvc
    ArtifactDto artifactDto = new ArtifactDto(
            "12350808601744904197",
            "Invisibility Cloak",
            "Invisibility Cloak Description",
            "ImageUrl",
            null
    );
    String json = this.objectMapper.writeValueAsString(artifactDto);
    //define a fake data that will be used by update method in the service layer
    Artifact updatedArtifact = new Artifact();
    updatedArtifact.setId("12350808601744904197");
    updatedArtifact.setName("Invisibility Cloak");
    updatedArtifact.setDescription("Invisibility Cloak Description");
    updatedArtifact.setImageUrl("ImageUrl");

    given(this.artifactService.update(eq("12350808601744904197"), Mockito.any(Artifact.class))).willReturn(updatedArtifact);

    //When and Then
    this.mockMvc.perform(MockMvcRequestBuilders.put(this.baseUrl + "/artifacts/12350808601744904197")
                    .contentType(MediaType.APPLICATION_JSON).content(json)
                    .accept(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.flag").value(true))
            .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
            .andExpect(jsonPath("$.message").value("Update Success"))
            .andExpect(jsonPath("$.data.id").value("12350808601744904197"))
            .andExpect(jsonPath("$.data.name").value(updatedArtifact.getName()))
            .andExpect(jsonPath("$.data.description").value(updatedArtifact.getDescription()))
            .andExpect(jsonPath("$.data.imageUrl").value(updatedArtifact.getImageUrl()));
  }

  // negative scenario
  @Test
  void testUpdateArtifactErrorWithNonExistingId() throws Exception {
    //Given
    ArtifactDto artifactDto = new ArtifactDto(
            "12350808601744904197",
            "Invisibility Cloak",
            "Invisibility Cloak Description",
            "ImageUrl",
            null
    );
    String json = this.objectMapper.writeValueAsString(artifactDto);


    given(this.artifactService.update(eq("12350808601744904197"), Mockito.any(Artifact.class)))
            .willThrow(new ObjectNotFoundException("artifact", "12350808601744904197"));

    //When and Then
    this.mockMvc.perform(MockMvcRequestBuilders.put(this.baseUrl + "/artifacts/12350808601744904197")
                    .contentType(MediaType.APPLICATION_JSON).content(json)
                    .accept(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.flag").value(false))
            .andExpect(jsonPath("$.code").value(StatusCode.NOT_FOUND))
            .andExpect(jsonPath("$.message").value("Could not find artifact with Id 12350808601744904197"))
            .andExpect(jsonPath("$.data").isEmpty());
  }

  //5. delete artifact
  // positive scenario
  @Test
  void testDeleteArtifactSuccess() throws Exception {
    //Given
    doNothing().when(this.artifactService).delete("12350808601744904191");

    //When and Then
    //simulate http delete request
    //we dont need the content and contentType
    this.mockMvc.perform(MockMvcRequestBuilders.delete(this.baseUrl + "/artifacts/12350808601744904191")
                    .accept(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.flag").value(true))
            .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
            .andExpect(jsonPath("$.message").value("Delete Success"))
            .andExpect(jsonPath("$.data").isEmpty());
  }

  // negative scenario
  @Test
  void testDeleteArtifactErrorWithNotFoundId() throws Exception {
    //Given
    //since service class return nothing
    doThrow(new ObjectNotFoundException("artifact", "12350808601744904191")).when(this.artifactService)
            .delete("12350808601744904191");

    //When and Then
    this.mockMvc.perform(MockMvcRequestBuilders.delete(this.baseUrl + "/artifacts/12350808601744904191")
                    .accept(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.flag").value(false))
            .andExpect(jsonPath("$.code").value(StatusCode.NOT_FOUND))
            .andExpect(jsonPath("$.message").value("Could not find artifact with Id 12350808601744904191"))
            .andExpect(jsonPath("$.data").isEmpty());
  }

  @Test
  void testSummarizeArtifactSuccess() throws Exception {
    //Given
    given(this.artifactService.summarize(Mockito.anyList())).willReturn("The summary includes six artifacts, owned by three wizards.");
    //When and Then
    this.mockMvc.perform(get(this.baseUrl + "/artifacts/summary")
                    .accept(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.flag").value(true))
            .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
            .andExpect(jsonPath("$.message").value("Summarize Success"))
            .andExpect(jsonPath("$.data").value("The summary includes six artifacts, owned by three wizards."));
  }

}


/*Testing controllers is a bit different from testing service class
* We will use MockBean annotation here
* import all right static methods
* take note of the package you are importing
*It will seem as if the controller is returning a java object
* back to the client but the client is not receiving that, it receives a json object.
* what happens is that spring MVC will serialize the java object into a
* json object/string and send that back to the client
* as a developer we won't worry about serializing an obj to json and vice versa
* */