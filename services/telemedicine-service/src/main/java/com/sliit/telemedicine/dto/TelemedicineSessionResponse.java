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
    // Unique name of the virtual meeting room
    private String roomName;
    // Direct link to the Jitsi meeting for the session
    private String jitsiUrl;
}
