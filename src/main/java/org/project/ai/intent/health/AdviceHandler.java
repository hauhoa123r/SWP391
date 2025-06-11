package org.project.ai.intent.health;

import org.project.ai.chat.AIService;
import org.project.ai.intent.BasePromptHandler;
import org.project.ai.prompt.health.AdvicePrompt;
import org.project.model.request.ChatMessageRequest;

public class AdviceHandler extends BasePromptHandler<AdvicePrompt> {
    public AdviceHandler(AIService aiService, AdvicePrompt advicePrompt) {
        super(aiService, advicePrompt);
    }

    @Override
    protected String buildPrompt(ChatMessageRequest chatMessageRequest) {
        return prompt.buildPrompt(chatMessageRequest);
    }

    @Override
    public String contextType() {
        return "advice";
    }
}
