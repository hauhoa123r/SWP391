package org.project.enums;

import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Collectors;

public enum Intent {
    MAKE_APPOINTMENT("make_appointment", "This is the step to make an appointment, doctor, hospital visit with address."),
    SYMPTOM_ADVICE("symptom_advice", "These are the patient's symptoms."),
    ASK_MEDICINE("ask_medicine", "This is a question about medicine."),
    ASK_HOSPITAL("ask_hospital", "This is a user question about where is the hospital, where is your hospital (what locations do you have)."),
    ASK_YOURSELF("ask_yourself", "This is a question about you, you are a virtual assistant of the KiViCare system"),
    ASK_DOCTOR("ask_doctor", "This is a request for information about a doctor."),
    SELF_IDENTIFICATION("self_identification", "This is the kind of question, do you know who I am?"),
    HEALTH_EFFECT("health_effect", "This is asking if a certain issue affects their health."),
    PERSONAL_HEALTH_QUESTION("personal_health_question", "This is a health question about the customer himself"),
    TREATMENT("treatment", "This is how to ask for treatment"),
    CHITCHAT("chitchat", "Casual conversations such as small talk, personal sharing, or polite social phrases that are NOT greetings, thank you, or good bye messages."),
    EMOTIONAL_SUPPORT("emotional_support", "This is a message where the user expresses feelings of sadness, disappointment, frustration, or emotional distress, requiring comforting or motivational responses."),
    DIET_ADVICE("diet_advice","These are phrases about eat."),
    ADVICE("advice","This is a user asking for your advice on good health."),
    CANCEL_APPOINTMENT("cancel_appointment", "This is a request to cancel an appointment."),
    RESCHEDULE_APPOINTMENT("reschedule_appointment", "This is a request to reschedule an appointment."),
    ORDER_MEDICINE("order_medicine", "This is a request to order medicine."),
    CHECK_MEDICINE_STOCK("check_medicine_stock", "This is a request to check the stock of medicine."),
    CANCEL_MEDICINE_ORDER("cancel_medicine_order", "This is a request to cancel a medicine order and possibly request a refund."),
    REQUEST_REFUND("request_refund", "This is a request to get a refund for a cancelled service or medicine order."),
    OUT_OF_SCOPE("out_of_scope", "This is a question or message unrelated to the hospital, healthcare, medicine, appointments, or the system's functionalities."),
    UNKNOWN("unknown", "");

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

