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
    ASK_PATIENT("ask_patient", "This is the kind of question, do you know who I am?"),
    GREETING("greeting", ""),
    HEALTH_EFFECT("health_effect", "This is asking if a certain issue affects their health."),
    ASK_HEALTH_PATIENT("ask_health_patient", "This is a health question about the customer himself"),
    TREATMENT("treatment", "This is how to ask for treatment"),
    DAILY("daily","These are everyday phrases like: hello, sorry."),
    EAT("eat","These are phrases about eat."),
    ADVICE("advice","This is a user asking for your advice on good health."),
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

