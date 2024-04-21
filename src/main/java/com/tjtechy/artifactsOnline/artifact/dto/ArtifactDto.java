package com.tjtechy.artifactsOnline.artifact.dto;

import com.tjtechy.artifactsOnline.wizard.dto.WizardDto;
import jakarta.validation.constraints.NotEmpty;
import org.hibernate.validator.constraints.Length;

public record ArtifactDto(String id,
                          @NotEmpty(message = "name is required.")
                                  //@Length()
                          String name,
                          @NotEmpty(message = "description is required.")
                          String description,
                          @NotEmpty(message = "imageUrl is required.")
                          String imageUrl,
                          WizardDto owner) {
}
/*you need to add the Spring Boot Starter validation dependency from
*Maven Repository to use the @NotEmpty annotation
*In addition to NotNull, we can also provide required length
* regular expression (regex) etc*/