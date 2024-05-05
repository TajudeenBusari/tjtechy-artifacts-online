package com.tjtechy.artifactsOnline.client.ai.chat;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientBuilderConfiguration {

  @Bean
  public RestClient.Builder restClientBuilder(){
    return RestClient.builder()
            .requestFactory(new JdkClientHttpRequestFactory());
  }



}



/*this class builds the rest client
* we create an instance of restClient builder and specift the
* http library to be used*/