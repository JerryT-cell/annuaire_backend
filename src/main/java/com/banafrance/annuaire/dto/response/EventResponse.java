package com.banafrance.annuaire.dto.response;

import com.banafrance.annuaire.model.EventStatus;
import com.banafrance.annuaire.model.EventVisibility;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class EventResponse {
    private UUID id;
    private String title;
    private String description;
    private Instant startAt;
    private Instant endAt;
    private String locationText;
    private Integer capacity;
    private long goingCount;
    private EventVisibility visibility;
    private EventStatus status;
    private UUID organizerId;
    private Instant createdAt;
}
