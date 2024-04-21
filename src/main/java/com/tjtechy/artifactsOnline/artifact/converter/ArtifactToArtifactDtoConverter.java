package com.tjtechy.artifactsOnline.artifact.converter;

import com.tjtechy.artifactsOnline.artifact.Artifact;
import com.tjtechy.artifactsOnline.artifact.dto.ArtifactDto;
import com.tjtechy.artifactsOnline.wizard.converter.WizardToWizardDtoConverter;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class ArtifactToArtifactDtoConverter implements Converter<Artifact, ArtifactDto> {

  //inject the WizardToWizardDtoConverter to be able to get artifact owner
  private final WizardToWizardDtoConverter wizardToWizardDtoConverter;

  public ArtifactToArtifactDtoConverter(WizardToWizardDtoConverter wizardToWizardDtoConverter) {
    this.wizardToWizardDtoConverter = wizardToWizardDtoConverter;
  }

  @Override
  public ArtifactDto convert(Artifact source) {
    ArtifactDto artifactDto = new ArtifactDto(source.getId(),
            source.getName(),
            source.getDescription(),
            source.getImageUrl(),
            source.getOwner() != null
                    ? this.wizardToWizardDtoConverter.convert(source.getOwner())
                    : null);

    return artifactDto;
  }

}

/*helps to manage the life cycle of these converters
 (we're making both converters component)
 if the owner is null, we should not pass it, so we use ternary
 operator
 */