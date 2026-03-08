package com.banafrance.annuaire.dto.request;

import com.banafrance.annuaire.model.EventStatus;
import com.banafrance.annuaire.model.EventVisibility;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter @Setter
public class EventRequest {

    @NotBlank
    private String title;

    private String description;

    @NotNull
    private Instant startAt;

    private Instant endAt;

    private String locationText;

    private Integer capacity;

    private EventVisibility visibility;

    private EventStatus status;
}
