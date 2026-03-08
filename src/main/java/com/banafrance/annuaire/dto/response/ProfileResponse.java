package com.banafrance.annuaire.dto.response;

import com.banafrance.annuaire.model.ProfileVisibility;
import lombok.*;

import java.util.UUID;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class ProfileResponse {
    private UUID id;
    private UUID userId;
    private String name;
    private String surname;
    private String occupation;
    private String city;
    private String country;
    private String banaQuarter;
    private String contactEmail;
    private ProfileVisibility visibility;
}
