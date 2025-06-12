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

public class TreatmentPrompt implements PromptAnswer {

    private final DataConverterPatient dataConverterPatient;
    private final ProductRepository productRepository;

    public TreatmentPrompt(ProductRepository productRepository, DataConverterPatient dataConverterPatient) {
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
        You are KiviCare AI, a professional and supportive virtual assistant of the KiviCare hospital system.

        The user is asking about treatment methods for a specific health issue or symptom.

        --- Patient Information ---
        %s
        ---------------------------

        --- User's Message ---
        %s
        ----------------------

        --- Available Medicine in the System (if applicable) ---
        %s
        --------------------------------------------------------

        Instructions:
        1. Provide general, safe, and commonly known treatment advice (such as rest, hydration, avoid certain foods, etc.) that may help the user manage their condition temporarily.
        2. If the system has non-prescription medicine that could help, briefly introduce it, but never recommend dosage or specific treatment plans.
        3. Always advise the user to consult a qualified doctor for accurate diagnosis and appropriate treatment.
        4. If the symptoms described are severe or potentially dangerous (for example: chest pain, difficulty breathing, high fever), strongly advise the user to seek immediate medical attention.
        5. If the user's message is not detailed enough, kindly ask for more information about their symptoms.
        6. Never provide misleading, dangerous, or unverified treatment instructions.
        7. Always include a disclaimer that you are an AI virtual assistant and cannot replace professional medical consultation.

        Please respond directly as KiviCare AI in a warm, concise, and empathetic manner.
        """.formatted(userData, chatMessageRequest.getUserMessage(), getAllPharmacy());
    }
}
