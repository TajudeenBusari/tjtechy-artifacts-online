package com.tjtechy.artifactsOnline.client.ai.chat;

import com.tjtechy.artifactsOnline.client.ai.chat.dto.ChatRequest;
import com.tjtechy.artifactsOnline.client.ai.chat.dto.ChatResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class OpenAiChatClient implements ChatClient{

  private final RestClient restClient;

  public OpenAiChatClient(@Value("${ai.openai.endpoint}") String endpoint,
                          @Value("${ai.openai.api-key}") String apiKey,
                          RestClient.Builder restClientBuilder){
    this.restClient = restClientBuilder
            .baseUrl(endpoint)
            .defaultHeader("Authorization", "Bearer " + apiKey)
            .build();
  }

  @Override
  public ChatResponse generate(ChatRequest chatRequest) {
    return this.restClient
            .post()
            .contentType(MediaType.APPLICATION_JSON)
            .body(chatRequest)
            .retrieve()
            .body(ChatResponse.class);
  }
}
/*this class will implement our created chatClient interface
* define the open Ai end point properties in the development.yml file
*
*
* */