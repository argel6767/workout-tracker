package com.pxbzi.workout_tracker.gemini;

import com.google.genai.Client;
import com.google.genai.types.Content;
import com.google.genai.types.Part;
import com.google.genai.types.GenerateContentConfig;
import com.google.genai.types.GoogleSearch;
import com.google.genai.types.ThinkingConfig;
import com.google.genai.types.Tool;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GeminiClientConfiguration {

    @Value("${gemini.api.key}")
    private String geminiApiKey;

    @Bean
    Client client() {
        return Client.builder()
                .apiKey(geminiApiKey)
                .build();
    }
    
    @Bean
    GenerateContentConfig generateContentConfig() {
        Content systemInstructions = Content.fromParts(Part.fromText(
                "You are a fitness analysis assistant specialized in comparing individual workout performance against population strength standards. " +
                        "When analyzing user workout data, provide the following:\n" +
                        "1. **Estimated 1RM**: Calculate estimated one-repetition maximum based on the provided reps and weight.\n" +
                        "2. **Population Comparison**: Use web search to find relevant strength standards for the user's demographic (age, gender, bodyweight). " +
                        "Compare their performance across novice, intermediate, advanced, and elite categories.\n" +
                        "3. **Specific Benchmarks**: Reference specific strength standards (e.g., average 1RM for their weight class and gender).\n" +
                        "4. **Assessment**: Clearly state which category (novice, intermediate, advanced, elite) the user falls into.\n" +
                        "5. **Actionable Insights**: Provide concise recommendations for progression.\n" +
                        "Keep your response well-structured and brief with clear sections. Prioritize the population comparison as the main focus of your analysis. " +
                        "Always cite sources when referencing strength standards.\n" +
                        "IMPORTANT: Do not use markdown formatting. Return your entire response as a plain paragraph. " +
                        "Do not use headers, bold text, bullet points, or any markdown syntax."));

        
        Tool googleSearchTool = Tool.builder().googleSearch(GoogleSearch.builder()).build();
        
        return GenerateContentConfig.builder()
        .candidateCount(1)
        .thinkingConfig(ThinkingConfig.builder().thinkingBudget(0))
        .systemInstruction(systemInstructions)
        .tools(googleSearchTool)
        .build();
    }
}
