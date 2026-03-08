package com.banafrance.annuaire.dto.response;

import com.banafrance.annuaire.model.RsvpStatus;
import lombok.*;

import java.util.UUID;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class RsvpResponse {
    private UUID eventId;
    private UUID userId;
    private RsvpStatus status;
}
