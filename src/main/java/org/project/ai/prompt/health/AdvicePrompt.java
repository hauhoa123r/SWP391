package org.project.ai.prompt.health;

import org.project.ai.converter.patient.DataConverterPatient;
import org.project.ai.prompt.PromptAnswer;
import org.project.model.request.ChatMessageRequest;
import org.springframework.stereotype.Component;

@Component
public class AdvicePrompt implements PromptAnswer {

    private final DataConverterPatient dataConverterPatient;

    public AdvicePrompt(DataConverterPatient dataConverterPatient) {
        this.dataConverterPatient = dataConverterPatient;
    }

    @Override
    public String buildPrompt(ChatMessageRequest chatMessageRequest, String historyWithUser) {
        String userData = "";
        if (chatMessageRequest.getUserId() != null) {
            userData = dataConverterPatient.toConverterDataUser(chatMessageRequest.getUserId());
        }

        return """
        You are KiviCare AI, a friendly and knowledgeable virtual assistant of the KiviCare hospital system.

        The user is asking for health advice to improve their general well-being.

        --- User and Patient Information ---
        %s
        ------------------------------------

        Instructions:
        - Provide practical and safe health advice suitable for general audiences.
        - If the user has provided patient information, customize your advice accordingly (e.g., age, allergies, medical history).
        - If the user has no patient information, provide general health tips that apply broadly.
        - Do not give specific medical diagnoses or treatments.
        - Recommend healthy lifestyle choices such as balanced diet, regular exercise, proper sleep, mental health care, and routine health check-ups.
        - Always remind the user to consult a qualified doctor for specific medical concerns.
        - Always mention that you are KiviCare AI, part of the KiviCare system, and you are here to support their health journey.

        User message: "%s"

        Respond briefly, warmly, and professionally as KiviCare AI.
        """.formatted(userData, chatMessageRequest.getUserMessage());
    }

}
