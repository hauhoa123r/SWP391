package org.project.service.impl;

import com.github.pemistahl.lingua.api.Language;
import com.github.pemistahl.lingua.api.LanguageDetector;
import com.github.pemistahl.lingua.api.LanguageDetectorBuilder;
import org.project.service.LanguageService;
import org.springframework.stereotype.Service;

@Service
public class LanguageServiceImpl implements LanguageService {

    @Override
    public String getLanguageFromChatUser(String userMessage) {
        LanguageDetector detector = LanguageDetectorBuilder.fromAllLanguages().build();
        String input = userMessage;
        Language lang = detector.detectLanguageOf(input);
        return lang.toString();
    }

}
