package org.project.ai.chat;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.stereotype.Service;

@Service
public class OpenAiServiceImpl implements AIService {
    private final ChatClient chatClient35;
    private final ChatClient chatClient4;

    private static final String SYSTEM_PROMPT_HUMANIZE = """
        You are a chatbot message formatter.
        
        Your task is to rewrite internal system messages so they sound polite, natural, and user-friendly.

        Rules:
        - Do not change the meaning.
        - Respond in the same language as the original message.
        - Be concise, friendly, and professional.
        - Do not add extra suggestions or explanations.
    """;

    private static final String SYSTEM_PROMPT_FULL_AI = """
        You are a multilingual virtual assistant for the KiViCare hospital system.

        Your responsibilities include:
        - Understanding user requests (e.g., appointments, symptoms, doctors).
        - Asking for missing information.
        - Translating or formatting content as requested.
        - Responding only in the user's language.

        Always follow the instructions from the prompt exactly.
        Never invent information. Be helpful and clear.
    """;

    public OpenAiServiceImpl(ChatClient.Builder builder) {
        this.chatClient35 = builder
                .defaultOptions(OpenAiChatOptions.builder()
                        .model("gpt-3.5-turbo")
                        .build())
                .build();

        this.chatClient4 = builder
                .defaultOptions(OpenAiChatOptions.builder()
                        .model("gpt-4o")
                        .build())
                .build();
    }

    @Override
    public String humanize(String rawMessage) {
        return chatClient35
                .prompt()
                .system(SYSTEM_PROMPT_HUMANIZE)
                .user(rawMessage)
                .call()
                .content()
                .trim();
    }

    @Override
    public String fulfillPrompt(String fullInstructionPrompt) {
        return chatClient4
                .prompt()
                .system(SYSTEM_PROMPT_FULL_AI)
                .user(fullInstructionPrompt)
                .call()
                .content()
                .trim();
    }
    @Override
    public String extractStructuredData(String extractionPrompt) {
        return chatClient35
                .prompt()
                .system("""
                You are a structured data extraction engine.

                You will always receive a prompt with clear fields and rules.
                Your job is to return only a raw JSON object, without explanations or markdown formatting.
                Do not include ``` or any commentary.

                Format must be strictly valid JSON. All missing fields must be set to null.
                """)
                .user(extractionPrompt)
                .call()
                .content()
                .trim();
    }

}
