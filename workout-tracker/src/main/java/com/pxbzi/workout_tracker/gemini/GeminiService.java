package com.pxbzi.workout_tracker.gemini;

import com.google.genai.Client;
import com.google.genai.types.GenerateContentResponse;
import com.google.genai.types.GenerateContentConfig;
import com.pxbzi.workout_tracker.gemini.models.ChatResponseDto;
import lombok.Data;
import lombok.extern.java.Log;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@Data
@Log
public class GeminiService {

    private final Client client;
    private final GenerateContentConfig generateContentConfig;
    private final String GEMINI_MODEL = "gemini-2.5-flash";

    public ChatResponseDto getChatResponseDto(String query) {
        GenerateContentResponse response = client.models.generateContent(GEMINI_MODEL, query, generateContentConfig);
        String responseCleaned = response.text().replaceAll("\n\n", " ");
        return new ChatResponseDto(responseCleaned, LocalDateTime.now());
    }
    
}
