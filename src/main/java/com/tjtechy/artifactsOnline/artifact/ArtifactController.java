package com.tjtechy.artifactsOnline.artifact;

import com.tjtechy.artifactsOnline.artifact.converter.ArtifactDtoToArtifactConverter;
import com.tjtechy.artifactsOnline.artifact.converter.ArtifactToArtifactDtoConverter;
import com.tjtechy.artifactsOnline.artifact.dto.ArtifactDto;
import com.tjtechy.artifactsOnline.system.Result;
import com.tjtechy.artifactsOnline.system.StatusCode;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/artifacts")
public class ArtifactController {

  private final ArtifactService artifactService;

  private final ArtifactToArtifactDtoConverter artifactToArtifactDtoConverter;

  private final ArtifactDtoToArtifactConverter artifactDtoToArtifactConverter;

  public ArtifactController(ArtifactService artifactService,
                            ArtifactToArtifactDtoConverter artifactToArtifactDtoConverter,
                            ArtifactDtoToArtifactConverter artifactDtoToArtifactConverter) {

    this.artifactService = artifactService;
    this.artifactToArtifactDtoConverter = artifactToArtifactDtoConverter;
    this.artifactDtoToArtifactConverter = artifactDtoToArtifactConverter;
  }

  //find a particular artifact
  @GetMapping("/{artifactId}")
  public Result findArtifactById(@PathVariable String artifactId){

    Artifact foundArtifact = this.artifactService.findById(artifactId);
    //convert found artifact to dto
    ArtifactDto artifactDto = this.artifactToArtifactDtoConverter.convert(foundArtifact);

    return new Result(true, StatusCode.SUCCESS, "Find One Success", artifactDto);
  }

  //find all artifacts
  @GetMapping
  public Result findAllArtifacts(){

    List<Artifact> foundArtifacts = this.artifactService.findAll();

    //convert found artifacts to a list of artifactDtos
    List<ArtifactDto> foundArtifactDtos = foundArtifacts.stream()
            .map(this.artifactToArtifactDtoConverter::convert)
            .collect(Collectors.toList());

    return new Result(true, StatusCode.SUCCESS, "Find All Success", foundArtifactDtos);
  }

  //create artifact
  @PostMapping
  public Result addArtifact(@Valid @RequestBody ArtifactDto artifactDto){

    //convert artifactDto to artifact
    Artifact newArtifact = this.artifactDtoToArtifactConverter.convert(artifactDto);
    Artifact savedArtifact = this.artifactService.save(newArtifact);

    //convert artifact to artifactDto
    ArtifactDto savedArtifactDto = this.artifactToArtifactDtoConverter.convert(savedArtifact);

    return new Result(true, StatusCode.SUCCESS, "Add Success", savedArtifactDto);
  }

  //update artifact
  @PutMapping("/{artifactId}")
  public Result updateArtifact(@PathVariable String artifactId,
                               @Valid @RequestBody ArtifactDto artifactDto){
    //first convert ArtifactDto to Artifact
    Artifact artifactUpdate = this.artifactDtoToArtifactConverter.convert(artifactDto);
    Artifact updatedArtifact = this.artifactService.update(artifactId, artifactUpdate);

    //convert back to Dto
    ArtifactDto updatedArtifactDto = this.artifactToArtifactDtoConverter.convert(updatedArtifact);
    return new Result(true, StatusCode.SUCCESS, "Update Success", updatedArtifactDto);
  }

  //Delete artifact
  @DeleteMapping("/{artifactId}")
  public Result deleteArtifact(@PathVariable String artifactId){
    this.artifactService.delete(artifactId);
    // we are using the Result method with only 3 parameters in the Result class/object
    return new Result(true, StatusCode.SUCCESS, "Delete Success");
  }

}




//Result is the wrapper class that defines the schema of the result