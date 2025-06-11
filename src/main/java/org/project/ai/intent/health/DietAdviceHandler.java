package org.project.ai.intent.health;

import org.project.ai.chat.AIService;
import org.project.ai.intent.BasePromptHandler;
import org.project.ai.prompt.health.DietAdvicePrompt;
import org.project.model.request.ChatMessageRequest;

public class DietAdviceHandler extends BasePromptHandler<DietAdvicePrompt> {
    public DietAdviceHandler(AIService aiService, DietAdvicePrompt dietAdvicePrompt) {
        super(aiService, dietAdvicePrompt);
    }

    @Override
    protected String buildPrompt(ChatMessageRequest chatMessageRequest) {
        return prompt.buildPrompt(chatMessageRequest);
    }

    @Override
    public String contextType() {
        return "diet_advice";
    }
}
