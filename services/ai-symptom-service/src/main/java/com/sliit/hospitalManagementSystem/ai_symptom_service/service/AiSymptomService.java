
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
import java.util.Objects;

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
                    .clinicalCondition("System Configuration Issue")
                    .recommendations(Collections.singletonList("Please configure a valid GEMINI_API_KEY to enable AI assistance. Contact system administration for support."))
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
                    .uri(Objects.requireNonNull(apiUrl, "Gemini API URL must be configured"))
                    .header("x-goog-api-key", apiKey)
                    .bodyValue(geminiRequest)
                    .retrieve()
                    .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(), 
                        response -> response.bodyToMono(String.class).map(err -> new RuntimeException("Gemini API Error: " + err)))
                    .bodyToMono(String.class)
                    .block();

            return parseGeminiResponse(responseBody);
        } catch (Exception e) {
            System.err.println("AI Service Error: " + e.getMessage());
            return SymptomCheckResponse.builder()
                    .diagnosis("AI analysis service is temporarily unavailable.")
                    .clinicalCondition("API Connection Failure")
                    .recommendations(Collections.singletonList("The backend was unable to communicate with the AI engine. Please verify the API key and network connection."))
                    .recommendedSpecialties(Collections.singletonList("General Practice"))
                    .urgencyLevel("MEDIUM")
                    .build();
        }
    }

    private String buildPrompt(SymptomCheckRequest request) {
        StringBuilder prompt = new StringBuilder("As a medical assistant AI, analyze the following patient data:\n");
        prompt.append("- Age: ").append(request.getAge()).append("\n");
        prompt.append("- Gender: ").append(request.getGender()).append("\n");
        prompt.append("- Symptoms: ").append(request.getSymptoms()).append("\n");
        if (request.getMedicalHistory() != null && !request.getMedicalHistory().isEmpty()) {
            prompt.append("- Medical History (Current/Hidden diseases): ").append(request.getMedicalHistory()).append("\n");
        }
        
        prompt.append("\nReturn the response in strictly JSON format with exactly these five keys: ")
              .append("'diagnosis' (a short preliminary suggestion using simple, non-medical language that anyone can understand), ")
              .append("'clinicalCondition' (the formal medical/clinical name of the suspected condition), ")
              .append("'recommendations' (a JSON array of short, actionable strings/steps), ")
              .append("'recommendedSpecialties' (a JSON array of strings for doctor types like Cardiology, Neurology, etc.), ")
              .append("'urgencyLevel' (LOW, MEDIUM, HIGH, or EMERGENCY). ")
              .append("Do not include any other text except the JSON.");
        return prompt.toString();
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

            List<String> recommendations = new ArrayList<>();
            if (responseJson.has("recommendations")) {
                JsonNode recsNode = responseJson.get("recommendations");
                if (recsNode.isArray()) {
                    recsNode.forEach(node -> recommendations.add(node.asText()));
                } else if (recsNode.isTextual()) {
                    recommendations.add(recsNode.asText());
                }
            }

            return SymptomCheckResponse.builder()
                    .diagnosis(responseJson.path("diagnosis").asText("Assessment complete"))
                    .clinicalCondition(responseJson.path("clinicalCondition").asText("Not specified"))
                    .recommendations(recommendations.isEmpty() ? Collections.singletonList("Please follow standard healthcare protocols.") : recommendations)
                    .recommendedSpecialties(specialties.isEmpty() ? Collections.singletonList("General Practitioner") : specialties)
                    .urgencyLevel(responseJson.path("urgencyLevel").asText("MEDIUM"))
                    .build();
        } catch (Exception e) {
            System.err.println("Gemini Parsing Error: " + e.getMessage() + " | Body: " + body);
            return SymptomCheckResponse.builder()
                    .diagnosis("Partial analysis completed.")
                    .clinicalCondition("Analysis Error")
                    .recommendations(Collections.singletonList("The AI assistant had trouble formatting the result. Please try again or consult a doctor directly."))
                    .recommendedSpecialties(Collections.singletonList("General Practice"))
                    .urgencyLevel("HIGH")
                    .build();
        }
    }
}
