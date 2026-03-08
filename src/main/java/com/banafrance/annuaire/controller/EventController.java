package com.banafrance.annuaire.controller;

import com.banafrance.annuaire.config.UserPrincipal;
import com.banafrance.annuaire.dto.request.EventRequest;
import com.banafrance.annuaire.dto.request.RsvpRequest;
import com.banafrance.annuaire.dto.response.EventResponse;
import com.banafrance.annuaire.dto.response.PageResponse;
import com.banafrance.annuaire.dto.response.RsvpResponse;
import com.banafrance.annuaire.service.EventService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/events")
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;

    @PostMapping
    public ResponseEntity<EventResponse> createEvent(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody EventRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(eventService.createEvent(principal, request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<EventResponse> updateEvent(
            @PathVariable UUID id,
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody EventRequest request) {
        return ResponseEntity.ok(eventService.updateEvent(id, principal, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEvent(
            @PathVariable UUID id,
            @AuthenticationPrincipal UserPrincipal principal) {
        eventService.deleteEvent(id, principal);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<EventResponse> cancelEvent(
            @PathVariable UUID id,
            @AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(eventService.cancelEvent(id, principal));
    }

    @GetMapping
    public ResponseEntity<PageResponse<EventResponse>> listEvents(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            Authentication authentication) {
        return ResponseEntity.ok(eventService.listEvents(page, size, authentication));
    }

    @GetMapping("/{id}")
    public ResponseEntity<EventResponse> getEvent(@PathVariable UUID id) {
        return ResponseEntity.ok(eventService.getEvent(id));
    }

    @PostMapping("/{id}/rsvp")
    public ResponseEntity<RsvpResponse> rsvp(
            @PathVariable UUID id,
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody RsvpRequest request) {
        return ResponseEntity.ok(eventService.rsvp(id, principal, request));
    }
}
