package com.tjtechy.artifactsOnline.wizard;

import com.tjtechy.artifactsOnline.artifact.Artifact;
import com.tjtechy.artifactsOnline.artifact.ArtifactRepository;
import com.tjtechy.artifactsOnline.system.exception.ObjectNotFoundException;

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
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.CoreMatchers.containsString;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles(value = "development") //only used for test case class override any active profile defined in the application.ym file
class WizardServiceTest {

  @Mock
  WizardRepository wizardRepository;

  @Mock
  ArtifactRepository artifactRepository;

  @InjectMocks
  WizardService wizardService;

  List<Wizard> wizards;

  @BeforeEach
  void setUp() {
    Wizard wizard1 = new Wizard();
    wizard1.setId(1);
    wizard1.setName("Albus Dumbledore");

    Wizard wizard2 = new Wizard();
    wizard2.setId(2);
    wizard2.setName("Harry Potter");

    Wizard wizard3 = new Wizard();
    wizard3.setId(3);
    wizard3.setName("Neville Longbottom");

    this.wizards = new ArrayList<>();
    this.wizards.add(wizard1);
    this.wizards.add(wizard2);
    this.wizards.add(wizard3);

  }

  @AfterEach
  void tearDown() {
  }

  //1. find by Id
  //positive scenario

  @Test
  void testFindByIdSuccess() {
    Wizard wizard = new Wizard();
    wizard.setId(2);
    wizard.setName("Harry Potter");
    //Given
    given(wizardRepository.findById(2)).willReturn(Optional.of(wizard));

    //When
    Wizard returnedWizard = wizardService.findById(2);

    //Then
    assertThat(returnedWizard.getId()).isEqualTo(wizard.getId());
    assertThat(returnedWizard.getName()).isEqualTo(wizard.getName());
    verify(wizardRepository, times(1)).findById(2);
  }
  //negative scenario

  @Test
  void testFindByIdNotFound(){
    //Given
    given(wizardRepository.findById(Mockito.any(Integer.class))).willReturn(Optional.empty());

    //When
    Throwable thrown = catchThrowable(() -> {
      Wizard returnedWizard = wizardService.findById(2);
    });

    //Then
    assertThat(thrown)
            .isInstanceOf(ObjectNotFoundException.class)
            .hasMessage("Could not find wizard with Id 2");
    verify(wizardRepository, times(1)).findById(2);
  }

  //2 find all wizards

  //positive scenario
  @Test
  void testFindAllSuccess(){
    //Given
    given(wizardRepository.findAll()).willReturn(this.wizards);

    //When
    List<Wizard> actualWizards = wizardService.findAll();

    //Then
    assertThat(actualWizards.size()).isEqualTo(this.wizards.size());
    verify(wizardRepository, times(1)).findAll();
  }

  //3. create wizard
  //positive scenario
  @Test
  void testCreateSuccess(){
    //Given
    Wizard newWizard = new Wizard();
    newWizard.setName("Harry Potter");
    given(this.wizardRepository.save(newWizard)).willReturn(newWizard);

    //When
    Wizard createdWizard = wizardService.create(newWizard);

    //Then
    assertThat(createdWizard.getName()).isEqualTo(newWizard.getName());
    verify(this.wizardRepository, times(1)).save(newWizard);
  }

  //4. update wizard
  //positive scenario
  @Test
  void testUpdateSuccess(){
    //Given
    //create data that will be updated
    Wizard oldWizard = new Wizard();
    oldWizard.setId(1);
    oldWizard.setName("Albus Dumbledore");

    //update
    Wizard update = new Wizard();
    update.setName("Albus Dumbledore-updated");

    given(this.wizardRepository.findById(1)).willReturn(Optional.of(oldWizard));
    //if found, save/update
    given(this.wizardRepository.save(oldWizard)).willReturn(oldWizard);

    //When
    Wizard updatedWizard = this.wizardService.update(1, update);

    //Then
    assertThat(updatedWizard.getId()).isEqualTo(1);
    assertThat(updatedWizard.getName()).isEqualTo(update.getName());
    verify(this.wizardRepository, times(1)).findById(1);
    verify(this.wizardRepository, times(1)).save(oldWizard);
  }

  //write test case for negative scenario for update

  //5. delete wizard
  //positive scenario
  @Test
  void testDeleteSuccess(){

    //Given
    //prepare a fake artifact to be deleted
    //first find by Id just like update
    //if not found throw an exception

    Wizard wizard = new Wizard();
    wizard.setId(1);
    wizard.setName("Albus Dumbledore");

    given(this.wizardRepository.findById(1)).willReturn(Optional.of(wizard));
    doNothing().when(wizardRepository).deleteById(1);

    //When
    this.wizardService.delete(1);

    //Then
    verify(this.wizardRepository, times(1)).deleteById(1);

  }

  //positive scenario
  @Test
  void testDeleteNotFound(){
    //given
    given(wizardRepository.findById(1)).willReturn(Optional.empty());

    //when
    assertThrows(ObjectNotFoundException.class, ()-> {
      this.wizardService.delete(1);
    });

    //Then
    verify(this.wizardRepository, times(1)).findById(1);
  }

  //6. Assign artifact
  //positive scenario
  @Test
  void testAssignArtifactSuccess(){
    //Given
    //create fake data for artifact and wizards
    //the goal is to assign the artifact which originally belong to Harry Potter to Neville Longbottom
    //since this method depends on both wizard and artifact, then we need to inject the artifactRepository
    Artifact artifact = new Artifact();
    artifact.setId("1250808601744904192");
    artifact.setName("Invisibility Cloak");
    artifact.setDescription("An invisibility cloak is used to make the wearer invisible");
    artifact.setImageUrl("ImageUrl");

    Wizard wizard2 = new Wizard();
    wizard2.setId(2);
    wizard2.setName("Harry Porter");
    wizard2.addArtifact(artifact);

    Wizard wizard3 = new Wizard();
    wizard3.setId(3);
    wizard3.setName("Neville Longbottom");


    given(this.artifactRepository.findById("1250808601744904192")).willReturn(Optional.of(artifact));
    given(this.wizardRepository.findById(3)).willReturn(Optional.of(wizard3));

    //When
    //BOTH EXIST
    this.wizardService.assignArtifact(3, "1250808601744904192");

    //Then
    //after assignment
    assertThat(artifact.getOwner().getId()).isEqualTo(3);
    //assertThat(wizard3.getArtifacts()).toString();
    assertThat(wizard3.getArtifacts()).isEqualToComparingOnlyGivenFields(artifact);
  }

  //7. Assign artifact
  //negative scenario
  @Test
  void testAssignArtifactErrorWithNonExistingWizardId(){
    //Given
    //create fake data for artifact and wizards
    //the goal is to assign the artifact which originally belong to Harry Potter to Neville Longbottom
    //since this method depends on both wizard and artifact, then we need to inject the artifactRepository
    Artifact artifact = new Artifact();
    artifact.setId("1250808601744904192");
    artifact.setName("Invisibility Cloak");
    artifact.setDescription("An invisibility cloak is used to make the wearer invisible");
    artifact.setImageUrl("ImageUrl");

    Wizard wizard2 = new Wizard();
    wizard2.setId(2);
    wizard2.setName("Harry Porter");
    wizard2.addArtifact(artifact);


    given(this.artifactRepository.findById("1250808601744904192")).willReturn(Optional.of(artifact));
    given(this.wizardRepository.findById(3)).willReturn(Optional.empty());

    //When
   Throwable thrown = assertThrows(ObjectNotFoundException.class, () -> {
     this.wizardService.assignArtifact(3, "1250808601744904192");
   });

    //Then
    assertThat(thrown)
            .isInstanceOf(ObjectNotFoundException.class)
                    .hasMessage("Could not find wizard with Id 3");
    assertThat(artifact.getOwner().getId()).isEqualTo(2);

  }

  //8. Assign artifact
  //negative scenario
  @Test
  void testAssignArtifactErrorWithNonExistingArtifactId(){
    //Given
    given(this.artifactRepository.findById("1250808601744904192")).willReturn(Optional.empty());


    //When
    Throwable thrown = assertThrows(ObjectNotFoundException.class, () -> {
      this.wizardService.assignArtifact(3, "1250808601744904192");
    });

    //Then

    assertThat(thrown)
            .isInstanceOf(ObjectNotFoundException.class)
            .hasMessage("Could not find artifact with Id 1250808601744904192");

  }
}