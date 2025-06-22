package org.project.ai.converter.patient;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.project.ai.chat.AIService;
import org.project.model.dto.PatientDTO;
import org.project.model.request.ChatMessageRequest;
import org.project.utils.RequestMapper;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class FilterDataCreatePatient {

    private final AIService aiService;
    private final ObjectMapper objectMapper;

    public FilterDataCreatePatient(AIService aiService) {
        this.aiService = aiService;
        this.objectMapper = new ObjectMapper();
    }

    public String extractRawData(ChatMessageRequest req, String history) {
        String prompt = buildPrompt(req, history);
        return aiService.extractStructuredData(prompt);
    }

    public PatientDTO parseExtractedData(String jsonString) {
        if (jsonString == null || jsonString.trim().isEmpty() || "null".equalsIgnoreCase(jsonString.trim())) {
            return new PatientDTO();
        }
        try {
            Map<String, Object> map = objectMapper.readValue(jsonString, new TypeReference<>() {});
            return RequestMapper.mapFromMap(map, PatientDTO.class);
        } catch (JsonProcessingException e) {
            return new PatientDTO();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public PatientDTO extractData(ChatMessageRequest req, String history) {
        String json = extractRawData(req, history);
        return parseExtractedData(json);
    }

    private String buildPrompt(ChatMessageRequest req, String history) {
        return String.format("""
            Your task is to extract structured data to create a new patient profile.
            Return only a raw JSON object. No explanations. No formatting. No ``` markers.

            You support **all languages** (Vietnamese, English, French, Japanese, Chinese, Arabic, etc.).
            The user message is written in the language code: `%s`.

            Extract and return a JSON with exactly these fields (use null if missing):

            {
              "userId": %s  
              "fullName": "string or null",
              "gender": "MALE, FEMALE, or OTHER",
              "dateOfBirth": "yyyy-MM-dd or null",
              "phoneNumber": "string or null",
              "email": "string or null",
              "address": "string or null",
              "familyRelationship": "SELF,FATHER,MOTHER,BROTHER,SISTER,SON,DAUGHTER,GRANDFATHER,GRANDMOTHER,CAUSIN,AUNT,UNCLE,OTHER",
              "bloodType": "A, B, AB, O or null"
              "isFormStarted": "true or false",
            }

            Rules:
            • If the user provides any **specific detail** (like Name, Gender, dateOfBirth, familyRelationship...), set `isFormStarted = true`.
            • If the user is only asking generally about booking (e.g. “how do I create patient?”, “can you help me?”, “I want to create patient”) → set `isFormStarted = false`.
            • Use only the information clearly mentioned by the user or in the conversation history.
            • Do not guess or assume anything.
            • If the user refers to someone (e.g., “my mother”, “my child”), use `familyRelationship` accordingly.
            • If gender is unclear, return null. Do not assume based on name.
            • Format dateOfBirth as yyyy-MM-dd if possible. Else, use null.

            --- Conversation history:
            %s

            --- User message:
            %s
            """, req.getLanguage(), req.getUserId(), history, req.getUserMessage());
    }
}
