package com.banafrance.annuaire.service;

import com.banafrance.annuaire.config.UserPrincipal;
import com.banafrance.annuaire.dto.request.ProfileUpdateRequest;
import com.banafrance.annuaire.dto.response.PageResponse;
import com.banafrance.annuaire.dto.response.ProfileResponse;
import com.banafrance.annuaire.exception.ResourceNotFoundException;
import com.banafrance.annuaire.model.Profile;
import com.banafrance.annuaire.model.ProfileVisibility;
import com.banafrance.annuaire.repository.ProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProfileService {

    private final ProfileRepository profileRepository;

    public PageResponse<ProfileResponse> getDirectory(int page, int size, String city, String country,
                                                       String name, Authentication authentication) {
        List<ProfileVisibility> visibilities = resolveVisibilities(authentication);

        Page<ProfileResponse> result = profileRepository.findFiltered(
                visibilities,
                city,
                country,
                name,
                PageRequest.of(page, size, Sort.by("surname").ascending())
        ).map(this::toResponse);

        return PageResponse.of(result);
    }

    public ProfileResponse getProfile(UUID profileId) {
        Profile profile = profileRepository.findById(profileId)
                .orElseThrow(() -> new ResourceNotFoundException("Profile not found"));
        return toResponse(profile);
    }

    @Transactional
    public ProfileResponse updateMyProfile(UserPrincipal principal, ProfileUpdateRequest request) {
        Profile profile = profileRepository.findByUserId(principal.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Profile not found"));

        if (request.getName() != null) profile.setName(request.getName());
        if (request.getSurname() != null) profile.setSurname(request.getSurname());
        if (request.getOccupation() != null) profile.setOccupation(request.getOccupation());
        if (request.getCity() != null) profile.setCity(request.getCity());
        if (request.getCountry() != null) profile.setCountry(request.getCountry());
        if (request.getBanaQuarter() != null) profile.setBanaQuarter(request.getBanaQuarter());
        if (request.getContactEmail() != null) profile.setContactEmail(request.getContactEmail());
        if (request.getVisibility() != null) profile.setVisibility(request.getVisibility());

        profile = profileRepository.save(profile);
        return toResponse(profile);
    }

    private List<ProfileVisibility> resolveVisibilities(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()
                || "anonymousUser".equals(authentication.getPrincipal())) {
            return List.of(ProfileVisibility.PUBLIC);
        }

        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        if (isAdmin) {
            return List.of(ProfileVisibility.PUBLIC, ProfileVisibility.MEMBERS_ONLY);
        }

        return List.of(ProfileVisibility.PUBLIC, ProfileVisibility.MEMBERS_ONLY);
    }

    private ProfileResponse toResponse(Profile profile) {
        return ProfileResponse.builder()
                .id(profile.getId())
                .userId(profile.getUser().getId())
                .name(profile.getName())
                .surname(profile.getSurname())
                .occupation(profile.getOccupation())
                .city(profile.getCity())
                .country(profile.getCountry())
                .banaQuarter(profile.getBanaQuarter())
                .contactEmail(profile.getContactEmail())
                .visibility(profile.getVisibility())
                .build();
    }
}
