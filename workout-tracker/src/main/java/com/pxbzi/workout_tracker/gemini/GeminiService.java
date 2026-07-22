package com.pxbzi.workout_tracker.gemini;

import com.google.genai.Client;
import com.google.genai.types.GenerateContentResponse;
import com.google.genai.types.GenerateContentConfig;
import com.google.genai.types.Content;
import com.google.genai.types.Part;
import com.google.genai.types.ThinkingConfig;
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
        return toChatResponseDto(response);
    }

    public ChatResponseDto getConciseChatResponseDto(String query) {
        Content systemInstruction = Content.fromParts(Part.fromText(
                "You are a concise fitness progress assistant. Respond with no more than five short plain-text sentences. "
                        + "State the main trend, mention only the most important supporting details, and finish with one "
                        + "actionable recommendation. Do not provide headings, lists, population comparisons, exhaustive "
                        + "individual exercise breakdowns, or extra context."
        ));
        GenerateContentConfig conciseConfig = GenerateContentConfig.builder()
                .candidateCount(1)
                .maxOutputTokens(240)
                .thinkingConfig(ThinkingConfig.builder().thinkingBudget(0))
                .systemInstruction(systemInstruction)
                .build();
        GenerateContentResponse response = client.models.generateContent(GEMINI_MODEL, query, conciseConfig);
        return toChatResponseDto(response);
    }

    private ChatResponseDto toChatResponseDto(GenerateContentResponse response) {
        String responseCleaned = response.text().replaceAll("\n+", " ").trim();
        return new ChatResponseDto(responseCleaned, LocalDateTime.now());
    }
    
}
