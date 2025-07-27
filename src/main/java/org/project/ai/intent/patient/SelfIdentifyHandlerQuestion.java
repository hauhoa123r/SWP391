package org.project.ai.intent.patient;

import org.project.ai.chat.AIService;
import org.project.ai.intent.BasePromptHandler;
import org.project.ai.prompt.patient.SelfIdentificationPrompt;
import org.project.model.request.ChatMessageRequest;

import java.io.IOException;

public class SelfIdentifyHandlerQuestion extends BasePromptHandler<SelfIdentificationPrompt> {

    protected SelfIdentifyHandlerQuestion(AIService aiService, SelfIdentificationPrompt createPatientPrompt) {
        super(aiService, createPatientPrompt);
    }

    @Override
    protected String buildPrompt(ChatMessageRequest req, String history) throws IOException{
        return prompt.buildPrompt(req, history);
    }

    @Override
    public String contextType() {
        return "self_identification";
    }
}
