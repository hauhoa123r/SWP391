package org.project.ai.prompt.patient.create;

import org.project.ai.converter.patient.DataConverterPatient;
import org.project.ai.converter.patient.FilterDataCreatePatient;
import org.project.ai.prompt.PromptAnswer;
import org.project.model.dto.PatientDTO;
import org.project.model.request.ChatMessageRequest;

import java.io.IOException;

public class CreatePatientPrompt implements PromptAnswer {
    private final FilterDataCreatePatient filterDataCreatePatient;
    private final DataConverterPatient dataConverterPatient;
    public CreatePatientPrompt(FilterDataCreatePatient filterDataCreatePatient,
                               DataConverterPatient dataConverterPatient) {
        this.filterDataCreatePatient = filterDataCreatePatient;
        this.dataConverterPatient = dataConverterPatient;
    }

    @Override
    public String buildPrompt(ChatMessageRequest chatMessageRequest, String historyWithUser) throws IOException, IllegalAccessException {
        PatientDTO patientDTO = filterDataCreatePatient.extractData(chatMessageRequest, historyWithUser);
        String validatePatient = dataConverterPatient.toValidationDataCreatePatient(patientDTO);
        if (validatePatient != null) {
            return """
                    Đề nghị người dùng thêm những field còn thiếu
                    trả lời theo ngôn ngữ: %s
                    """.formatted(chatMessageRequest.getLanguage());
        }

        return "Tạo bệnh nhân thành công";
    }
}
