package com.tjtechy.artifactsOnline.wizard;

import com.tjtechy.artifactsOnline.system.Result;
import com.tjtechy.artifactsOnline.system.StatusCode;
import com.tjtechy.artifactsOnline.wizard.converter.WizardDtoToWizardConverter;
import com.tjtechy.artifactsOnline.wizard.converter.WizardToWizardDtoConverter;
import com.tjtechy.artifactsOnline.wizard.dto.WizardDto;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

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
  @GetMapping("/{wizardId}")
  public Result findWizardById(@PathVariable Integer wizardId){

    Wizard foundWizard = this.wizardService.findById(wizardId);
    //convert found wizard to Dto
    WizardDto wizardDto = this.wizardToWizardDtoConverter.convert(foundWizard);
    return new Result(true, StatusCode.SUCCESS, "Find One Success", wizardDto);
  }

  //find all wizards
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

  @DeleteMapping("/{wizardId}")
  public Result deleteWizard(@PathVariable Integer wizardId){
    this.wizardService.delete(wizardId);
    return new Result(true, StatusCode.SUCCESS, "Delete Success");
  }

}
