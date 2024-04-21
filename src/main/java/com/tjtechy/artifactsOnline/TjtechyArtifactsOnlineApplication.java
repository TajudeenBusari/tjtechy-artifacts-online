package com.tjtechy.artifactsOnline;

import com.tjtechy.artifactsOnline.artifact.utils.IdWorker;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
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
