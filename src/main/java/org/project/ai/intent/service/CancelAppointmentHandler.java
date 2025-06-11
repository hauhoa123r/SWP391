package org.project.ai.intent.service;

import org.project.ai.chat.AIService;
import org.project.ai.intent.BasePromptHandler;
import org.project.ai.prompt.service.CancelAppointmentPrompt;
import org.project.model.request.ChatMessageRequest;

public class CancelAppointmentHandler extends BasePromptHandler<CancelAppointmentPrompt> {
    public CancelAppointmentHandler(AIService aiService, CancelAppointmentPrompt cancelAppointmentPrompt) {
        super(aiService, cancelAppointmentPrompt);
    }

    @Override
    protected String buildPrompt(ChatMessageRequest chatMessageRequest) {
        return prompt.buildPrompt(chatMessageRequest);
    }

    @Override
    public String contextType() {
        return "cancel_appointment";
    }
}
