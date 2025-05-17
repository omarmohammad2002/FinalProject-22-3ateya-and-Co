package com.example.anghamna.UserService.rabbitmq;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class EventPublisher {

    @Autowired
    private RabbitTemplate rabbitTemplate;


//    public EventPublisher(RabbitTemplate rabbitTemplate) {
//        this.rabbitTemplate = rabbitTemplate;
//    }

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

        String message = userId.toString();
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.EXCHANGE,
                RabbitMQConfig.USER_DELETED_ROUTING_KEY,
                message
        );

    }
}
