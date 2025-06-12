package org.project.ai.prompt;

import org.project.enums.Intent;
import org.project.model.request.ChatMessageRequest;
import org.springframework.stereotype.Component;
@Component
public class IntentPrompt implements PromptStrategy {

    @Override
    public String buildPrompt(ChatMessageRequest userMessage, String historyWithUser) {
        return """
        Classify the user's intent into one of the following labels:
        %s
        
        Question: "%s"

        Respond only with the corresponding label (no explanation).
        """.formatted(Intent.getAllDescriptions(), userMessage.getUserMessage());
    }

}
