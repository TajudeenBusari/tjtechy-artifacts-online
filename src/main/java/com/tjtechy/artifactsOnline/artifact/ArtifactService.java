package com.tjtechy.artifactsOnline.artifact;

import com.tjtechy.artifactsOnline.artifact.utils.IdWorker;
import com.tjtechy.artifactsOnline.system.exception.ObjectNotFoundException;
import io.micrometer.core.annotation.Timed;
import io.micrometer.observation.annotation.Observed;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class ArtifactService {

  //inject the dependency using constructor method
  private final ArtifactRepository artifactRepository;

  private final IdWorker idWorker; //generate the unique Id for artifacts for us


  public ArtifactService(ArtifactRepository artifactRepository, IdWorker idWorker) {

    this.artifactRepository = artifactRepository;
    this.idWorker = idWorker;
  }

  @Observed(name = "artifact", contextualName = "findByIdService")
  public Artifact findById(String artifactId){

    return this.artifactRepository.findById(artifactId)
            .orElseThrow(()-> new ObjectNotFoundException("artifact",artifactId));
  }

  @Timed("findAllArtifactsService.time")
  public List<Artifact> findAll(){

    return this.artifactRepository.findAll();
  }

  public Artifact save(Artifact newArtifact){

    //first generate Id and convert to string
    newArtifact.setId(idWorker.nextId() + "");

    return this.artifactRepository.save(newArtifact);
  }

  public Artifact update(String artifactId, Artifact update){
    //find by id
    //modify
    //not, found, throw exception
    return  this.artifactRepository.findById(artifactId)
            .map(oldArtifact -> {
              oldArtifact.setName(update.getName());
              oldArtifact.setDescription(update.getDescription());
              oldArtifact.setImageUrl(update.getImageUrl());

              return this.artifactRepository.save(oldArtifact);

            })
            .orElseThrow(() -> new ObjectNotFoundException("artifact", artifactId));
  }

  public void delete(String artifactId){
    //first find if Id exist or not, then delete

    this.artifactRepository.findById(artifactId)
            .orElseThrow(() -> new ObjectNotFoundException("artifact", artifactId));
    this.artifactRepository.deleteById(artifactId);

  }
}


/*
* Activate service needs to generate the Id during the save method
* So we must inject the IdWorker
*
* */




/*when we launch the application, the IoC container will
inject an instance of the Artifact repository into this class
so that we can use the object (artifactRepository) inside this class
@Transactional put every method in its own transaction meaning:
if a method throws exception while executing, modification to a
database in that method will not occur, i.e there will be a rollback
//can be added at class level or method level (if not all methods needs it)
* let first write a test for the find all method since we are using TDD
*
*
 */
