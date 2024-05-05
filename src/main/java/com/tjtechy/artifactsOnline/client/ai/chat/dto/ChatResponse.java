package com.tjtechy.artifactsOnline.client.ai.chat.dto;

import java.util.List;

public record ChatResponse(List<Choice> choices) {
}



/*Multiple choices are offered primarily by to provide diversity
and a range of options for users.
This feature is particularly useful in scenarios where there isn't
a single correct answer or where diff perspective or styles of response
might be valuable
*
*
* */