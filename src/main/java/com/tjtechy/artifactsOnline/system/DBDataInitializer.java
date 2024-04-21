package com.tjtechy.artifactsOnline.system;

import com.tjtechy.artifactsOnline.artifact.Artifact;
import com.tjtechy.artifactsOnline.artifact.ArtifactRepository;
import com.tjtechy.artifactsOnline.wizard.Wizard;
import com.tjtechy.artifactsOnline.wizard.WizardRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DBDataInitializer implements CommandLineRunner {

  //inject the ArtifactRepo and WizardRepo to add data to DB
  private final ArtifactRepository artifactRepository;

  private final WizardRepository wizardRepository;

  public DBDataInitializer(ArtifactRepository artifactRepository, WizardRepository wizardRepository) {
    this.artifactRepository = artifactRepository;
    this.wizardRepository = wizardRepository;
  }

  @Override
  public void run(String... args) throws Exception {

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

    Artifact artifact4 = new Artifact();
    artifact4.setId("125080601744904194");
    artifact4.setName("The Marauder's Map");
    artifact4.setDescription("The magical map is created by Remus Lupin");
    artifact4.setImageUrl("ImageUrl");

    Artifact artifact5 = new Artifact();
    artifact4.setId("125080601744904195");
    artifact4.setName("The sword of Gryffindor");
    artifact4.setDescription("The globin made sword adorned with large rubies on...");
    artifact4.setImageUrl("ImageUrl");

    Artifact artifact6 = new Artifact();
    artifact4.setId("125080601744904196");
    artifact4.setName("Resurrection stone");
    artifact4.setDescription("The Resurrection stone allow the holders...");
    artifact4.setImageUrl("ImageUrl");


    Wizard wizard1 = new Wizard();
    wizard1.setId(1);
    wizard1.setName("Albus Dumbledore");
    //assign artifacts to wizard1
    wizard1.addArtifact(artifact1);
    wizard1.addArtifact(artifact3);

    Wizard wizard2 = new Wizard();
    wizard2.setId(2);
    wizard2.setName("Harry Potter");
    wizard2.addArtifact(artifact2);
    wizard2.addArtifact(artifact4);

    Wizard wizard3 = new Wizard();
    wizard3.setId(3);
    wizard3.setName("Neville Longbotton");
    wizard3.addArtifact(artifact5);

    //save wizards to DB
    /*this also saves all associated artifacts because we have
    used cascade type in the wizard entity
    except for artifact 6 which is not assigned to any wizard
     */

    wizardRepository.save(wizard1);
    wizardRepository.save(wizard2);
    wizardRepository.save(wizard3);
    artifactRepository.save(artifact6);

  }
}


/*
* This class implements the CommandLineRunner interface,
* and it has a run method which must be implemented
*
*
* */
