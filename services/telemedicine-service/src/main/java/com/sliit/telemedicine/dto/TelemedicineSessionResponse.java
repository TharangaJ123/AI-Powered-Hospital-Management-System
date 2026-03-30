package com.sliit.telemedicine.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TelemedicineSessionResponse {
    private String roomName;
    private String jitsiUrl;
}
