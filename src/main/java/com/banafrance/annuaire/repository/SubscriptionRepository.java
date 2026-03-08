package com.banafrance.annuaire.repository;

import com.banafrance.annuaire.model.Subscription;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubscriptionRepository extends JpaRepository<Subscription, String> {

    boolean existsByEmail(String email);
}
