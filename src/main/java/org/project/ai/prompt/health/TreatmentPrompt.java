package org.project.ai.prompt.health;

import org.project.ai.prompt.PromptAnswer;
import org.project.model.request.ChatMessageRequest;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class TreatmentPrompt implements PromptAnswer {
    @Override
    public String buildPrompt(ChatMessageRequest chatMessageRequest, String historyWithUser) throws IOException, IllegalAccessException {
        return "";
    }
}
