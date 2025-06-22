package org.project.ai.prompt.patient.create;

import org.project.ai.converter.patient.DataConverterPatient;
import org.project.ai.converter.patient.FilterDataCreatePatient;
import org.project.ai.prompt.PromptAnswer;
import org.project.model.dto.MedicalProfileDTO;
import org.project.model.dto.PatientDTO;
import org.project.model.request.ChatMessageRequest;
import org.project.service.impl.MedicalProfileServiceImpl;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CreatePatientPrompt implements PromptAnswer {
    private final FilterDataCreatePatient filterDataCreatePatient;
    private final DataConverterPatient dataConverterPatient;
    private final MedicalProfileServiceImpl medicalProfileService;

    public CreatePatientPrompt(FilterDataCreatePatient filterDataCreatePatient,
                               DataConverterPatient dataConverterPatient,
                               MedicalProfileServiceImpl medicalProfileService) {
        this.filterDataCreatePatient = filterDataCreatePatient;
        this.dataConverterPatient = dataConverterPatient;
        this.medicalProfileService = medicalProfileService;
    }

    @Override
    public String buildPrompt(ChatMessageRequest chatMessageRequest, String historyWithUser) throws IOException{
        PatientDTO patientDTO = filterDataCreatePatient.extractData(chatMessageRequest, historyWithUser);
        String validatePatient = dataConverterPatient.toValidationDataCreatePatient(patientDTO);
        if(!patientDTO.isFormStarted()){
            return toGetInformationNewPatient(patientDTO);
        }
        if (!validatePatient.isEmpty()) {
            return """
                    Đề nghị người dùng thêm những field còn thiếu
                    %s
                    trả lời theo ngôn ngữ: %s
                    """.formatted(validatePatient,chatMessageRequest.getLanguage());
        }
        medicalProfileService.addPatientAndMedicalProfile(new MedicalProfileDTO(), patientDTO);
        return "Tạo bệnh nhân thành công";
    }

    private String toGetInformationNewPatient(PatientDTO patientDTO){
            return """
        Vâng, tôi có thể giúp bạn với việc đặt lịch hẹn. Bạn vui lòng cung cấp cho tôi thông tin sau:

        1. Họ và tên bệnh nhân.
        2. Ngày sinh.
        3. Mối quan hệ trong gia đình.
        4. Giới tính.

        Vui lòng cung cấp thông tin để tôi có thể hỗ trợ bạn tốt nhất. Cảm ơn!
                    """;
    }
}
