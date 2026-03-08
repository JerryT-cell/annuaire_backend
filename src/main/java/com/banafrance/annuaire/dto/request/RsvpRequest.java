package com.banafrance.annuaire.dto.request;

import com.banafrance.annuaire.model.RsvpStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class RsvpRequest {

    @NotNull
    private RsvpStatus status;
}
