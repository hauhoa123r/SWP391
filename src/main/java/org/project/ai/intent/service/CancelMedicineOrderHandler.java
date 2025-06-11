package org.project.ai.intent.service;

import org.project.ai.chat.AIService;
import org.project.ai.intent.BasePromptHandler;
import org.project.ai.prompt.service.CancelMedicineOrderPrompt;
import org.project.model.request.ChatMessageRequest;

public class CancelMedicineOrderHandler extends BasePromptHandler<CancelMedicineOrderPrompt> {

    public CancelMedicineOrderHandler(AIService aiService, CancelMedicineOrderPrompt cancelMedicineOrderPrompt) {
        super(aiService, cancelMedicineOrderPrompt);
    }

    @Override
    protected String buildPrompt(ChatMessageRequest chatMessageRequest) {
        return prompt.buildPrompt(chatMessageRequest);
    }

    @Override
    public String contextType() {
        return "cancel_medicine_order";
    }
}
