package com.banafrance.annuaire.controller;

import com.banafrance.annuaire.config.UserPrincipal;
import com.banafrance.annuaire.dto.request.ProfileUpdateRequest;
import com.banafrance.annuaire.dto.response.ProfileResponse;
import com.banafrance.annuaire.service.ProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/profiles")
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService profileService;

    @GetMapping("/{id}")
    public ResponseEntity<ProfileResponse> getProfile(@PathVariable UUID id) {
        return ResponseEntity.ok(profileService.getProfile(id));
    }

    @PutMapping("/me")
    public ResponseEntity<ProfileResponse> updateMyProfile(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestBody ProfileUpdateRequest request) {
        return ResponseEntity.ok(profileService.updateMyProfile(principal, request));
    }
}
