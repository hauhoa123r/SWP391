package org.project.ai.intent.system;

import org.project.ai.chat.AIService;
import org.project.ai.intent.BasePromptHandler;
import org.project.ai.prompt.system.CheckMedicineStockPrompt;
import org.project.model.request.ChatMessageRequest;
import org.springframework.stereotype.Component;

@Component
public class CheckMedicineStockHandler extends BasePromptHandler<CheckMedicineStockPrompt> {
    public CheckMedicineStockHandler(AIService aiService, CheckMedicineStockPrompt checkMedicineStockPrompt) {
        super(aiService, checkMedicineStockPrompt);
    }

    @Override
    protected String buildPrompt(ChatMessageRequest chatMessageRequest) {
        return prompt.buildPrompt(chatMessageRequest);
    }

    @Override
    public String contextType() {
        return "check_medicine_stock";
    }
}
