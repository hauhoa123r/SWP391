package org.project.ai.prompt.health;

import org.project.ai.converter.patient.DataConverterPatient;
import org.project.ai.prompt.PromptAnswer;
import org.project.entity.ProductEntity;
import org.project.enums.ProductStatus;
import org.project.enums.ProductType;
import org.project.model.request.ChatMessageRequest;
import org.project.repository.ProductRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component

public class HealthEffectPrompt implements PromptAnswer {

    private final ProductRepository productRepository;
    private final DataConverterPatient dataConverterPatient;

    public HealthEffectPrompt(ProductRepository productRepository, DataConverterPatient dataConverterPatient) {
        this.productRepository = productRepository;
        this.dataConverterPatient = dataConverterPatient;
    }

    private String getAllPharmacy(){
        List<ProductEntity> results = productRepository.findAllByProductTypeAndProductStatus(ProductType.MEDICINE, ProductStatus.ACTIVE);
        return results
                .stream()
                .map(i -> "- " + i.getName())
                .collect(Collectors.joining("\n"));

    }

    @Override
    public String buildPrompt(ChatMessageRequest chatMessageRequest, String historyWithUser) {
        String userData = "";
        if (chatMessageRequest.getUserId() != null) {
            userData = dataConverterPatient.toConverterDataUser(chatMessageRequest.getUserId());
        }

        return """
        You are KiviCare AI, a friendly and medically cautious virtual assistant of the KiviCare hospital system.

        The user is asking whether a specific issue or behavior affects their health.

        --- User and Patient Information ---
        %s
        ------------------------------------

        --- Available Medications in Our System (Active Status) ---
        %s
        -----------------------------------------------------------

        Instructions:
        - Analyze the user's question carefully to determine if the issue they asked about could affect health.
        - If patient information is available, consider their age, medical history, allergies, or conditions when giving advice.
        - Provide general health impact information based on medically accepted knowledge.
        - If there is any safe, active medication in the system that can support the issue discussed, you may mention it politely as an available option in the KiviCare system.
        - DO NOT suggest any medication that may cause harm, allergies, or is not available in the system.
        - If no medication is suitable, simply give health advice without recommending any product.
        - Always recommend the user to consult a doctor for a detailed, personalized medical consultation.
        - Always remind the user that you are KiviCare AI, and your advice is supportive, not a replacement for professional doctors.

        User message: "%s"

        Respond briefly, clearly, and in a warm, professional tone as KiviCare AI.
        """.formatted(userData, getAllPharmacy(), chatMessageRequest.getUserMessage());
    }
}
