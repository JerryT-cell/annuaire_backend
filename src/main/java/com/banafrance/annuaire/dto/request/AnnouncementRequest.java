package com.banafrance.annuaire.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class AnnouncementRequest {

    @NotBlank
    private String subject;

    @NotBlank
    private String body;
}
