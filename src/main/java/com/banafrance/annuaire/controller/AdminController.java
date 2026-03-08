package com.banafrance.annuaire.controller;

import com.banafrance.annuaire.dto.request.AnnouncementRequest;
import com.banafrance.annuaire.dto.response.MessageResponse;
import com.banafrance.annuaire.dto.response.SubscriptionResponse;
import com.banafrance.annuaire.service.EmailOutboxService;
import com.banafrance.annuaire.service.SubscriptionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final SubscriptionService subscriptionService;
    private final EmailOutboxService emailOutboxService;

    @GetMapping("/subscriptions")
    public ResponseEntity<List<SubscriptionResponse>> listSubscriptions() {
        return ResponseEntity.ok(subscriptionService.listAll());
    }

    @PostMapping("/announcements")
    public ResponseEntity<MessageResponse> sendAnnouncement(@Valid @RequestBody AnnouncementRequest request) {
        emailOutboxService.sendAnnouncement(request.getSubject(), request.getBody());
        return ResponseEntity.ok(MessageResponse.builder().message("Announcement queued for delivery").build());
    }
}
