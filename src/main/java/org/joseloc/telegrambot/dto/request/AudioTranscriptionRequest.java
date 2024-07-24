package org.joseloc.telegrambot.dto.request;

import lombok.Data;
import org.springframework.core.io.ByteArrayResource;

@Data
public class AudioTranscriptionRequest {
    private ByteArrayResource file;
    private String model = "whisper-1";
    private String language;
    private String prompt;
    private String responseFormat = "json";
    private Double temperature = 0.0;
}