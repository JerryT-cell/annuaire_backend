package com.banafrance.annuaire.service;

import com.banafrance.annuaire.config.UserPrincipal;
import com.banafrance.annuaire.dto.request.EventRequest;
import com.banafrance.annuaire.dto.request.RsvpRequest;
import com.banafrance.annuaire.dto.response.EventResponse;
import com.banafrance.annuaire.dto.response.PageResponse;
import com.banafrance.annuaire.dto.response.RsvpResponse;
import com.banafrance.annuaire.exception.AccessDeniedException;
import com.banafrance.annuaire.exception.EventFullException;
import com.banafrance.annuaire.exception.ResourceNotFoundException;
import com.banafrance.annuaire.model.*;
import com.banafrance.annuaire.repository.EventRepository;
import com.banafrance.annuaire.repository.EventRsvpRepository;
import com.banafrance.annuaire.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;
    private final EventRsvpRepository rsvpRepository;
    private final UserRepository userRepository;

    @Transactional
    public EventResponse createEvent(UserPrincipal principal, EventRequest request) {
        User organizer = userRepository.getReferenceById(principal.getId());
        Event event = Event.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .startAt(request.getStartAt())
                .endAt(request.getEndAt())
                .locationText(request.getLocationText())
                .capacity(request.getCapacity())
                .visibility(request.getVisibility() != null ? request.getVisibility() : EventVisibility.PUBLIC)
                .status(request.getStatus() != null ? request.getStatus() : EventStatus.DRAFT)
                .organizer(organizer)
                .build();
        event = eventRepository.save(event);
        return toResponse(event);
    }

    @Transactional
    public EventResponse updateEvent(UUID eventId, UserPrincipal principal, EventRequest request) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found"));
        checkOrganizer(event, principal);

        event.setTitle(request.getTitle());
        event.setDescription(request.getDescription());
        event.setStartAt(request.getStartAt());
        event.setEndAt(request.getEndAt());
        event.setLocationText(request.getLocationText());
        event.setCapacity(request.getCapacity());
        if (request.getVisibility() != null) event.setVisibility(request.getVisibility());
        if (request.getStatus() != null) event.setStatus(request.getStatus());

        event = eventRepository.save(event);
        return toResponse(event);
    }

    @Transactional
    public void deleteEvent(UUID eventId, UserPrincipal principal) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found"));
        checkOrganizer(event, principal);
        eventRepository.delete(event);
    }

    @Transactional
    public EventResponse cancelEvent(UUID eventId, UserPrincipal principal) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found"));
        checkOrganizer(event, principal);
        event.setStatus(EventStatus.CANCELLED);
        event = eventRepository.save(event);
        return toResponse(event);
    }

    public PageResponse<EventResponse> listEvents(int page, int size, Authentication authentication) {
        List<EventVisibility> visibilities = resolveEventVisibilities(authentication);
        List<EventStatus> statuses = List.of(EventStatus.PUBLISHED);

        Page<EventResponse> result = eventRepository.findVisibleEvents(statuses, visibilities, PageRequest.of(page, size))
                .map(this::toResponse);
        return PageResponse.of(result);
    }

    public EventResponse getEvent(UUID eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found"));
        return toResponse(event);
    }

    @Transactional
    public RsvpResponse rsvp(UUID eventId, UserPrincipal principal, RsvpRequest request) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found"));
        User user = userRepository.getReferenceById(principal.getId());

        EventRsvpId rsvpId = new EventRsvpId(eventId, principal.getId());
        EventRsvp rsvp = rsvpRepository.findById(rsvpId).orElse(null);

        if (request.getStatus() == RsvpStatus.GOING && event.getCapacity() != null) {
            long currentGoing = rsvpRepository.countByIdEventIdAndStatus(eventId, RsvpStatus.GOING);
            // If user is already GOING, they don't take a new spot
            boolean alreadyGoing = rsvp != null && rsvp.getStatus() == RsvpStatus.GOING;
            if (!alreadyGoing && currentGoing >= event.getCapacity()) {
                throw new EventFullException("Event is at full capacity");
            }
        }

        if (rsvp == null) {
            rsvp = EventRsvp.builder()
                    .id(rsvpId)
                    .event(event)
                    .user(user)
                    .status(request.getStatus())
                    .build();
        } else {
            rsvp.setStatus(request.getStatus());
        }

        rsvp = rsvpRepository.save(rsvp);

        return RsvpResponse.builder()
                .eventId(eventId)
                .userId(principal.getId())
                .status(rsvp.getStatus())
                .build();
    }

    private void checkOrganizer(Event event, UserPrincipal principal) {
        boolean isAdmin = principal.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        if (!isAdmin && !event.getOrganizer().getId().equals(principal.getId())) {
            throw new AccessDeniedException("Only the organizer or admin can modify this event");
        }
    }

    private List<EventVisibility> resolveEventVisibilities(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()
                || "anonymousUser".equals(authentication.getPrincipal())) {
            return List.of(EventVisibility.PUBLIC);
        }
        return List.of(EventVisibility.PUBLIC, EventVisibility.MEMBERS_ONLY);
    }

    private EventResponse toResponse(Event event) {
        long goingCount = rsvpRepository.countByIdEventIdAndStatus(event.getId(), RsvpStatus.GOING);
        return EventResponse.builder()
                .id(event.getId())
                .title(event.getTitle())
                .description(event.getDescription())
                .startAt(event.getStartAt())
                .endAt(event.getEndAt())
                .locationText(event.getLocationText())
                .capacity(event.getCapacity())
                .goingCount(goingCount)
                .visibility(event.getVisibility())
                .status(event.getStatus())
                .organizerId(event.getOrganizer().getId())
                .createdAt(event.getCreatedAt())
                .build();
    }
}
