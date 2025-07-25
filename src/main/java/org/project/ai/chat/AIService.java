package org.project.ai.chat;

public interface AIService {
    String humanize(String rawMessage);
    String fulfillPrompt(String prompt);
    String extractStructuredData(String extractionPrompt);
}
