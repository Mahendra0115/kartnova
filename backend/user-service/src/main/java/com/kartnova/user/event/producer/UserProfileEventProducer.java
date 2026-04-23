package com.kartnova.user.event.producer;

import com.kartnova.user.constant.KafkaTopics;
import com.kartnova.user.event.payload.UserAddressAddedEvent;
import com.kartnova.user.event.payload.UserProfileCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.support.SendResult;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserProfileEventProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void publishUserProfileCreated(UserProfileCreatedEvent event) {
        try {
            kafkaTemplate.send(
                    KafkaTopics.USER_PROFILE_CREATED,
                    String.valueOf(event.getAuthUserId()),
                    event
            ).whenComplete((result, exception) -> logPublishResult(
                    "user profile created",
                    event.getAuthUserId(),
                    result,
                    exception
            ));
        } catch (Exception exception) {
            log.error(
                    "Failed to publish user profile created event for authUserId={}. API flow will continue.",
                    event.getAuthUserId(),
                    exception
            );
        }
    }

    public void publishUserAddressAdded(UserAddressAddedEvent event) {
        try {
            kafkaTemplate.send(
                    KafkaTopics.USER_ADDRESS_ADDED,
                    String.valueOf(event.getProfileId()),
                    event
            ).whenComplete((result, exception) -> logPublishResult(
                    "user address added",
                    event.getProfileId(),
                    result,
                    exception
            ));
        } catch (Exception exception) {
            log.error(
                    "Failed to publish user address added event for profileId={}. API flow will continue.",
                    event.getProfileId(),
                    exception
            );
        }
    }

    private void logPublishResult(String eventName, Long key, SendResult<String, Object> result, Throwable exception) {
        if (exception != null) {
            log.error("Failed to publish {} event for key={}. API flow will continue.", eventName, key, exception);
            return;
        }

        log.info(
                "{} event published successfully for key={}, topic={}, partition={}, offset={}",
                eventName,
                key,
                result.getRecordMetadata().topic(),
                result.getRecordMetadata().partition(),
                result.getRecordMetadata().offset()
        );
    }
}
