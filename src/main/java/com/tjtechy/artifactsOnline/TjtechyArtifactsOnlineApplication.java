package com.tjtechy.artifactsOnline;

import com.tjtechy.artifactsOnline.artifact.utils.IdWorker;
import io.swagger.v3.oas.annotations.ExternalDocumentation;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.tags.Tags;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@OpenAPIDefinition(
				info = @Info(
								title = "Spring Boot Rest API Doc",
								description = "Spring Boot REST API Doc",
								version = "v1.0.0",
								contact = @Contact(
												name = "Tjtechy",
												email = "tjtechy@gmail.com",
												url = "https://www.tjtechy.com"
								),

								license = @License(
												name = "Apache 2.0",
												url = "https://www.tjtechy.com/license"
								)

				),
				externalDocs = @ExternalDocumentation(
								description = "Spring Boot Artifact-online Documentation",
								url = "https://www.tjtechy.com/artifact/artifact-online.html"
				)

)
public class TjtechyArtifactsOnlineApplication {

	public static void main(String[] args) {

		SpringApplication.run(TjtechyArtifactsOnlineApplication.class, args);
	}

	//managing IdWorker
	@Bean
	public IdWorker idWorker(){
		return new IdWorker(1, 1);
	}


}
//login username = john, password = 123456
//#http://localhost:8080/h2-console
//http://localhost:8080/swagger-ui/index.html