package com.enjoytrip.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.InMemoryChatMemory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;

@Configuration
public class AiConfig {
	@Value("${ssafy.ai.system-prompt}")
    String systemPrompt;

    @Bean
    ChatMemory chatMemory() {
        return new InMemoryChatMemory();
    }

    @Bean()
    ChatClient simpleChatClient(ChatClient.Builder builder, ChatMemory chatMemory) {
        return builder.defaultSystem(systemPrompt)
        		.defaultAdvisors(
                        new SimpleLoggerAdvisor(Ordered.LOWEST_PRECEDENCE-1),
                        new MessageChatMemoryAdvisor(chatMemory))
        		.build();
    }


}
