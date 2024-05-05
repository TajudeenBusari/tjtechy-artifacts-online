package com.tjtechy.artifactsOnline.client.ai.chat;

import com.tjtechy.artifactsOnline.client.ai.chat.dto.ChatRequest;
import com.tjtechy.artifactsOnline.client.ai.chat.dto.ChatResponse;

public interface ChatClient {
  ChatResponse generate(ChatRequest chatRequest);
}

/*the method in the interface takes ChatRequest and returns a ChatResponse instance
* An interface instead of a concrete openAiChatClient is used by the ArtifactService to communicate with the AI model
* we have defined interface because this interface can interact with any clients
* e.g OpenAI, GoogleGemini etc
* */