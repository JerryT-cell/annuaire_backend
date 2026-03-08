package com.banafrance.annuaire.controller;

import com.banafrance.annuaire.dto.request.SubscriptionRequest;
import com.banafrance.annuaire.dto.response.MessageResponse;
import com.banafrance.annuaire.dto.response.SubscriptionResponse;
import com.banafrance.annuaire.service.SubscriptionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/subscriptions")
@RequiredArgsConstructor
public class SubscriptionController {

    private final SubscriptionService subscriptionService;

    @PostMapping
    public ResponseEntity<SubscriptionResponse> subscribe(@Valid @RequestBody SubscriptionRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(subscriptionService.subscribe(request));
    }

    @DeleteMapping("/{email}")
    public ResponseEntity<MessageResponse> unsubscribe(@PathVariable String email) {
        return ResponseEntity.ok(subscriptionService.unsubscribe(email));
    }
}
