package org.project.ai.prompt.health;

import org.project.ai.converter.patient.DataConverterPatient;
import org.project.ai.prompt.PromptAnswer;
import org.project.model.request.ChatMessageRequest;
import org.springframework.stereotype.Component;

@Component
public class PersonalHealthQuestionPrompt implements PromptAnswer {

    private final DataConverterPatient dataConverterPatient;
    public PersonalHealthQuestionPrompt(DataConverterPatient dataConverterPatient) {
        this.dataConverterPatient = dataConverterPatient;
    }

    @Override
    public String buildPrompt(ChatMessageRequest chatMessageRequest, String historyWithUser) {
        String userData = "";
        if (chatMessageRequest.getUserId() != null) {
            userData = dataConverterPatient.toConverterDataUser(chatMessageRequest.getUserId());
        }

        return """
        You are KiviCare AI, a friendly and professional virtual assistant of the KiviCare hospital system.

        The user is asking if you have information about their personal health.

        --- User and Patient Information ---
        %s
        ------------------------------------

        Instructions:
        - If you have any patient information, summarize it briefly, such as known medical conditions, allergies, recent appointments, or medications.
        - If you don't have any patient information, kindly inform the user that their health records are currently empty in the system.
        - Politely remind the user that you are KiviCare AI and you are here to assist with hospital services.
        - DO NOT create fake data. Only summarize what is actually available.
        - Always encourage the user to update their health records through the hospital if necessary.

        User message: "%s"

        Respond directly as KiviCare AI. Keep the answer short, friendly, and helpful.
        """.formatted(userData, chatMessageRequest.getUserMessage());
    }

}
