package com.banafrance.annuaire.repository;

import com.banafrance.annuaire.model.Event;
import com.banafrance.annuaire.model.EventStatus;
import com.banafrance.annuaire.model.EventVisibility;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface EventRepository extends JpaRepository<Event, UUID> {

    @Query("""
        SELECT e FROM Event e
        WHERE e.status IN :statuses
        AND e.visibility IN :visibilities
        ORDER BY e.startAt ASC
    """)
    Page<Event> findVisibleEvents(
            @Param("statuses") List<EventStatus> statuses,
            @Param("visibilities") List<EventVisibility> visibilities,
            Pageable pageable
    );
}
