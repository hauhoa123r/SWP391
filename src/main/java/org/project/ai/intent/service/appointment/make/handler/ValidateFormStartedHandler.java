package org.project.ai.intent.service.appointment.make.handler;

import org.project.ai.chat.AIService;
import org.project.model.dai.AppointmentDAI;
import org.springframework.stereotype.Component;

@Component
public class ValidateFormStartedHandler extends BaseValidationHandler{

    @Override
    protected boolean isValid(AppointmentDAI data) {
        if(!data.getIsFormStarted()) return false;
        return true;
    }

    @Override
    protected String errorMessage(AppointmentDAI data) {
        String rawMessage = """
        Vâng, tôi có thể giúp bạn với việc đặt lịch hẹn. Bạn vui lòng cung cấp cho tôi thông tin sau:

        1. Ngày và giờ bạn muốn đặt lịch hẹn.
        2. Bệnh viện bạn muốn đến
        3. Khoa hoặc bác sĩ cụ thể mà bạn muốn gặp.
        4. Người cần đặt lịch hẹn là bạn hay người thân của bạn?

        Vui lòng cung cấp thông tin để tôi có thể hỗ trợ bạn tốt nhất. Cảm ơn!
        """;

        return """
        Translate the following message into %s:

        ---
        %s
        ---
        Only return the translated version. Do not include any explanation.
        """.formatted(data.getLanguage(), rawMessage);
    }
}
