package by.kireenko.notificationservice.listeners;

import by.kireenko.notificationservice.dto.UserEventDto;
import by.kireenko.notificationservice.services.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserEventListener {

    private final EmailService emailService;

    @RabbitListener(queues = "${application.rabbitmq.queue.name}")
    public void handleUserEvent(UserEventDto event) {
        log.info("Received user event from RabbitMQ: {}", event);

        String[] recipients = event.getRecipients().toArray(new String[0]);

        if (recipients.length == 0) {
            log.warn("No recipients in the event message. Skipping notification.");
            return;
        }

        String subject;
        String text;

        switch (event.getEventType()) {
            case "CREATED":
                subject = String.format("Создан пользователь %s", event.getUsername());
                text = String.format("Создан пользователь с именем - %s и почтой - %s",
                        event.getUsername(), event.getEmail());
                break;
            case "UPDATED":
                subject = String.format("Изменен пользователь %s", event.getUsername());
                text = String.format("Изменен пользователь с именем - %s и почтой - %s",
                        event.getUsername(), event.getEmail());
                break;
            case "DELETED":
                subject = String.format("Удален пользователь %s", event.getUsername());
                text = String.format("Удален пользователь с именем - %s и почтой - %s",
                        event.getUsername(), event.getEmail());
                break;
            default:
                log.warn("Received unknown event type: {}", event.getEventType());
                return;
        }

        if (event.getPassword() != null && !event.getPassword().isEmpty()) {
            text += String.format(", паролем - %s", event.getPassword());
        }
        text += ".";

        emailService.sendNotification(recipients, subject, text);
    }
}
