package com.pxbzi.workout_tracker.gemini;

import com.pxbzi.workout_tracker.gemini.models.ChatResponseDto;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class GeminiControllerTest {

    @Test
    void delegatesQueryToGeminiService() {
        GeminiService service = mock(GeminiService.class);
        ChatResponseDto expected = new ChatResponseDto("response", LocalDateTime.now());
        when(service.getChatResponseDto("How am I progressing?")).thenReturn(expected);

        ChatResponseDto result = new GeminiController(service)
                .getChatResponseDto(new QueryDto("How am I progressing?"));

        assertThat(result).isSameAs(expected);
        verify(service).getChatResponseDto("How am I progressing?");
    }
}
