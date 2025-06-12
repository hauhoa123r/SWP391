package org.project.ai.prompt.patient;

import org.project.ai.converter.patient.DataConverterPatient;
import org.project.ai.prompt.PromptAnswer;
import org.project.ai.prompt.PromptStrategy;
import org.project.model.request.ChatMessageRequest;
import org.project.repository.ProductRepository;
import org.springframework.stereotype.Component;

@Component
public class PatientPrompt implements PromptAnswer {

    private final ProductRepository productRepository;

    private final DataConverterPatient dataConverterPatient;
    public PatientPrompt(ProductRepository productRepository, DataConverterPatient dataConverterPatient) {
        this.productRepository = productRepository;
        this.dataConverterPatient = dataConverterPatient;
    }

    @Override
    public String buildPrompt(ChatMessageRequest chatMessageRequest, String historyWithUser) {
        String userData = "";
        if(chatMessageRequest.getUserId() != null){
            userData = dataConverterPatient.toConverterDataUser(chatMessageRequest.getUserId());
        }

        return String.format("""
                You are KiviCare AI, a virtual assistant of the KiviCare hospital system.
                
                The user asked: "%s"
                
                The following is the list of patients registered under this user's account: %s.
                
                Please check if the person the user is asking about matches any patient in this list.
                
                If the patient is found, kindly provide the patient's basic information.
                
                If the patient is not found in the list, politely respond: "Xin lỗi, tôi không tìm thấy bệnh nhân này trong danh sách đã đăng ký của bạn."
                
                Always respond as KiviCare AI.
            """, chatMessageRequest.getUserMessage(), userData);
    }
}