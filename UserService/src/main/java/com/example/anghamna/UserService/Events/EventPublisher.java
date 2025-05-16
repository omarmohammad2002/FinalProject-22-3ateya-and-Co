package com.example.anghamna.UserService.Events;

import com.example.anghamna.UserService.rabbitmq.RabbitMQConfig;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

@Service
public class EventPublisher {

    private final RabbitTemplate rabbitTemplate;

    @Autowired
    public EventPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    // Publish follow event
//    public void publishFollowEvent(String followerId, String followedId) {
//        rabbitTemplate.convertAndSend(RabbitMQConfig.USER_EVENTS_EXCHANGE,
//                RabbitMQConfig.USER_FOLLOWED_ROUTING_KEY,
//                Map.of("followerId", followerId, "followedId", followedId));
//    }

    // Publish unfollow event
//    public void publishUnfollowEvent(String followerId, String followedId) {
//        rabbitTemplate.convertAndSend(RabbitMQConfig.USER_EVENTS_EXCHANGE,
//                RabbitMQConfig.USER_UNFOLLOWED_ROUTING_KEY,
//                Map.of("followerId", followerId, "unfollowedId", followedId));
//    }

    public void publishUserDeletedEvent(UUID userId){
     //FIXME change userID type from int to UUID

        UUID hardcodedArtistID = UUID.fromString("b35a6f2c-972c-4dd3-876c-45a3a5ce0d1f");
        String message = hardcodedArtistID.toString();
        rabbitTemplate.convertAndSend(RabbitMQConfig.MUSIC_EVENT_EXCHANGE,
                RabbitMQConfig.USER_DELETED_ROUTING_KEY,
                message
        );

    }
}
