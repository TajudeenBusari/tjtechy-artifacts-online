package com.tjtechy.artifactsOnline.client.ai.chat.dto;

public record Message(String role,
                      String content) {
}



/*role: can be System, User, Assistant
*content: can be instructions,Questions,Example,Statement
*
* */