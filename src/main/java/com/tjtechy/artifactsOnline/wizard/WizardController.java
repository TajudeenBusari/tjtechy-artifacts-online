package com.tjtechy.artifactsOnline.wizard;

import com.tjtechy.artifactsOnline.system.Result;
import com.tjtechy.artifactsOnline.system.StatusCode;
import com.tjtechy.artifactsOnline.wizard.converter.WizardDtoToWizardConverter;
import com.tjtechy.artifactsOnline.wizard.converter.WizardToWizardDtoConverter;
import com.tjtechy.artifactsOnline.wizard.dto.WizardDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Tag(
        name = "CRUD REST APIs for wizards Resource",
        description = "CRUD REST APIs - Create Wizard, Update Wizard, Update artifact ownership by Wizard Id and Artifact Id, Get All Wizards, Get Wizard, Delete Wizard"
)
@RestController
@RequestMapping("${api.endpoint.base-url}/wizards")
public class WizardController {

  private final WizardService wizardService;
  private final WizardToWizardDtoConverter wizardToWizardDtoConverter;
  private final WizardDtoToWizardConverter wizardDtoToWizardConverter;

  public WizardController(WizardService wizardService,
                          WizardToWizardDtoConverter wizardToWizardDtoConverter,
                          WizardDtoToWizardConverter wizardDtoToWizardConverter) {

    this.wizardService = wizardService;
    this.wizardToWizardDtoConverter = wizardToWizardDtoConverter;
    this.wizardDtoToWizardConverter = wizardDtoToWizardConverter;
  }

  //find a particular wizard
  @Operation(
          summary = "Get Wizard by Id REST API",
          description = "Get Wizard is used to get wizard in database"
  )
  @ApiResponse(
          responseCode = "200",
          description = "HTTP Status 200 SUCCESS"
  )
  @GetMapping("/{wizardId}")
  public Result findWizardById(@PathVariable Integer wizardId){

    Wizard foundWizard = this.wizardService.findById(wizardId);
    //convert found wizard to Dto
    WizardDto wizardDto = this.wizardToWizardDtoConverter.convert(foundWizard);
    return new Result(true, StatusCode.SUCCESS, "Find One Success", wizardDto);
  }

  //find all wizards
  @Operation(
          summary = "Get all Wizards REST API",
          description = "Get all Wizards is used to get wizards in database"
  )
  @ApiResponse(
          responseCode = "200",
          description = "HTTP Status 200 SUCCESS"
  )
  @GetMapping
  public Result findAllWizards(){

    List<Wizard> foundWizards = this.wizardService.findAll();
    //convert found Wizard to Dto
    List<WizardDto> foundWizardDtos = foundWizards.stream()
            .map(this.wizardToWizardDtoConverter::convert)
            .collect(Collectors.toList());
    return new Result(true, StatusCode.SUCCESS, "Find All Success", foundWizardDtos);
  }

  //create a wizard
  @Operation(
          summary = "Add Wizard REST API",
          description = "Add Wizard is used to create new wizard in database"
  )
  @ApiResponse(
          responseCode = "200",
          description = "HTTP Status 200 SUCCESS"
  )
  @PostMapping
  public Result createWizard(@Valid @RequestBody WizardDto wizardDto){
    //convert wizardDto to wizard
    Wizard newWizard = this.wizardDtoToWizardConverter.convert(wizardDto);
    Wizard createdWizard = this.wizardService.create(newWizard);

    //convert wizard to wizardDto
    WizardDto createdWizardDto = this.wizardToWizardDtoConverter.convert(createdWizard);
    return new Result(true, StatusCode.SUCCESS, "Add Success", createdWizardDto);
  }

  //update wizard
  @Operation(
          summary = "Update Wizard REST API",
          description = "Update Wizard is used to update an existing wizard in database"
  )
  @ApiResponse(
          responseCode = "200",
          description = "HTTP Status 200 SUCCESS"
  )
  @PutMapping("/{wizardId}")
  public Result updateWizard(@PathVariable Integer wizardId,
                             @Valid @RequestBody WizardDto wizardDto){

    //first convert WizardDto to Wizard
    Wizard wizardUpdate = this.wizardDtoToWizardConverter.convert(wizardDto);
    Wizard updatedWizard = this.wizardService.update(wizardId, wizardUpdate);

    //convert back to dto
    WizardDto updatedWizardDto = this.wizardToWizardDtoConverter.convert(updatedWizard);

    return new Result(true, StatusCode.SUCCESS, "Update Success", updatedWizardDto);
  }

  @Operation(
          summary = "Delete Wizard REST API",
          description = "Delete Wizard is used to remove an existing wizard in database"
  )
  @ApiResponse(
          responseCode = "200",
          description = "HTTP Status 200 SUCCESS"
  )
  @DeleteMapping("/{wizardId}")
  public Result deleteWizard(@PathVariable Integer wizardId){
    this.wizardService.delete(wizardId);
    return new Result(true, StatusCode.SUCCESS, "Delete Success");
  }

  //method (put) to handle the request artifacts
  //put method cos it is changing the ownership of artifact
  //has two path params-->artifactId and wizardId
  @Operation(
          summary = "Assign Artifact REST API",
          description = "Assign Wizard is used to change the ownership of an artifact"
  )
  @ApiResponse(
          responseCode = "200",
          description = "HTTP Status 200 SUCCESS"
  )
  @PutMapping("/{wizardId}/artifacts/{artifactId}")
  public Result assignArtifact(@PathVariable Integer wizardId, @PathVariable String artifactId){
    this.wizardService.assignArtifact(wizardId, artifactId);
    return new Result(true, StatusCode.SUCCESS, "Artifact Assignment Success");
  }


}
