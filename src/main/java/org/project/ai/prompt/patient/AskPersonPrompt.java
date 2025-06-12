package org.project.ai.prompt.patient;

import org.project.ai.prompt.PromptAnswer;
import org.project.model.request.ChatMessageRequest;
import org.springframework.stereotype.Component;

@Component
public class AskPersonPrompt implements PromptAnswer {
    @Override
    public String buildPrompt(ChatMessageRequest chatMessageRequest, String historyWithUser) {
        String userMessage = chatMessageRequest.getUserMessage();

        return String.format("User asked: \"%s\". Please clarify whether the user is asking about a doctor or a patient. " +
                "Ask the user: 'Do you want to ask about a doctor or a patient?'", userMessage);
    }
}
