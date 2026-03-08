package com.banafrance.annuaire.service;

import com.banafrance.annuaire.model.EmailOutbox;
import com.banafrance.annuaire.model.OutboxStatus;
import com.banafrance.annuaire.model.Subscription;
import com.banafrance.annuaire.repository.EmailOutboxRepository;
import com.banafrance.annuaire.repository.SubscriptionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailOutboxService {

    private final EmailOutboxRepository outboxRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final JavaMailSender mailSender;

    public void queueEmail(String toEmail, String templateKey, String payload) {
        EmailOutbox outbox = EmailOutbox.builder()
                .toEmail(toEmail)
                .templateKey(templateKey)
                .payload(payload)
                .build();
        outboxRepository.save(outbox);
    }

    @Scheduled(fixedDelay = 30000)
    @Transactional
    public void processOutbox() {
        List<EmailOutbox> pending = outboxRepository.findTop50ByStatusOrderByCreatedAtAsc(OutboxStatus.PENDING);
        for (EmailOutbox entry : pending) {
            try {
                SimpleMailMessage message = new SimpleMailMessage();
                message.setTo(entry.getToEmail());
                message.setSubject(entry.getTemplateKey());
                message.setText(entry.getPayload() != null ? entry.getPayload() : "");
                message.setFrom("noreply@banafrance.com");
                mailSender.send(message);

                entry.setStatus(OutboxStatus.SENT);
            } catch (Exception e) {
                log.error("Failed to send email to {}: {}", entry.getToEmail(), e.getMessage());
                entry.setStatus(OutboxStatus.FAILED);
            }
            outboxRepository.save(entry);
        }
    }

    @Transactional
    public void sendAnnouncement(String subject, String body) {
        List<Subscription> subscribers = subscriptionRepository.findAll();
        for (Subscription sub : subscribers) {
            String payload = "{\"subject\":\"" + subject + "\",\"body\":\"" + body + "\"}";
            queueEmail(sub.getEmail(), subject, payload);
        }
    }
}
