package org.project.api;

import org.project.ai.intent.ChatRouterService;
import org.project.model.request.ChatMessageRequest;
import org.project.service.LanguageService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/chatbox")
public class ChatBoxAPI {

    private final ChatRouterService chatRouterService;
    private final LanguageService languageService;

    public ChatBoxAPI(ChatRouterService chatRouterService, LanguageService languageService) {
        this.chatRouterService = chatRouterService;
        this.languageService = languageService;
    }

    @PostMapping
    public ResponseEntity<String> handleMessageUser(@Valid @RequestBody ChatMessageRequest chatMessageRequest) {
        chatMessageRequest.setLanguage(languageService.getLanguageFromChatUser(chatMessageRequest.getUserMessage()));
        String reply = chatRouterService.convertToUserMessage(chatMessageRequest);
        return ResponseEntity.ok(reply);
    }
}
