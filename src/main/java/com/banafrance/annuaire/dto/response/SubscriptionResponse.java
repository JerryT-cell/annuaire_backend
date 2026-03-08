package com.banafrance.annuaire.dto.response;

import lombok.*;

import java.time.Instant;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class SubscriptionResponse {
    private String email;
    private Instant createdAt;
}
