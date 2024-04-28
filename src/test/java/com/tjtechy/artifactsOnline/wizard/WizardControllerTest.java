package com.tjtechy.artifactsOnline.wizard;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tjtechy.artifactsOnline.artifact.Artifact;
import com.tjtechy.artifactsOnline.system.StatusCode;
import com.tjtechy.artifactsOnline.system.exception.ObjectNotFoundException;
import com.tjtechy.artifactsOnline.wizard.dto.WizardDto;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.mockito.Mockito;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)//turn off spring security
class WizardControllerTest {
  @Autowired
  MockMvc mockMvc;

  @MockBean
  WizardService wizardService;

  @Autowired
  ObjectMapper objectMapper;

  List<Wizard> wizards;

  //inject the base url from application.yml file
  @Value("${api.endpoint.base-url}")
  String baseUrl;

  @BeforeEach
  void setUp() {

    Artifact artifact1 = new Artifact();
    artifact1.setId("125080601744904191");
    artifact1.setName("Deluminator");
    artifact1.setDescription("A deluminator is a device invented by Albus Dumbledore that resembles...");
    artifact1.setImageUrl("ImageUrl");

    Artifact artifact2 = new Artifact();
    artifact2.setId("125080601744904192");
    artifact2.setName("Invisibility Cloak");
    artifact2.setDescription("A Invisibility cloak is to make the wearer invisible");
    artifact2.setImageUrl("ImageUrl");

    Artifact artifact3 = new Artifact();
    artifact3.setId("125080601744904193");
    artifact3.setName("Elder Wand");
    artifact3.setDescription("The elder wand is know as Deathstick");
    artifact3.setImageUrl("ImageUrl");

    this.wizards = new ArrayList<>();

    Wizard wizard1 = new Wizard();
    wizard1.setId(1);
    wizard1.setName("Albus Dumbledore");
    wizard1.addArtifact(artifact2);
    wizard1.addArtifact(artifact3);
    this.wizards.add(wizard1);

    Wizard wizard2 = new Wizard();
    wizard2.setId(2);
    wizard2.setName("Harry Potter");
    wizard2.addArtifact(artifact1);
    this.wizards.add(wizard2);

    Wizard wizard3 = new Wizard();
    wizard3.setId(3);
    wizard3.setName("Neville Longbottom");
    wizard3.addArtifact(artifact2);
    this.wizards.add(wizard3);
  }

  @AfterEach
  void tearDown() {
  }

  //1. Find wizard by Id

  //positive scenario
  @Test
  void testFindWizardByIdSuccess() throws Exception {

    //Given
    given(this.wizardService.findById(1)).willReturn(this.wizards.get(0));

    //When and Then
    this.mockMvc.perform(get(this.baseUrl + "/wizards/1").accept(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.flag").value(true))
            .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
            .andExpect(jsonPath("$.message").value("Find One Success"))
            .andExpect(jsonPath("$.data.id").value(1))
            .andExpect(jsonPath("$.data.name").value("Albus Dumbledore"));
  }

  //negative scenario
  @Test
  void testFindWizardByIdNotFound() throws Exception {
    //Given
    given(this.wizardService.findById(1)).willThrow(new ObjectNotFoundException("wizard", 1));
    //When and Then
    this.mockMvc.perform(get(this.baseUrl + "/wizards/1").accept(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.flag").value(false))
            .andExpect(jsonPath("$.code").value(StatusCode.NOT_FOUND))
            .andExpect(jsonPath("$.message").value("Could not find wizard with Id 1"))
            .andExpect(jsonPath("$.data").isEmpty());
  }

  //2. Find All wizards
  @Test
  void testFindAllWizardsSuccess() throws Exception {
    //Given
    given(this.wizardService.findAll()).willReturn(this.wizards);
    //When and Then
    this.mockMvc.perform(get(this.baseUrl + "/wizards").accept(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.flag").value(true))
            .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
            .andExpect(jsonPath("$.message").value("Find All Success"))
            .andExpect(jsonPath("$.data", Matchers.hasSize(this.wizards.size())))
            .andExpect(jsonPath("$.data[0].id").value(1))
            .andExpect(jsonPath("$.data[0].name").value("Albus Dumbledore"))
            .andExpect(jsonPath("$.data[1].id").value(2))
            .andExpect(jsonPath("$.data[1].name").value("Harry Potter"));
  }

  //3. create wizard
  @Test
  void testCreateWizardSuccess() throws Exception {
    //BECAUSE WE TESTING THE CONTROLLER, WE HAVE TO DEAL WITH THE DTO
    //create a Dto obj and covert to json string using Jackson before sending
    //Given
    WizardDto wizardDto = new WizardDto(
            null,
            "Albus Dumbledore",
            2
    );
    String json = this.objectMapper.writeValueAsString(wizardDto);
    //define fake data that will be returned bt the service class
    Wizard newWizard = new Wizard();
    newWizard.setId(1);
    newWizard.setName("Albus Dumbledore");
    given(this.wizardService.create(Mockito.any(Wizard.class))).willReturn(newWizard);

    //When and Then
    this.mockMvc.perform(post(this.baseUrl + "/wizards")
            .contentType(MediaType.APPLICATION_JSON)
            .content(json).accept(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.flag").value(true))
            .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
            .andExpect(jsonPath("$.message").value("Add Success"))
            .andExpect(jsonPath("$.data.id").isNotEmpty())
            .andExpect(jsonPath("$.data.name").value("Albus Dumbledore"));
  }

  //4. update wizard
  //positive scenario
  @Test
  void testUpdateWizardSuccess() throws Exception {
    //Given
    //prepare a fake data to be updated
    WizardDto wizardDto = new WizardDto(null, "Updated wizard name", 2);



    String json = this.objectMapper.writeValueAsString(wizardDto);

    Wizard updatedWizard = new Wizard();
    updatedWizard.setId(1);
    updatedWizard.setName("Updated wizard name");

    given(this.wizardService.update(eq(1), Mockito.any(Wizard.class))).willReturn(updatedWizard);

    //When and //Then
    this.mockMvc.perform(put(this.baseUrl + "/wizards/1").contentType(MediaType.APPLICATION_JSON)
            .content(json).accept(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.flag").value(true))
            .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
            .andExpect(jsonPath("$.message").value("Update Success"))
            .andExpect(jsonPath("$.data.id").value(1))
            .andExpect(jsonPath("$.data.name").value("Updated wizard name"));

  }

  //write test case for //negative scenario for update

  //5. Delete wizard
  //positive scenario
  @Test
  void testDeleteWizardSuccess() throws Exception {
    //Given
    doNothing().when(this.wizardService).delete(3);

    //when and Then
    this.mockMvc.perform(delete(this.baseUrl + "/wizards/3")
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.flag").value(true))
            .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
            .andExpect(jsonPath("$.message").value("Delete Success"))
            .andExpect(jsonPath("$.data").isEmpty());
  }

  //negative scenario
  @Test
  void testDeleteWizardErrorWithNotFound() throws Exception {
    //Given
    doThrow(new ObjectNotFoundException("wizard", 4)).when(this.wizardService).delete(4);

    //When and //Then
    this.mockMvc.perform(delete(this.baseUrl + "/wizards/4").accept(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.flag").value(false))
            .andExpect(jsonPath("$.code").value(StatusCode.NOT_FOUND))
            .andExpect(jsonPath("$.message").value("Could not find wizard with Id 4"))
            .andExpect(jsonPath("$.data").isEmpty());
  }

  //6. Assign artifact
  //positive scenario
  @Test
  void testAssignArtifactSuccess() throws Exception {
    //Given
    //mock the behaviour of service layer
    doNothing().when(this.wizardService).assignArtifact(2, "125080601744904191");
    //When and Then

    this.mockMvc.perform(put(this.baseUrl + "/wizards/2/artifacts/125080601744904191").accept(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.flag").value(true))
            .andExpect(jsonPath("$.code").value(StatusCode.SUCCESS))
            .andExpect(jsonPath("$.message").value("Artifact Assignment Success"))
            .andExpect(jsonPath("$.data").isEmpty());
  }

  //6. Assign artifact
  //negative scenario
  @Test
  void testAssignArtifactErrorWithNonExistingWizardId() throws Exception {
    //Given
    //mock the behaviour of service layer
    doThrow(new ObjectNotFoundException("wizard", 5)).when(this.wizardService).assignArtifact(5, "125080601744904191");
    //When and Then

    this.mockMvc.perform(put(this.baseUrl + "/wizards/5/artifacts/125080601744904191").accept(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.flag").value(false))
            .andExpect(jsonPath("$.code").value(StatusCode.NOT_FOUND))
            .andExpect(jsonPath("$.message").value("Could not find wizard with Id 5"))
            .andExpect(jsonPath("$.data").isEmpty());
  }

  //7. Assign artifact
  //negative scenario
  @Test
  void testAssignArtifactErrorWithNonExistingArtifactId() throws Exception {
    //Given
    //mock the behaviour of service layer
    doThrow(new ObjectNotFoundException("artifact", "125080601744904199")).when(this.wizardService).assignArtifact(2, "125080601744904199");
    //When and Then

    this.mockMvc.perform(put(this.baseUrl + "/wizards/2/artifacts/125080601744904199").accept(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.flag").value(false))
            .andExpect(jsonPath("$.code").value(StatusCode.NOT_FOUND))
            .andExpect(jsonPath("$.message").value("Could not find artifact with Id 125080601744904199"))
            .andExpect(jsonPath("$.data").isEmpty());
  }


}