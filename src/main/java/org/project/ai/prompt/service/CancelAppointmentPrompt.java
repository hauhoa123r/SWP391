package org.project.ai.prompt.service;

import org.project.ai.prompt.PromptAnswer;
import org.project.model.request.ChatMessageRequest;

public class CancelAppointmentPrompt implements PromptAnswer {
    @Override
    public String buildPrompt(ChatMessageRequest chatMessageRequest) {
        return "";
    }
}
