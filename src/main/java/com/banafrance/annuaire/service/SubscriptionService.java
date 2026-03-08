package com.banafrance.annuaire.service;

import com.banafrance.annuaire.dto.request.SubscriptionRequest;
import com.banafrance.annuaire.dto.response.MessageResponse;
import com.banafrance.annuaire.dto.response.SubscriptionResponse;
import com.banafrance.annuaire.exception.DuplicateResourceException;
import com.banafrance.annuaire.exception.ResourceNotFoundException;
import com.banafrance.annuaire.model.Subscription;
import com.banafrance.annuaire.repository.SubscriptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;

    public SubscriptionResponse subscribe(SubscriptionRequest request) {
        if (subscriptionRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("Email already subscribed");
        }
        Subscription subscription = Subscription.builder()
                .email(request.getEmail())
                .build();
        subscription = subscriptionRepository.save(subscription);
        return toResponse(subscription);
    }

    public MessageResponse unsubscribe(String email) {
        Subscription subscription = subscriptionRepository.findById(email)
                .orElseThrow(() -> new ResourceNotFoundException("Subscription not found"));
        subscriptionRepository.delete(subscription);
        return MessageResponse.builder().message("Unsubscribed successfully").build();
    }

    public List<SubscriptionResponse> listAll() {
        return subscriptionRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    private SubscriptionResponse toResponse(Subscription subscription) {
        return SubscriptionResponse.builder()
                .email(subscription.getEmail())
                .createdAt(subscription.getCreatedAt())
                .build();
    }
}
