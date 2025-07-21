package org.project.ai.prompt;

import org.project.model.request.ChatMessageRequest;

import java.io.IOException;

public interface PromptAnswer {
    String buildPrompt(ChatMessageRequest chatMessageRequest, String historyWithUser) throws IOException, IllegalAccessException;
}
