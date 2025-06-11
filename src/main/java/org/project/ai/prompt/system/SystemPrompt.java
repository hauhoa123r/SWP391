package org.project.ai.prompt.system;

import org.project.ai.converter.DataSystemConverter;
import org.project.ai.intent.system.AskSystemHandler;
import org.project.ai.prompt.PromptStrategy;
import org.springframework.stereotype.Component;

@Component

public class SystemPrompt implements PromptStrategy {

    private final DataSystemConverter dataSystemConverter;

    public SystemPrompt(DataSystemConverter dataSystemConverter) {
        this.dataSystemConverter = dataSystemConverter;
    }

    @Override
    public String buildPrompt(String userMessage) {
        return """
        You are a virtual assistant representing the Kivicare hospital system.

        User's question: "%s"

        Information about the Kivicare hospital system:
        %s

        Respond in a professional, concise, and informative manner. List 3 hospitals and inform the user how many hospitals remain.
        Ask the user for their address in order to recommend the nearest hospital for convenient travel.
        """.formatted(userMessage, dataSystemConverter.toConverterAllHospitals());
    }
}
