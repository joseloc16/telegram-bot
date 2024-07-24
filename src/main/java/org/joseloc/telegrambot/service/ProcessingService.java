package org.joseloc.telegrambot.service;

import lombok.RequiredArgsConstructor;
import org.joseloc.telegrambot.dto.response.AudioTranscriptionResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class ProcessingService {

    private final RestTemplate restTemplate;

    @Value("${openai.key}")
    private String openAiApiKey;

    @Value("${server-url.image-processing}")
    private String imageProcessingServiceUrl;

    @Value("${server-url.audio-transcription}")
    private String openAiAudioTranscriptionUrl;

    public String processImage(MultipartFile imageFile) throws IOException {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", new org.springframework.core.io.ByteArrayResource(imageFile.getBytes()) {
            @Override
            public String getFilename() {
                return imageFile.getOriginalFilename();
            }
        });

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        ResponseEntity<String> response = restTemplate.exchange(
                imageProcessingServiceUrl ,
                HttpMethod.POST,
                requestEntity,
                String.class
        );

        return response.getBody();
    }

    public String processAudio(MultipartFile audioFile) throws IOException {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        headers.setBearerAuth(openAiApiKey);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", new org.springframework.core.io.ByteArrayResource(audioFile.getBytes()) {
            @Override
            public String getFilename() {
                return audioFile.getOriginalFilename();
            }
        });
        body.add("model", "whisper-1");

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        ResponseEntity<AudioTranscriptionResponse> response = restTemplate.exchange(
                openAiAudioTranscriptionUrl ,
                HttpMethod.POST,
                requestEntity,
                AudioTranscriptionResponse.class
        );

        return response.getBody().getText();
    }
}