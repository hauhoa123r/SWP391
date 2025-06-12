package org.project.ai.context;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ChatSession {


    public String getChatSessionWithUser(List<ChatMessage> chatMessages){
        StringBuilder historyChatWithUser = new StringBuilder();
        if(chatMessages != null && !chatMessages.isEmpty()) {
            StringBuilder historyBuilder = new StringBuilder();
            for (ChatMessage chatMessage : chatMessages) {
                historyBuilder.append(chatMessage.getRole())
                        .append(": ")
                        .append(chatMessage.getContent())
                        .append("\n");
            }
            historyChatWithUser.append("Below is the conversation history with the user:\n" + historyBuilder.toString());
        }
        return historyChatWithUser.toString();
        }

}
