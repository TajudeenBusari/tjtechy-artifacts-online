package com.tjtechy.artifactsOnline.artifact;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tjtechy.artifactsOnline.artifact.dto.ArtifactDto;
import com.tjtechy.artifactsOnline.artifact.utils.IdWorker;
import com.tjtechy.artifactsOnline.client.ai.chat.ChatClient;
import com.tjtechy.artifactsOnline.client.ai.chat.dto.ChatRequest;
import com.tjtechy.artifactsOnline.client.ai.chat.dto.ChatResponse;
import com.tjtechy.artifactsOnline.client.ai.chat.dto.Choice;
import com.tjtechy.artifactsOnline.client.ai.chat.dto.Message;
import com.tjtechy.artifactsOnline.system.exception.ObjectNotFoundException;
import com.tjtechy.artifactsOnline.wizard.Wizard;
import com.tjtechy.artifactsOnline.wizard.dto.WizardDto;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.catchThrowable;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles(value = "development") //only used for test case class override any active profile defined in the application.ym file

class ArtifactServiceTest {

  @Mock
  ArtifactRepository artifactRepository;

  @Mock
  IdWorker idWorker;

  @Mock
  ChatClient chatClient;

  @InjectMocks //injects both mocks into the artifactService
  ArtifactService artifactService;

  //create a list of artifacts for testing find all
  List<Artifact> artifacts;

  @BeforeEach
  void setUp() {
    Artifact artifact1 = new Artifact();
    artifact1.setId("1250808601744904191");
    artifact1.setName("Deluminatork");
    artifact1.setDescription("An Deluminator is a device invented by Albus Dumbledor...");
    artifact1.setImageUrl("ImageUrl");

    Artifact artifact2 = new Artifact();
    artifact2.setId("1250808601744904192");
    artifact2.setName("Invisibility Cloak");
    artifact2.setDescription("An invisibility cloak is used to make the water invisible");
    artifact2.setImageUrl("ImageUrl");

    this.artifacts = new ArrayList<>();
    this.artifacts.add(artifact1);
    this.artifacts.add(artifact2);
  }

  @AfterEach
  void tearDown() {
  }

  //1. find a single artifact by Id
  //positive scenario
  @Test
  void testFindByIdSuccess() {
    //When testing, we follow:

    //Given. Arrange inputs and targets. Define the behavior of Mock object (artifactRepository)

    //create some fake artifact
    /*"id": "1250808601744904192",
      "name":"Invisibility Cloak",
      "description":"An invisibility cloak is used to make the water invisible";
      "imageUrl":"ImageUrl"
    * */
    Artifact artifact = new Artifact();
    artifact.setId("1250808601744904192");
    artifact.setName("Invisibility Cloak");
    artifact.setDescription("An invisibility cloak is used to make the water invisible");
    artifact.setImageUrl("ImageUrl");

    //create a fake wizard, the wizard will be the owner of the artifact
    Wizard wizard = new Wizard();
    wizard.setId(2);
    wizard.setName("Harry Potter");

    artifact.setOwner(wizard);//set owner of the artifact

    given(artifactRepository.findById("1250808601744904192")).willReturn(Optional.of(artifact));

    //When. Act on the target behavior.When steps should cover the method to be tested
    Artifact returnedArtifact = artifactService.findById("1250808601744904192");

    //Then. Assert step. Assert expected outcomes
    assertThat(returnedArtifact.getId()).isEqualTo(artifact.getId());
    assertThat(returnedArtifact.getName()).isEqualTo(artifact.getName());
    assertThat(returnedArtifact.getDescription()).isEqualTo(artifact.getDescription());
    assertThat(returnedArtifact.getImageUrl()).isEqualTo(artifact.getImageUrl());
    //WE CAN ALSO VERIFY THAT THE method is called once in the service object
    verify(artifactRepository, times(1)).findById("1250808601744904192");
  }

  //negative scenario
  @Test
  void testFindByIdNotFound(){

    //Given
    given(artifactRepository.findById(Mockito.any(String.class))).willReturn(Optional.empty());

    //when
    Throwable thrown = catchThrowable(() -> {
      Artifact returnedArtifact = artifactService.findById("1250808601744904192");
    });

    //then
    assertThat(thrown)
            .isInstanceOf(ObjectNotFoundException.class)
            .hasMessage("Could not find artifact with Id 1250808601744904192");
    verify(artifactRepository, times(1)).findById("1250808601744904192");
  }

  //2. find all artifacts
  //positive scenario
  @Test
  void testFindAllSuccess(){
    //Given
    given(artifactRepository.findAll()).willReturn(this.artifacts);

    //When
    List<Artifact> actualArtifacts = artifactService.findAll();

    //Then
    assertThat(actualArtifacts.size()).isEqualTo(this.artifacts.size());
    verify(artifactRepository, times(1)).findAll();

  }

  //3. create artifacts

  //positive scenario
  @Test
  void testSaveSuccess(){
    //Given
    Artifact newArtifact = new Artifact();
    newArtifact.setName("Helsinki East Artifact");
    newArtifact.setDescription("Artifact located at Helsinki East side...");
    newArtifact.setImageUrl("ImageUrl");

    given(idWorker.nextId()).willReturn(123456L);
    given(artifactRepository.save(newArtifact)).willReturn(newArtifact);

    //When
    Artifact savedArtifact = artifactService.save(newArtifact);

    //Then
    assertThat(savedArtifact.getId()).isEqualTo("123456");
    assertThat(savedArtifact.getName()).isEqualTo(newArtifact.getName());
    assertThat(savedArtifact.getDescription()).isEqualTo(newArtifact.getDescription());
    assertThat(savedArtifact.getImageUrl()).isEqualTo(newArtifact.getImageUrl());
    verify(artifactRepository, times(1)).save(newArtifact);

  }

  //4. update artifact

  //positive scenario
  @Test
  void testUpdateSuccess(){

    //Given
    //create some old Artifact data that will be updated
    Artifact oldArtifact = new Artifact();
    oldArtifact.setId("1250808601744904192");
    oldArtifact.setName("Invisibility Cloak");
    oldArtifact.setDescription("An invisibility cloak is used to make the water invisible");
    oldArtifact.setImageUrl("ImageUrl");

    //update
    Artifact update = new Artifact();
    update.setId("1250808601744904192");
    update.setName("Invisibility Cloak");
    update.setDescription("An invisibility cloak is used to make the water invisible-update");
    update.setImageUrl("ImageUrl");

    given(artifactRepository.findById("1250808601744904192")).willReturn(Optional.of(oldArtifact));
    given(artifactRepository.save(oldArtifact)).willReturn(oldArtifact);//already has updated values

    //When
    Artifact updatedArtifact = artifactService.update("1250808601744904192", update);

    //Then
    assertThat(updatedArtifact.getId()).isEqualTo(update.getId());
    assertThat(updatedArtifact.getDescription()).isEqualTo(update.getDescription());
    verify(artifactRepository, times(1)).findById("1250808601744904192");
    verify(artifactRepository, times(1)).save(oldArtifact);

    /*update is a little tricky:
    //1. we find artifact by id
    //2. we then update the old artifact
     We need to mock two behaviours above*/
  }

  //negative scenario
  @Test
  void testUpdateNotFound(){
    //Given
    //Id does not exist
    Artifact update = new Artifact();
    update.setName("Invisibility Cloak");
    update.setDescription("An invisibility cloak is used to make the water invisible-update");
    update.setImageUrl("ImageUrl");

    given(artifactRepository.findById("1250808601744904192")).willReturn(Optional.empty());

    //When
    assertThrows(ObjectNotFoundException.class, () ->{
      artifactService.update("1250808601744904192", update);
    });
    //Then
    verify(artifactRepository, times(1)).findById("1250808601744904192");
  }

  //5. Delete
  //positive scenario
  @Test
  void testDeleteSuccess(){
    //first find by Id just like update
    //if not found throw an exception
    //Given
    //prepare a fake artifact to be deleted
    Artifact artifact = new Artifact();
    artifact.setId("1250808601744904192");
    artifact.setName("Invisibility Cloak");
    artifact.setDescription("An invisibility cloak is used to make the water invisible");
    artifact.setImageUrl("ImageUrl");
    //mock the behaviours of repository findById and deleteById
    //since the deleteById method in the repository does not return anything, we weill use doNothing() form mockito
    given(artifactRepository.findById("1250808601744904192")).willReturn(Optional.of(artifact));
    doNothing().when(artifactRepository).deleteById("1250808601744904192");

    //When
    //since this is not returning anything, we do not have to assign to any variable
    artifactService.delete("1250808601744904192");
    //Then
    //we only need to verify that the action is called once in the repository
    verify(artifactRepository, times(1)).deleteById("1250808601744904192");
  }

  //negative scenario
  @Test
  void testDeleteNotFound(){
    //Given
    given(artifactRepository.findById("1250808601744904192")).willReturn(Optional.empty());

    //When
    assertThrows(ObjectNotFoundException.class, ()-> {
      artifactService.delete("1250808601744904192");
    });
    //Then
    verify(artifactRepository, times(1)).findById("1250808601744904192");
  }

  //define a test method for summarize
  @Test
  void testSummarizeSuccess() throws JsonProcessingException {
    //Given
    //recall artifactDto contains a wizardDto, create it first
    WizardDto wizardDto = new WizardDto(1, "Albus Dombledore", 2);

    //create two artifactDto and put them in a list
    List<ArtifactDto> artifactDtos = List.of(
            new ArtifactDto("1250808601744904191", "Deluminator", "An Deluminator is a device invented by Albus Dumbledor...", "ImageUrl", wizardDto),
            new ArtifactDto("1250808601744904193", "Elder Wand", "The Elder Wand, known as Deathstick or Wand of ...", "ImageUrl", wizardDto)
    );

    //define the behavior of chat client
    //create list of messages

    //prepare chatRequest
    ObjectMapper objectMapper = new ObjectMapper();
    String jsonArray = objectMapper.writeValueAsString(artifactDtos);
    List<Message> messages = List.of(
            new Message("system", "Your task is to generate a short summary of a given JSON array in at most 100 words. The summary must include the number of artifacts, each artifact's description and the ownership information. Don't mention that the summary is from a given JSON array."),
            new Message("user", jsonArray)
    );

    ChatRequest chatRequest = new ChatRequest("gpt-3.5-turbo", messages);

    //prepare chatResponse
    ChatResponse chatResponse = new ChatResponse(List.of(new
            Choice(0, new Message("assistant", "A summary of two artifacts owned by Albus Dumbledor."))));

    given(this.chatClient.generate(chatRequest)).willReturn(chatResponse);


    //When
    String summary = this.artifactService.summarize(artifactDtos);

    //Then
    assertThat(summary).isEqualTo("A summary of two artifacts owned by Albus Dumbledor.");
    verify(this.chatClient, times(1)).generate(chatRequest);

  }

}


//NB: DON'T USE THE REAL REPOSITORY FOR TESTING
/*BeforeEach means if any each test method get executed
by jUnit5, set up is executed first
*AfterEach means after each test is executed, tear down gets called
*Because we have not called the repository class in the service class
for the purpose of testing, we will use mocking.
*Mocking is creating objects that simulates the behavior of real object,
 in this case the artifactrepository
*A popular frame for mocking is Mockito
*Add the Mockito annotation, introduce the repository class and the services class
*Add Mock annotation to the object being simulated
*Inject the Mock to the service object here
*When test starts, Mockito will inject the mocked repo object to the service obj
NB: findById in the repository (check the classes it extends) returns optional, so
take note while testing the service class

* TEST find all artifacts in the service class
* inject the IdWorker as well since we need to mock the bean
(generate Id) here as well
*
*/