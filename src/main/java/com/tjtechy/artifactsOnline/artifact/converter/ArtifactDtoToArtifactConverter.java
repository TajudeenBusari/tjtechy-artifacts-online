package com.tjtechy.artifactsOnline.artifact.converter;

import com.tjtechy.artifactsOnline.artifact.Artifact;
import com.tjtechy.artifactsOnline.artifact.dto.ArtifactDto;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class ArtifactDtoToArtifactConverter implements Converter<ArtifactDto, Artifact> {

  @Override
  public Artifact convert(ArtifactDto source) {
    Artifact artifact = new Artifact();
    artifact.setId(source.id());
    artifact.setName(source.name());
    artifact.setDescription(source.description());
    artifact.setImageUrl(source.imageUrl());

    return artifact;
  }
}


/*Since artifactDto is a RECORD, it will .property e.g source.id etc.
* Client does not have knowledge about owner, it is null here
* */