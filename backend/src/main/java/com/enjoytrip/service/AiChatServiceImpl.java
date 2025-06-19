package com.enjoytrip.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AiChatServiceImpl implements AiChatService {
	private final ChatClient simpleChatClient;

	@Override
	public Object simpleGeneration(String userInput) {
   	 var responseSpec = simpleChatClient
			 .prompt()
   			 .system(spec -> spec.param("language", "korean").param("character", "chill")).user(userInput)
   			 .call();
   	 return responseSpec.content();

    } 
}
