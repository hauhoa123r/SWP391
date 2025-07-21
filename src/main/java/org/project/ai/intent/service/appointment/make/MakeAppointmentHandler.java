package org.project.ai.intent.service.appointment.make;

import org.project.ai.chat.AIService;
import org.project.ai.intent.BasePromptHandler;
import org.project.ai.prompt.service.appointment.MakeAppointmentPrompt;
import org.project.model.request.ChatMessageRequest;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class MakeAppointmentHandler
        extends BasePromptHandler<MakeAppointmentPrompt> {


    public MakeAppointmentHandler(AIService aiService, MakeAppointmentPrompt makeAppointmentPrompt) {
        super(aiService, makeAppointmentPrompt);
    }

    @Override
    protected String buildPrompt(ChatMessageRequest chatMessageRequest, String historyWithUser) throws IOException {
        return prompt.buildPrompt(chatMessageRequest, historyWithUser);
    }

    @Override
    public String contextType() {
        return "make_appointment";
    }
}
