package com.sliit.appointment_service.client;

import com.sliit.appointment_service.dto.DoctorDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DoctorServiceClient {

    private final WebClient.Builder webClientBuilder;

    public List<DoctorDto> getDoctorsBySpecialty(String specialty) {
        return webClientBuilder.build()
                .get()
                .uri("http://localhost:8081/api/doctors/search",
                        uriBuilder -> uriBuilder.queryParam("specialty", specialty).build())
                .retrieve()
                .bodyToFlux(DoctorDto.class)
                .collectList()
                .block();
    }
}
