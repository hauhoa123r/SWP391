package org.project.ai.prompt;

import org.project.enums.Intent;
import org.project.model.request.ChatMessageRequest;
import org.springframework.stereotype.Component;
@Component
public class IntentPrompt implements PromptStrategy {

    @Override
    public String buildPrompt(ChatMessageRequest userMessage, String historyWithUser) {
        return """
        You are a highly accurate intent classification system for the KiviCare hospital virtual assistant.

        Task:
        - Based on the user's current message and the previous conversation history, classify the user's intent into one of the following labels:
        %s

        Notes:
        - Use the conversation history to understand the context and avoid misclassification.
        - Prioritize the most recent user message but always consider the conversation flow.

        Conversation history:
        %s

        Current user message:
        "%s"

        Respond ONLY with the exact corresponding label (no explanation, no extra words).
        """.formatted(Intent.getAllDescriptions(), historyWithUser, userMessage.getUserMessage());
    }

}
