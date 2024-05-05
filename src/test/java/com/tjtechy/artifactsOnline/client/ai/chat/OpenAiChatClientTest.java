package com.tjtechy.artifactsOnline.client.ai.chat;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tjtechy.artifactsOnline.client.ai.chat.dto.ChatRequest;
import com.tjtechy.artifactsOnline.client.ai.chat.dto.ChatResponse;
import com.tjtechy.artifactsOnline.client.ai.chat.dto.Choice;
import com.tjtechy.artifactsOnline.client.ai.chat.dto.Message;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;

import java.util.List;


import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.catchThrowable;
import static org.hamcrest.Matchers.startsWith;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.*;

@RestClientTest(OpenAiChatClient.class)
class OpenAiChatClientTest {

  @Autowired
  private OpenAiChatClient openAiChatClient;
  @Autowired
  private MockRestServiceServer mockServer;

  @Autowired
  private ObjectMapper objectMapper;

  private String url;

  private ChatRequest chatRequest;

  @BeforeEach
  void setUp() {
    this.url = "https://api.openai.com/v1/chat/completions";

    this.chatRequest = new ChatRequest("gpt-3.5-turbo", List.of(
            new Message("system", "Your task is to generate a short summary of a given JSON array in at most 100 words. The summary must include the number of artifacts, each artifact's description and the ownership information.  Don't mention that the summary is from a given JSON array"),
            new Message("user", "A json Array.")));
  }
  //1. success
  @Test
  void testGenerateSuccess() throws JsonProcessingException {
    //Given
    //prepare a fake data
    //this data is put in the beforeEach so other tests can share it
//    ChatRequest chatRequest = new ChatRequest("gpt-3.5-turbo", List.of(
//            new Message("system", "Your task is to generate a short summary of a given JSON array in at most 100 words. The summary must include the number of artifacts, each artifact's description and the ownership information.  Don't mention that the summary is from a given JSON array"),
//            new Message("user", "A json Array.")));
    //Given
    ChatResponse chatResponse = new
            ChatResponse(List.of(new Choice(0, new Message("assistant", "The summary includes six artifacts, owned by three different wizards"))));
    this.mockServer.expect(requestTo(this.url))
            .andExpect(method(HttpMethod.POST))
            .andExpect(header("Authorization", startsWith("Bearer ")))
            .andExpect(content().json(this.objectMapper.writeValueAsString(chatRequest)))
            .andRespond(withSuccess(this.objectMapper.writeValueAsString(chatResponse), MediaType.APPLICATION_JSON));

    //When
    ChatResponse generatedChatResponse = this.openAiChatClient.generate(this.chatRequest);

    //Then
    this.mockServer.verify();//Verify that all expected requests set up via expect and andExpect were indeed performed
    assertThat(generatedChatResponse.choices().get(0).message().content())
            .isEqualTo("The summary includes six artifacts, owned by three different wizards");
  }

  //2. Not success
  //a. unauthorized
  @Test
  void testGenerateUnauthorizedRequest(){
    //Given
    this.mockServer.expect(requestTo(this.url))
            .andExpect(method(HttpMethod.POST))
            .andRespond(withUnauthorizedRequest()); //mockServer will return a 401

    //When
    Throwable thrown = catchThrowable(() -> {
      ChatResponse generatedChatResponse = this.openAiChatClient.generate(this.chatRequest);
    });

    //Then
    this.mockServer.verify(); //all expected requests were performed
    assertThat(thrown)
            .isInstanceOf(HttpClientErrorException.Unauthorized.class);

  }

  //b. this simulates a scenario where the service receives a 429 Quota Exceeded response
  @Test
  void testGenerateQuotaExceeded(){
    //Given
    this.mockServer.expect(requestTo(this.url))
            .andExpect(method(HttpMethod.POST))
            .andRespond(withTooManyRequests());
    //When
    Throwable thrown = catchThrowable(() -> {
      ChatResponse chatResponse = this.openAiChatClient.generate(chatRequest);
    });

    //Then
    this.mockServer.verify();
    assertThat(thrown)
            .isInstanceOf(HttpClientErrorException.TooManyRequests.class);
  }

  //c. this simulates a scenario where the service receives a 500 internal server error
  @Test
  void testGenerateServerError(){
    //Given
    this.mockServer.expect(requestTo(this.url))
            .andExpect(method(HttpMethod.POST))
            .andRespond(withServerError());
    //When
    Throwable thrown = catchThrowable(() -> {
      ChatResponse chatResponse = this.openAiChatClient.generate(chatRequest);
    });

    //Then
    this.mockServer.verify();
    assertThat(thrown)
            .isInstanceOf(HttpServerErrorException.InternalServerError.class);
  }

  //d. this simulates a scenario where the service receives a 503 service unavailable response
  @Test
  void testGenerateServerOverloaded(){
    //Given
    this.mockServer.expect(requestTo(this.url))
            .andExpect(method(HttpMethod.POST))
            .andRespond(withServiceUnavailable());
    //When
    Throwable thrown = catchThrowable(() -> {
      ChatResponse chatResponse = this.openAiChatClient.generate(chatRequest);
    });

    //Then
    this.mockServer.verify();
    assertThat(thrown)
            .isInstanceOf(HttpServerErrorException.ServiceUnavailable.class);
  }

}


/*Test class for the openaichatclient (which depends on restClient to interact with the open AI API
* WE NEED to mock the behavior of the restClient
*
*
*
* */