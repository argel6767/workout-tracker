package com.pxbzi.workout_tracker.gemini;

import com.google.genai.types.GenerateContentConfig;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GeminiServiceFailureTest {

    @Test
    void propagatesProviderFailuresWithoutCallingTheRealService() {
        GeminiGateway gateway = mock(GeminiGateway.class);
        GenerateContentConfig config = mock(GenerateContentConfig.class);
        RuntimeException providerFailure = new RuntimeException("provider unavailable");
        when(gateway.generate("gemini-2.5-flash", "analyze", config))
                .thenThrow(providerFailure);

        GeminiService service = new GeminiService(gateway, config);

        assertThatThrownBy(() -> service.getChatResponseDto("analyze"))
                .isSameAs(providerFailure);
    }
}
