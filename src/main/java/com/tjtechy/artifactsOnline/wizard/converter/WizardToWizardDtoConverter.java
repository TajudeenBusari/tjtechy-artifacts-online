package com.tjtechy.artifactsOnline.wizard.converter;

import com.tjtechy.artifactsOnline.wizard.Wizard;
import com.tjtechy.artifactsOnline.wizard.dto.WizardDto;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class WizardToWizardDtoConverter implements Converter<Wizard, WizardDto> {

  @Override
  public WizardDto convert(Wizard source) {

    //since WizardDti is a record, pass all args
    WizardDto wizardDto = new WizardDto(
            source.getId(),
            source.getName(),
            source.getNumberOfArtifacts());

    return wizardDto;
  }
}


/*implement the converter interface*/