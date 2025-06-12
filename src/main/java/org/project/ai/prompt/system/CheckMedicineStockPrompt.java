package org.project.ai.prompt.system;

import org.project.ai.prompt.PromptAnswer;
import org.project.model.request.ChatMessageRequest;
import org.springframework.stereotype.Component;

@Component
public class CheckMedicineStockPrompt implements PromptAnswer {
    @Override
    public String buildPrompt(ChatMessageRequest chatMessageRequest, String historyWithUser) {
        return "";
    }
}
