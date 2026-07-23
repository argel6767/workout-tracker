package com.pxbzi.workout_tracker.gemini;

import com.google.genai.Client;
import com.google.genai.types.GenerateContentConfig;
import com.google.genai.types.GenerateContentResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GeminiGateway {

    private final Client client;

    public GenerateContentResponse generate(String model, String query, GenerateContentConfig config) {
        return client.models.generateContent(model, query, config);
    }
}
