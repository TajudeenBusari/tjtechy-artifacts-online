package com.tjtechy.artifactsOnline.wizard;

import com.tjtechy.artifactsOnline.artifact.Artifact;
import com.tjtechy.artifactsOnline.artifact.ArtifactRepository;
import com.tjtechy.artifactsOnline.artifact.utils.IdWorker;
import com.tjtechy.artifactsOnline.system.exception.ObjectNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class WizardService {

  private final WizardRepository wizardRepository;

  private final ArtifactRepository artifactRepository;

  public WizardService(WizardRepository wizardRepository, ArtifactRepository artifactRepository) {

    this.wizardRepository = wizardRepository;
    this.artifactRepository = artifactRepository;
  }
  public Wizard findById(Integer wizardId){

    return this.wizardRepository.findById(wizardId)
            .orElseThrow(() -> new ObjectNotFoundException("wizard", wizardId));
  }

  public List<Wizard> findAll(){

    return this.wizardRepository.findAll();
  }

  public Wizard create(Wizard newWizard){

    return this.wizardRepository.save(newWizard);
  }

  public Wizard update(Integer wizardId, Wizard update){
    //find by id
    //modify
    //not, found, throw exception
    return this.wizardRepository.findById(wizardId)
            .map(oldWizard -> {
              oldWizard.setName(update.getName());
              return this.wizardRepository.save(oldWizard);
            })
            .orElseThrow(()->new ObjectNotFoundException("wizard", wizardId));
  }

  public void delete(Integer wizardId){
    //first find if Id exist or not, then delete
    this.wizardRepository.findById(wizardId).orElseThrow(()->
            new ObjectNotFoundException("wizard", wizardId));
    //if found
    this.wizardRepository.deleteById(wizardId);
  }

  public void assignArtifact(Integer wizardId, String artifactId){
    //Firstly Find this artifact by Id from DB if it exists or throw exception if does not
    Artifact artifactToBeAssigned = this.artifactRepository.findById(artifactId).orElseThrow(() ->
            new ObjectNotFoundException("artifact", artifactId));

    //Secondly, find this wizard by Id from DB
    Wizard wizard = this.wizardRepository.findById(wizardId).orElseThrow(() ->
            new ObjectNotFoundException("wizard", wizardId));

    //Thirdly Artifact assignment
    //we need to see if the artifact is already owned by some wizard
    if(artifactToBeAssigned.getOwner() != null){
      //remove (create the method in wizard class)
      artifactToBeAssigned.getOwner().removeArtifact(artifactToBeAssigned);
    }
    wizard.addArtifact(artifactToBeAssigned);



  }

}

/*since the assign artifact method depends on both wizard and artifact,
// then we need to inject the artifactRepository just as the wizardRepository
 to know if either Id exist in the Repositories*/