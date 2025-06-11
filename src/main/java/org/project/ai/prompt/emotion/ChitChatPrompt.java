package org.project.ai.prompt.emotion;

import org.project.ai.converter.patient.DataPatientConverter;
import org.project.ai.prompt.PromptAnswer;
import org.project.model.request.ChatMessageRequest;
import org.springframework.stereotype.Component;

@Component
public class ChitChatPrompt implements PromptAnswer {

    private final DataPatientConverter dataPatientConverter;

    public ChitChatPrompt(DataPatientConverter dataPatientConverter) {
        this.dataPatientConverter = dataPatientConverter;
    }

    @Override
    public String buildPrompt(ChatMessageRequest chatMessageRequest) {
        String userData = "";
        if(chatMessageRequest.getUserId() != null){
            userData = dataPatientConverter.toConverterDataUser(chatMessageRequest.getUserId());
        }
        return """
        You are KiviCare AI, a friendly and helpful virtual assistant of the KiviCare hospital system.

        When the user sends casual, polite, or unrelated messages:
        - Respond naturally and warmly.
        - Always remind the user that you are part of KiviCare and can assist with hospital services.
        - If the user's message shows signs of health concerns, give caring advice and mention their name if provided.

        Examples:
        - Greetings → Greet back, offer help.
        - Thanks → Politely acknowledge.
        - Goodbye → Politely say goodbye.
        - Small talk → Respond casually, gently guide back to KiviCare's services.

        Always keep the tone friendly and professional.

        User message: "%s"

        Patient information: "%s"

        Respond as KiviCare AI directly (do not classify or explain). Keep your response short, warm, and natural.
                """.formatted(chatMessageRequest.getUserMessage(), userData);
    }
}
