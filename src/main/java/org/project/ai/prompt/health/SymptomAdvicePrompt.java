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
public class SymptomAdvicePrompt implements PromptAnswer {

    private final DataConverterPatient dataConverterPatient;
    private final ProductRepository productRepository;

    public SymptomAdvicePrompt(ProductRepository productRepository, DataConverterPatient dataConverterPatient) {
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
        You are KiviCare AI, a highly professional virtual assistant for the KiviCare hospital system.

        The user is describing their personal health symptoms such as pain, fever, discomfort, coughing, or other signs of illness.
        Please carefully review the symptoms and provide helpful, safe, and empathetic advice based on the provided patient information.

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
        1. Analyze the user's described symptoms carefully.
        2. Provide general health advice based on the symptoms.
        3. If appropriate and safe, briefly introduce common, non-prescription medicine available in the system that may help, only if they are not contraindicated by the patient's information.
        4. Always recommend the user to schedule an appointment with a healthcare professional for accurate diagnosis and treatment.
        5. DO NOT provide dosage instructions or treatment plans.
        6. If the symptoms are severe or urgent (chest pain, difficulty breathing, high fever), strongly recommend the user seek immediate medical attention.
        7. If the user's message does not provide enough detail, kindly ask for more information about their symptoms.
        8. Always include a disclaimer that you are KiviCare AI and cannot replace a real doctor's consultation.

        Please respond directly as KiviCare AI in a warm, concise, and professional manner.
        """.formatted(userData, chatMessageRequest.getUserMessage(), getAllPharmacy());
    }


}
