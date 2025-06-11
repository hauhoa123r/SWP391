package org.project.ai.intent.service;

import org.project.ai.chat.AIService;
import org.project.ai.intent.BasePromptHandler;
import org.project.ai.prompt.service.RequestRefundPrompt;
import org.project.model.request.ChatMessageRequest;

public class RequestRefundHandler extends BasePromptHandler<RequestRefundPrompt> {

    public RequestRefundHandler(AIService aiService, RequestRefundPrompt requestRefundPrompt) {
        super(aiService, requestRefundPrompt);
    }

    @Override
    protected String buildPrompt(ChatMessageRequest chatMessageRequest) {
        return prompt.buildPrompt(chatMessageRequest);
    }

    @Override
    public String contextType() {
        return "request_refund";
    }
}
