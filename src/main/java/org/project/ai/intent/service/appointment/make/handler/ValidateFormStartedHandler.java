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
        Yes, I can help you schedule. Please provide me with the following information:
                
        1. The date and time you would like to schedule an appointment.
                
        2. The hospital you would like to visit
                
        3. The specific department or doctor you would like to see.(optional)
                
        4. Is the person you would like to schedule an appointment with a friend or family member?
                
        Please provide the information so I can best assist you. Thank you!
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
