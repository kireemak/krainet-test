package by.kireenko.authservice.utils;

import by.kireenko.authservice.dto.UserEventDto;
import by.kireenko.authservice.model.Role;
import by.kireenko.authservice.model.User;
import by.kireenko.authservice.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserEventProducer {
    private final RabbitTemplate rabbitTemplate;
    private final UserRepository userRepository;

    @Value("${application.rabbitmq.exchange.name}")
    private String exchangeName;
    @Value("${application.rabbitmq.routing.key}")
    private String routingKey;

    private void sendUserEvent(String eventType, String username, String email, String rawPassword) {
        List<String> adminEmails = userRepository.findAllByRole(Role.ADMIN)
                .stream()
                .map(User::getEmail)
                .collect(Collectors.toList());

        if (adminEmails.isEmpty()) {
            log.warn("No admin users found in DB. Notification will not be sent.");
            return;
        }

        UserEventDto userEventDto = new UserEventDto(eventType, username, email, rawPassword, adminEmails);

        log.info("Sending user event '{}' for user '{}' to admins: {}", eventType, username, adminEmails);
        rabbitTemplate.convertAndSend(exchangeName, routingKey, userEventDto);
    }

    public void sendUserCreatedEvent(User user, String rawPassword) {
        sendUserEvent("CREATED", user.getUsername(), user.getEmail(), rawPassword);
    }

    public void sendUserUpdatedEvent(User user, String rawPassword) {
        sendUserEvent("UPDATED", user.getUsername(), user.getEmail(), rawPassword);
    }

    public void sendUserDeletedEvent(String username, String email) {
        sendUserEvent("DELETED", username, email, null);
    }
}
