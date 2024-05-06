package com.tjtechy.artifactsOnline.artifact;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.tjtechy.artifactsOnline.artifact.converter.ArtifactDtoToArtifactConverter;
import com.tjtechy.artifactsOnline.artifact.converter.ArtifactToArtifactDtoConverter;
import com.tjtechy.artifactsOnline.artifact.dto.ArtifactDto;
import com.tjtechy.artifactsOnline.system.Result;
import com.tjtechy.artifactsOnline.system.StatusCode;
import io.micrometer.core.instrument.MeterRegistry;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("${api.endpoint.base-url}/artifacts")
public class ArtifactController {

  private final ArtifactService artifactService;

  private final ArtifactToArtifactDtoConverter artifactToArtifactDtoConverter;

  private final ArtifactDtoToArtifactConverter artifactDtoToArtifactConverter;
  //for metrics collection e.g counter metrics
  private final MeterRegistry meterRegistry;

  public ArtifactController(ArtifactService artifactService,
                            ArtifactToArtifactDtoConverter artifactToArtifactDtoConverter,
                            ArtifactDtoToArtifactConverter artifactDtoToArtifactConverter, MeterRegistry meterRegistry) {

    this.artifactService = artifactService;
    this.artifactToArtifactDtoConverter = artifactToArtifactDtoConverter;
    this.artifactDtoToArtifactConverter = artifactDtoToArtifactConverter;
    this.meterRegistry = meterRegistry;
  }

  //find a particular artifact
  @GetMapping("/{artifactId}")
  public Result findArtifactById(@PathVariable String artifactId){

    Artifact foundArtifact = this.artifactService.findById(artifactId);
    meterRegistry.counter("artifact.id." + artifactId).increment();
    //convert found artifact to dto
    ArtifactDto artifactDto = this.artifactToArtifactDtoConverter.convert(foundArtifact);

    return new Result(true, StatusCode.SUCCESS, "Find One Success", artifactDto);
  }

  //find all artifacts
  @GetMapping
  public Result findAllArtifacts(Pageable pageable){

    Page<Artifact> artifactPage = this.artifactService.findAll(pageable);

    //convert found artifactPage to a artifactDtos
    //page is also streamable, so we can use the map method directly
    Page<ArtifactDto> foundArtifactDtoPage = artifactPage
            .map(this.artifactToArtifactDtoConverter::convert);

    return new Result(true, StatusCode.SUCCESS, "Find All Success", foundArtifactDtoPage);
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

  //define a method for artifacts summarization
  @GetMapping("/summary")
  public Result summarizeArtifacts() throws JsonProcessingException {

    List<Artifact> foundArtifacts = this.artifactService.findAll();

    //convert found artifacts to a list of artifactDtos
    List<ArtifactDto> foundArtifactDtos = foundArtifacts.stream()
            .map(this.artifactToArtifactDtoConverter::convert)
            .collect(Collectors.toList());
    String artifactSummary = this.artifactService.summarize(foundArtifactDtos);

    return new Result(true, StatusCode.SUCCESS, "Summarize Success", artifactSummary);
  }

  //search by criteria
  @PostMapping("/search")
  public Result findArtifactsByCriteria(@RequestBody Map<String, String> searchCriteria, Pageable pageable){

    //user may provide one or more data to make their search, we use
    // Map to map the request body in terms of key and value

    Page<Artifact> artifactPage = this.artifactService.findByCriteria(searchCriteria, pageable);
    //convert to dto
    Page<ArtifactDto> artifactDtoPage = artifactPage.map(this.artifactToArtifactDtoConverter::convert);

    return new Result(true, StatusCode.SUCCESS, "Search Success", artifactDtoPage);
  }

}




//Result is the wrapper class that defines the schema of the result