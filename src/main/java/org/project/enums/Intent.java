package org.project.enums;

import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Collectors;

public enum Intent {
    MAKE_APPOINTMENT("make_appointment", "This is the step to make an appointment, doctor, hospital visit with address."),
    SYMPTOM_ADVICE("symptom_advice", "This is when the user describes their personal health symptoms, such as feeling pain, discomfort, fever, coughing, or any signs of illness. It does not include accidents, injuries caused by external events (fighting, falling, traffic accidents), or issues caused by risky personal behaviors (substance abuse, violence)."),
    ASK_MEDICINE("ask_medicine", "This is a question about medicine, including inquiries about medication names, uses, dosage, possible side effects, or whether a specific medicine is suitable for the patient."),
    ASK_HOSPITAL("ask_hospital", "This is a user question about where is the hospital, where is your hospital (what locations do you have)."),
    SELF_IDENTIFICATION("self_identification", "This is the kind of question, do you know who I am?"),
    HEALTH_EFFECT("health_effect", "This is asking if a certain behavior, situation, event, or issue has an impact on the user's health. Example: Does drinking coffee at night affect my health?"),
    PERSONAL_HEALTH_QUESTION("personal_health_question", "This is when the user asks about their own health status or medical information stored in the system, such as: Do you know my blood pressure? or What is my medical history?"),
    TREATMENT("treatment", "This is a request for treatment methods, asking how to manage or cure a specific health issue or symptom. Example: How do I treat a sore throat?"),
    CHITCHAT("chitchat", "Casual conversations such as small talk, personal sharing, or polite social phrases that are greetings, thank you, or good bye messages."),
    EMOTIONAL_SUPPORT("emotional_support", "This is a message where the user expresses feelings of sadness, disappointment, frustration, stress, or emotional distress, requiring comforting or motivational responses."),
    DIET_ADVICE("diet_advice","These are questions about food, diet, or nutrition, including what to eat, what to avoid, dietary recommendations, or healthy eating habits."),
    ADVICE("advice","This is when the user asks for general health advice or lifestyle tips not specifically about medicine or diet. Example: How can I improve my sleep quality?"),
    CREATE_PATIENT("create_patient", "User wants to register a new patient record."),
    UNKNOWN("unknown", "This is when the user's message is not understandable, contains random characters, meaningless text, or cannot be classified into any known category. Example: 'asdkjh2131@#' or 'xxxxxx'.");


    private final String key;
    private final String description;

    Intent(String key, String description) {
        this.key = key;
        this.description = description;
    }

    public String getKey() {
        return key;
    }

    public String getDescription() {
        return description;
    }

    public static Optional<Intent> fromKey(String input) {
        for (Intent intent : Intent.values()) {
            if (intent.getKey().equalsIgnoreCase(input.trim())) {
                return Optional.of(intent);
            }
        }
        return Optional.empty();
    }

    public static String getAllDescriptions() {
        return Arrays.stream(Intent.values())
                .map(i -> "- " + i.getKey() + (i.getDescription().isEmpty() ? "" : ": " + i.getDescription()))
                .collect(Collectors.joining("\n"));
    }
}

