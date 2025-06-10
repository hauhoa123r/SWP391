package org.project.ai.prompt.system;

import org.project.ai.converter.DataPatientConverter;
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
public class MedicinePrompt implements PromptAnswer {
    private final ProductRepository productRepository;
    private final DataPatientConverter dataPatientConverter;
    public MedicinePrompt(ProductRepository productRepository, DataPatientConverter dataPatientConverter) {
        this.productRepository = productRepository;
        this.dataPatientConverter = dataPatientConverter;
    }

    private String getAllPharmacy(){
        List<ProductEntity> results = productRepository.findAllByProductTypeAndProductStatus(ProductType.MEDICINE, ProductStatus.ACTIVE);
        return results
                .stream()
                .map(i -> "- " + i.getName())
                .collect(Collectors.joining("\n"));

    }

    @Override
    public String buildPrompt(ChatMessageRequest chatMessageRequest) {
        String userData = "";
        if(chatMessageRequest.getUserId() != null){
             userData = dataPatientConverter.toConverterDataUser(chatMessageRequest.getUserId());
        }
        return """
        You are a medication advisor in the hospital system.
        Respond in: %s

        List of available medications in the system:
        %s

        The user is asking about the medication: %s

        %s

        Please suggest a suitable medication (if any) or provide professional advice.
        If no suitable medication exists, explain the potential health risks of using the drug
        and advise the user to consult a doctor. Greet the user by name and clarify that you are an AI and cannot replace a real doctor.

        Keep the response concise, medically accurate, and include rest recommendations if necessary.
        Do not provide incorrect or misleading information.
        """.formatted(chatMessageRequest.getLanguage(), getAllPharmacy(), chatMessageRequest.getUserMessage(),
                userData);

    }
}
