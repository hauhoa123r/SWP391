package org.project.ai.intent;

import org.project.model.request.ChatMessageRequest;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class IntentHandlerRegistry {

    private final Map<String, IntentHandler> handlerMap;
    private static final Set<String> intentType = Set.of(
            "make_appointment", "cancel_appointment",
            "reschedule_appointment", "order_medicine",
            "cancel_medicine", "cancel_medicine_order",
            "request_refund", "create_patient");
    public IntentHandlerRegistry(List<IntentHandler> handlers) {
        this.handlerMap = handlers.stream()
                .collect(Collectors.toMap(IntentHandler::contextType, h -> h));
    }

    public String handle(ChatMessageRequest chatMessageRequest, String historyWithUser) throws IOException {
        IntentHandler handler = handlerMap.get(chatMessageRequest.getPrompt());
        if(intentType.contains(chatMessageRequest.getPrompt())) chatMessageRequest.setIntentType(true);
        else chatMessageRequest.setIntentType(false);
        return handler.handle(chatMessageRequest, historyWithUser);
    }
}