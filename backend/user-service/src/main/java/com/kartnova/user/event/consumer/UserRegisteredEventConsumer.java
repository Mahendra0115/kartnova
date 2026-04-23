package com.kartnova.user.event.consumer;

import com.kartnova.user.dto.request.CreateUserProfileRequest;
import com.kartnova.user.event.payload.UserRegisteredEvent;
import com.kartnova.user.exception.UserProfileAlreadyExistsException;
import com.kartnova.user.service.UserProfileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserRegisteredEventConsumer {

    private final UserProfileService userProfileService;

    @KafkaListener(
            topics = "${app.kafka.topics.user-registered}",
            groupId = "${spring.kafka.consumer.group-id}"
    )
    public void consumeUserRegisteredEvent(UserRegisteredEvent event) {
        try {
            CreateUserProfileRequest request = new CreateUserProfileRequest();
            request.setAuthUserId(event.getAuthUserId());
            request.setFirstName(event.getFirstName());
            request.setLastName(event.getLastName());
            request.setEmail(event.getEmail());
            request.setPhoneNumber(event.getPhoneNumber());

            userProfileService.createUserProfile(request);
            log.info("User profile created successfully from Kafka event for authUserId={}", event.getAuthUserId());
        } catch (UserProfileAlreadyExistsException exception) {
            log.warn("User profile already exists for authUserId={}, skipping event", event.getAuthUserId());
        } catch (Exception exception) {
            log.error("Failed to consume user registered event for authUserId={}", event.getAuthUserId(), exception);
        }
    }
}