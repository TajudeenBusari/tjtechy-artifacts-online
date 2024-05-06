package com.tjtechy.artifactsOnline.artifact;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tjtechy.artifactsOnline.artifact.dto.ArtifactDto;
import com.tjtechy.artifactsOnline.artifact.utils.IdWorker;
import com.tjtechy.artifactsOnline.client.ai.chat.ChatClient;
import com.tjtechy.artifactsOnline.client.ai.chat.dto.ChatRequest;
import com.tjtechy.artifactsOnline.client.ai.chat.dto.ChatResponse;
import com.tjtechy.artifactsOnline.client.ai.chat.dto.Message;
import com.tjtechy.artifactsOnline.system.exception.ObjectNotFoundException;
import io.micrometer.core.annotation.Timed;
import io.micrometer.observation.annotation.Observed;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;

@Service
@Transactional
public class ArtifactService {

  //inject the dependency using constructor method
  private final ArtifactRepository artifactRepository;

  private final IdWorker idWorker; //generate the unique Id for artifacts for us

  private final ChatClient chatClient;


  public ArtifactService(ArtifactRepository artifactRepository, IdWorker idWorker, ChatClient chatClient) {

    this.artifactRepository = artifactRepository;
    this.idWorker = idWorker;
    this.chatClient = chatClient;
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

  public String summarize(List<ArtifactDto> artifactDtos) throws JsonProcessingException {
    ObjectMapper objectMapper = new ObjectMapper();
    String jsonArray = objectMapper.writeValueAsString(artifactDtos);

    //prepare messages for summarizing
    List<Message> messages = List.of(
            new Message("system", "Your task is to generate a short summary of a given JSON array in at most 100 words. The summary must include the number of artifacts, each artifact's description and the ownership information. Don't mention that the summary is from a given JSON array."),
            new Message("user", jsonArray)
    );
    ChatRequest chatRequest = new ChatRequest("gpt-3.5-turbo", messages);
    ChatResponse chatResponse = this.chatClient.generate(chatRequest); //tell chatClient to generate a text summary based on given chatRequest
    return chatResponse.choices().get(0).message().content();

  }

  public Page<Artifact> findAll(Pageable pageable) {

    return this.artifactRepository.findAll(pageable);
  }


  public Page<Artifact> findByCriteria(Map<String, String> searchCriteria, Pageable pageable) {

    //modify the securityConfig file to allow access to that this end point
    Specification<Artifact> spec = Specification.where(null);//create a base specification

    if(StringUtils.hasLength(searchCriteria.get("id"))){
      spec = spec.and(ArtifactSpecs.hasId(searchCriteria.get("id")));
    }

    if(StringUtils.hasLength(searchCriteria.get("name"))){
      spec = spec.and(ArtifactSpecs.containsName(searchCriteria.get("name")));
    }

    if(StringUtils.hasLength(searchCriteria.get("description"))){
      spec = spec.and(ArtifactSpecs.containsDescription(searchCriteria.get("description")));
    }

    if(StringUtils.hasLength(searchCriteria.get("ownerName"))){
      spec = spec.and(ArtifactSpecs.hasOwnerName(searchCriteria.get("ownerName")));
    }
    return this.artifactRepository.findAll(spec, pageable);
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
