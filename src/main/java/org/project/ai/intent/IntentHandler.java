package org.project.ai.intent;


import org.project.model.request.ChatMessageRequest;

import java.io.IOException;

public interface IntentHandler {
    String handle(ChatMessageRequest chatMessageRequest, String historyWithUser) throws IOException;
    String contextType();
}