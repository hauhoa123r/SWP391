package org.project.ai.intent;

import org.project.ai.chat.AIService;
import org.project.model.request.ChatMessageRequest;

import java.io.IOException;
import java.io.UncheckedIOException;

public abstract class BasePromptHandler<TPrompt> implements IntentHandler {
    protected final AIService aiService;
    protected final TPrompt prompt;

    protected BasePromptHandler(AIService aiService, TPrompt prompt) {
        this.aiService = aiService;
        this.prompt    = prompt;
    }

    protected abstract String buildPrompt(ChatMessageRequest req, String history)
            throws IOException;

    @Override
    public String handle(ChatMessageRequest req, String history) {
        try {
            if(req.isIntentType()){
                return aiService.humanize(buildPrompt(req, history));
            }
            return aiService.fulfillPrompt(buildPrompt(req, history));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

}
