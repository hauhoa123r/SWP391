package org.project.ai.converter.appointment.make_appointment;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.project.ai.chat.AIService;
import org.project.model.dai.PatientBasicInfo;
import org.project.model.request.ChatMessageRequest;
import org.project.utils.RequestMapper;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class FilterDataPatientAppointment {

    private final AIService aiService;
    private final ObjectMapper objectMapper;

    public FilterDataPatientAppointment(AIService aiService) {
        this.aiService = aiService;
        this.objectMapper = new ObjectMapper();
    }

    public String extractRawJson(ChatMessageRequest req, String history) {
        String prompt = buildPrompt(req, history);
        return aiService.extractStructuredData(prompt);
    }

    public PatientBasicInfo parseExtracted(String jsonString) {
        if (jsonString == null || jsonString.trim().isEmpty() || "null".equalsIgnoreCase(jsonString.trim())) {
            return new PatientBasicInfo();
        }

        try {
            Map<String, Object> map = objectMapper.readValue(jsonString, new TypeReference<>() {});
            return RequestMapper.mapFromMap(map, PatientBasicInfo.class);
        } catch (JsonProcessingException e) {
            return new PatientBasicInfo();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public PatientBasicInfo extractData(ChatMessageRequest req, String history) {
        String json = extractRawJson(req, history);
        return parseExtracted(json);
    }

    private String buildPrompt(ChatMessageRequest req, String history) {
        return String.format("""
            Your task is to extract structured data from multilingual user input.
            Return only a raw JSON object. No explanations. No formatting. No ``` markers.

            You support **all languages** (Vietnamese, English, French, Japanese, Chinese, Arabic, etc.).
            The user message is written in the language code: `%s`.

            Extract and return a JSON object with exactly these fields (use `null` if missing):

            {
              "userId": %s,
              "fullName": "string or null",  // Name of the person the appointment is for
              "familyRelationship": "SELF,FATHER,MOTHER,BROTHER,SISTER,SON,DAUGHTER,GRANDFATHER,GRANDMOTHER,CAUSIN,AUNT,UNCLE,OTHER",
            }

            Rules:
            - If the user says "I want to book for myself", set fullName = null, familyRelationship = "self"
            - If the user says "for my mother", "for my daughter", extract relationship accordingly.
            - If name is mentioned (e.g. 'Nguyễn Văn A'), extract as fullName.
            - Do not guess or assume missing data. Use null if unsure.

            --- Conversation history:
            %s

            --- User message:
            %s
            """, req.getLanguage(),req.getUserId(), history, req.getUserMessage());
    }
}
