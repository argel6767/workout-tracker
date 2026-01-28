package com.pxbzi.workout_tracker.gemini;

import com.pxbzi.workout_tracker.gemini.models.ChatResponseDto;
import lombok.Data;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/gemini")
@Data
public class GeminiController {

    private final GeminiService geminiService;

    @PostMapping("/query")
    public ChatResponseDto getChatResponseDto(@RequestBody QueryDto query) {
        return geminiService.getChatResponseDto(query.query());
    }
}
