package com.banafrance.annuaire.controller;

import com.banafrance.annuaire.dto.response.PageResponse;
import com.banafrance.annuaire.dto.response.ProfileResponse;
import com.banafrance.annuaire.service.ProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/directory")
@RequiredArgsConstructor
public class DirectoryController {

    private final ProfileService profileService;


    @GetMapping
    public ResponseEntity<PageResponse<ProfileResponse>> getDirectory(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String country,
            @RequestParam(required = false) String occupation,
            @RequestParam(required = false) String name,
            Authentication authentication) {
        return ResponseEntity.ok(profileService.getDirectory(page, size, city, country, occupation, name, authentication));
    }
}
