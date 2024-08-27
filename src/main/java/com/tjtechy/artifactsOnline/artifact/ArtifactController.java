package com.tjtechy.artifactsOnline.artifact;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.tjtechy.artifactsOnline.artifact.converter.ArtifactDtoToArtifactConverter;
import com.tjtechy.artifactsOnline.artifact.converter.ArtifactToArtifactDtoConverter;
import com.tjtechy.artifactsOnline.artifact.dto.ArtifactDto;
import com.tjtechy.artifactsOnline.client.imagestorage.ImageStorageClient;
import com.tjtechy.artifactsOnline.system.Result;
import com.tjtechy.artifactsOnline.system.StatusCode;
import io.micrometer.core.instrument.MeterRegistry;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Tag(
        name = "CRUD REST APIs for artifacts Resource",
        description = "CRUD REST APIs - Create Artifact, Update Artifact, Get All Artifacts, Get Artifact, Get Artifact summary, Delete Artifact"
)
@RestController
@RequestMapping("${api.endpoint.base-url}/artifacts")
public class ArtifactController {

  private final ArtifactService artifactService;

  private final ArtifactToArtifactDtoConverter artifactToArtifactDtoConverter;

  private final ArtifactDtoToArtifactConverter artifactDtoToArtifactConverter;
  //for metrics collection e.g counter metrics
  private final MeterRegistry meterRegistry;
  private final ImageStorageClient imageStorageClient;

  public ArtifactController(ArtifactService artifactService,
                            ArtifactToArtifactDtoConverter artifactToArtifactDtoConverter,
                            ArtifactDtoToArtifactConverter artifactDtoToArtifactConverter,
                            MeterRegistry meterRegistry,
                            ImageStorageClient imageStorageClient) {

    this.artifactService = artifactService;
    this.artifactToArtifactDtoConverter = artifactToArtifactDtoConverter;
    this.artifactDtoToArtifactConverter = artifactDtoToArtifactConverter;
    this.meterRegistry = meterRegistry;
    this.imageStorageClient = imageStorageClient;
  }

  //find a particular artifact
  @Operation(
          summary = "Get Artifact by Id REST API",
          description = "Get Artifact is used to get artifact in database"
  )
  @ApiResponse(
          responseCode = "200",
          description = "HTTP Status 200 SUCCESS"
  )
  @GetMapping("/{artifactId}")
  public Result findArtifactById(@PathVariable String artifactId){

    Artifact foundArtifact = this.artifactService.findById(artifactId);
    meterRegistry.counter("artifact.id." + artifactId).increment();
    //convert found artifact to dto
    ArtifactDto artifactDto = this.artifactToArtifactDtoConverter.convert(foundArtifact);

    return new Result(true, StatusCode.SUCCESS, "Find One Success", artifactDto);
  }

  //find all artifacts
  @Operation(
          summary = "Get all Artifacts REST API",
          description = "Get all Artifacts is used to retrieve all artifacts in database"
  )
  @ApiResponse(
          responseCode = "200",
          description = "HTTP Status 200 SUCCESS"
  )
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
  @Operation(
          summary = "Add Artifact REST API",
          description = "Add Artifact is used to create new artifact in database"
  )
  @ApiResponse(
          responseCode = "200",
          description = "HTTP Status 200 SUCCESS"
  )
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
  @Operation(
          summary = "Update Artifact REST API",
          description = "Update Artifact is used to update an existing artifact in database"
  )
  @ApiResponse(
          responseCode = "200",
          description = "HTTP Status 200 SUCCESS"
  )
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
  @Operation(
          summary = "Delete Artifact REST API",
          description = "Delete Artifact is used to remove an existing artifact in database"
  )
  @ApiResponse(
          responseCode = "200",
          description = "HTTP Status 200 SUCCESS"
  )
  @DeleteMapping("/{artifactId}")
  public Result deleteArtifact(@PathVariable String artifactId){
    this.artifactService.delete(artifactId);
    // we are using the Result method with only 3 parameters in the Result class/object
    return new Result(true, StatusCode.SUCCESS, "Delete Success");
  }

  //define a method for artifacts summarization
  @Operation(
          summary = "Summarize Artifacts REST API",
          description = "Summarize Artifacts is used to get the summary of artifacts"
  )
  @ApiResponse(
          responseCode = "200",
          description = "HTTP Status 200 SUCCESS"
  )
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
  @Operation(
          summary = "Search Artifacts REST API",
          description = "Search Artifacts is used to find artifact by criteria specified in the request body"
  )
  @ApiResponse(
          responseCode = "200",
          description = "HTTP Status 200 SUCCESS"
  )
  @PostMapping("/search")
  public Result findArtifactsByCriteria(@RequestBody Map<String, String> searchCriteria, Pageable pageable){

    //user may provide one or more data to make their search, we use
    // Map to map the request body in terms of key and value

    Page<Artifact> artifactPage = this.artifactService.findByCriteria(searchCriteria, pageable);
    //convert to dto
    Page<ArtifactDto> artifactDtoPage = artifactPage.map(this.artifactToArtifactDtoConverter::convert);

    return new Result(true, StatusCode.SUCCESS, "Search Success", artifactDtoPage);
  }

  @Operation(
          summary = "Upload Artifact image REST API",
          description = "Upload Artifact is used to upload artifact image to azure storage blob"
  )
  @ApiResponse(
          responseCode = "200",
          description = "HTTP Status 200 SUCCESS"
  )
  @PostMapping("/images")
  public Result uploadImage(@RequestParam String containerName, @RequestParam MultipartFile file) throws IOException {
    try (InputStream inputStream = file.getInputStream()){
      String imageUrl = this.imageStorageClient.uploadImage(containerName, file.getOriginalFilename(), inputStream, file.getSize());
      return new Result(true, StatusCode.SUCCESS, "Image Upload Success", imageUrl);
    }


  }

}




//Result is the wrapper class that defines the schema of the result