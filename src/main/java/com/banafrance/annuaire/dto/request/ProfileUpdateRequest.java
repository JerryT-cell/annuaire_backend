package com.banafrance.annuaire.dto.request;

import com.banafrance.annuaire.model.ProfileVisibility;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ProfileUpdateRequest {
    private String name;
    private String surname;
    private String occupation;
    private String city;
    private String country;
    private String banaQuarter;
    private String contactEmail;
    private ProfileVisibility visibility;
}
