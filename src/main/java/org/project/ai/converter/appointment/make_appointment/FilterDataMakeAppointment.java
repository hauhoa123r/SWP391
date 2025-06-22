package org.project.ai.converter.appointment.make_appointment;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.project.ai.chat.AIService;
import org.project.converter.AppointmentConverter;
import org.project.model.dai.AppointmentDAI;
import org.project.model.request.ChatMessageRequest;
import org.project.utils.RequestMapper;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

@Component
public class FilterDataMakeAppointment {
    private final AIService aiService;
    private final AppointmentConverter appointmentConverter;
    private final ObjectMapper mapper;

    public FilterDataMakeAppointment(AIService aiService,
                                     AppointmentConverter appointmentConverter,
                                     ObjectMapper mapper) {
        this.aiService = aiService;
        this.appointmentConverter = appointmentConverter;
        this.mapper = new ObjectMapper();
    }

    public String extractRawData(ChatMessageRequest req, String history) throws IOException {
        String prompt = promptToGetDataMakeAppointment(req, history);
        return aiService.extractStructuredData(prompt);
    }

    public AppointmentDAI parseExtractedData(String jsonString) throws JsonProcessingException {
        if (jsonString == null
                || jsonString.trim().isEmpty()
                || "null".equalsIgnoreCase(jsonString.trim())) {
            return new AppointmentDAI();
        }
        try {
            Map<String, Object> map = mapper.readValue(jsonString, new TypeReference<>() {});
            return RequestMapper.mapFromMap(map, AppointmentDAI.class);
        } catch (JsonProcessingException e) {
            return new AppointmentDAI();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public AppointmentDAI extractData(ChatMessageRequest req, String history) throws IOException {
        String json = extractRawData(req, history);
        return parseExtractedData(json);
    }

    private String promptToGetDataMakeAppointment(ChatMessageRequest req, String history) {
        String today = new SimpleDateFormat("dd/MM/yyyy").format(new Date());
        return String.format("""
        Your task is to extract structured data from multilingual user input.
        Return only a raw JSON object. No explanations. No formatting. No ``` markers.

        You support **all languages** (Vietnamese, English, French, Japanese, Chinese, Arabic, etc.).

        The user message is written in the language code: `%s`.

        Extract and return a JSON with exactly these fields (use null if missing):

        {
          "userId": %s  
          "date": "dd/MM/yyyy or null",
          "time": "HH:mm or null",
          "doctorName": "exact doctor name if mentioned, or 'random' if user wants any doctor, or null if unclear"
          "hospitalName": "string or null",
          "departmentName": "string or null",
          "patientName": "string or null",
          "isFormStarted": "true or false",
          "language": "%s"
        }

        Rules:
        • If the user provides any **specific detail** (like date, doctor, department, hospital...), set `isFormStarted = true`.
        • If the user is only asking generally about booking (e.g. “how do I book?”, “can you help me?”, “I want to schedule”) → set `isFormStarted = false`.
        • Translate relative terms like "tomorrow", "ngày mai", etc., to absolute dates based on %s.
        • If unsure about time, use 09:00 for "morning", 14:00 for "afternoon"; or null.

        --- Conversation history:
        %s

        --- User message:
        %s
        """, req.getUserId() ,req.getUserId(),req.getLanguage(), req.getLanguage(), today, history, req.getUserMessage());
    }



}
