package com.banafrance.annuaire.repository;

import com.banafrance.annuaire.model.EventRsvp;
import com.banafrance.annuaire.model.EventRsvpId;
import com.banafrance.annuaire.model.RsvpStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface EventRsvpRepository extends JpaRepository<EventRsvp, EventRsvpId> {

    List<EventRsvp> findByIdEventId(UUID eventId);

    long countByIdEventIdAndStatus(UUID eventId, RsvpStatus status);
}
