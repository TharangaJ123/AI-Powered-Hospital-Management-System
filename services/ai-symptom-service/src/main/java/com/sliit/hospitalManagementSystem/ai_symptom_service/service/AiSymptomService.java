package com.sliit.hospitalManagementSystem.ai_symptom_service.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sliit.hospitalManagementSystem.ai_symptom_service.dto.SymptomCheckRequest;
import com.sliit.hospitalManagementSystem.ai_symptom_service.dto.SymptomCheckResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.*;

@Service
@RequiredArgsConstructor
public class AiSymptomService {

    @Value("${gemini.api.key}")
    private String apiKey;

    @Value("${gemini.api.url}")
    private String apiUrl;

    private final WebClient.Builder webClientBuilder;
    private final ObjectMapper objectMapper;

    public SymptomCheckResponse checkSymptoms(SymptomCheckRequest request) {
        if ("YOUR_GEMINI_API_KEY".equals(apiKey) || apiKey == null || apiKey.isEmpty()) {
            return SymptomCheckResponse.builder()
                    .diagnosis("Health analysis system is currently offline.")
                    .recommendations("Please configure a valid GEMINI_API_KEY to enable AI assistance. Contact system administration for support.")
                    .recommendedSpecialties(Collections.singletonList("General Practice"))
                    .urgencyLevel("MEDIUM")
                    .build();
        }
        
        String prompt = buildPrompt(request);
        
        try {
            String maskedKey = (apiKey != null && apiKey.length() > 8) ? apiKey.substring(0, 4) + "..." + apiKey.substring(apiKey.length() - 4) : "INVALID";
            System.out.println("Calling Gemini API: " + apiUrl + " (Key: " + maskedKey + ")");
            Map<String, Object> geminiRequest = new HashMap<>();
            Map<String, Object> content = new HashMap<>();
            content.put("parts", Collections.singletonList(Collections.singletonMap("text", prompt)));
            geminiRequest.put("contents", Collections.singletonList(content));

            String responseBody = webClientBuilder.build()
                    .post()
                    .uri(apiUrl)
                    .header("x-goog-api-key", apiKey)
                    .bodyValue(geminiRequest)
                    .retrieve()
                    .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(), 
                        response -> response.bodyToMono(String.class).map(err -> new RuntimeException("Gemini API Error: " + err)))
                    .bodyToMono(String.class)
                    .block();

            if (responseBody == null) {
                return SymptomCheckResponse.builder()
                        .diagnosis("AI analysis returned an empty response.")
                        .recommendations("Please try again. If the issue persists, contact system administration.")
                        .recommendedSpecialties(Collections.singletonList("General Practice"))
                        .urgencyLevel("MEDIUM")
                        .build();
            }
            return parseGeminiResponse(responseBody);
        } catch (Exception e) {
            System.err.println("AI Service Error: " + e.getMessage());
            return SymptomCheckResponse.builder()
                    .diagnosis("AI analysis service is temporarily unavailable.")
                    .recommendations("The backend was unable to communicate with the AI engine. Please verify the API key and network connection.")
                    .recommendedSpecialties(Collections.singletonList("General Practice"))
                    .urgencyLevel("MEDIUM")
                    .build();
        }
    }

    private String buildPrompt(SymptomCheckRequest request) {
        return "As a medical assistant AI, analyze these symptoms: " + request.getSymptoms() + ". " +
                (request.getAdditionalInfo() != null ? "Additional Info: " + request.getAdditionalInfo() : "") +
                "\nReturn the response in strictly JSON format with exactly these four keys: " +
                "'diagnosis' (a short preliminary suggestion), " +
                "'recommendations' (advice followed by list of steps), " +
                "'recommendedSpecialties' (list of strings for doctor types like Cardiology, Neurology, etc.), " +
                "'urgencyLevel' (LOW, MEDIUM, HIGH, or EMERGENCY). " +
                "Do not include any other text except the JSON.";
    }

    private SymptomCheckResponse parseGeminiResponse(String body) {
        try {
            JsonNode root = objectMapper.readTree(body);
            String rawJson = root.path("candidates").get(0).path("content").path("parts").get(0).path("text").asText();
            
            // Extract JSON from potential Markdown markers
            int startIndex = rawJson.indexOf("{");
            int endIndex = rawJson.lastIndexOf("}");
            if (startIndex != -1 && endIndex != -1) {
                rawJson = rawJson.substring(startIndex, endIndex + 1);
            }
            
            JsonNode responseJson = objectMapper.readTree(rawJson);
            
            List<String> specialties = new ArrayList<>();
            if (responseJson.has("recommendedSpecialties")) {
                JsonNode specsNode = responseJson.get("recommendedSpecialties");
                if (specsNode.isArray()) {
                    specsNode.forEach(node -> specialties.add(node.asText()));
                } else if (specsNode.isTextual()) {
                    specialties.add(specsNode.asText());
                }
            }

            return SymptomCheckResponse.builder()
                    .diagnosis(responseJson.path("diagnosis").asText("Assessment complete"))
                    .recommendations(responseJson.path("recommendations").asText("Please follow standard healthcare protocols."))
                    .recommendedSpecialties(specialties.isEmpty() ? Collections.singletonList("General Practitioner") : specialties)
                    .urgencyLevel(responseJson.path("urgencyLevel").asText("MEDIUM"))
                    .build();
        } catch (Exception e) {
            System.err.println("Gemini Parsing Error: " + e.getMessage() + " | Body: " + body);
            return SymptomCheckResponse.builder()
                    .diagnosis("Partial analysis completed.")
                    .recommendations("The AI assistant had trouble formatting the result. Please try again or consult a doctor directly.")
                    .recommendedSpecialties(Collections.singletonList("General Practice"))
                    .urgencyLevel("HIGH")
                    .build();
        }
    }
}
