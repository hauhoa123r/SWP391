package org.project.ai.intent;

import org.project.ai.chat.AIService;
import org.project.ai.prompt.IntentPrompt;
import org.project.enums.Intent;
import org.project.model.request.ChatMessageRequest;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class ChatRouterService {

    private final AIService aiService;
    private final IntentPrompt intentPrompt;
    private final IntentHandlerRegistry handlerRegistry;

    public ChatRouterService(AIService aiService,
                             IntentPrompt intentPrompt,
                             IntentHandlerRegistry handlerRegistry) {
        this.aiService = aiService;
        this.intentPrompt = intentPrompt;
        this.handlerRegistry = handlerRegistry;
    }

    private String findContextType(ChatMessageRequest userMessage, String historyWithUser) {
        String prompt = intentPrompt.buildPrompt(userMessage, historyWithUser);
        return aiService.fulfillPrompt(prompt).toLowerCase();
    }

    public String convertToUserMessage(ChatMessageRequest chatMessageRequest, String historyWithUser) throws IOException {
        chatMessageRequest.setPrompt(findContextType(chatMessageRequest, historyWithUser));
        if (Intent.fromKey(chatMessageRequest.getPrompt()).isEmpty() || ("unknown").equals(chatMessageRequest.getPrompt())) {

        }

        return handlerRegistry.handle(chatMessageRequest, historyWithUser);
    }
}
