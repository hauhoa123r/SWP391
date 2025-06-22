package org.project.ai.intent.patient.create;

import org.project.ai.chat.AIService;
import org.project.ai.intent.BasePromptHandler;
import org.project.ai.prompt.patient.create.CreatePatientPrompt;
import org.project.model.request.ChatMessageRequest;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CreatePatientHandler extends BasePromptHandler<CreatePatientPrompt> {

    protected CreatePatientHandler(AIService aiService, CreatePatientPrompt createPatientPrompt) {
        super(aiService, createPatientPrompt);
    }

    @Override
    protected String buildPrompt(ChatMessageRequest req, String history) throws IOException{
        return prompt.buildPrompt(req, history);
    }

    @Override
    public String contextType() {
        return "create_patient";
    }
}
