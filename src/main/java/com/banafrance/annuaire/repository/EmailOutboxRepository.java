package com.banafrance.annuaire.repository;

import com.banafrance.annuaire.model.EmailOutbox;
import com.banafrance.annuaire.model.OutboxStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface EmailOutboxRepository extends JpaRepository<EmailOutbox, UUID> {

    List<EmailOutbox> findTop50ByStatusOrderByCreatedAtAsc(OutboxStatus status);
}
