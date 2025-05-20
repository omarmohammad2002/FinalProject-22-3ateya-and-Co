package com.example.anghamna.UserService.rabbitmq;

import com.example.anghamna.UserService.Observers.Observer;
import com.example.anghamna.UserService.Observers.Subject;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class EventPublisher  implements Subject {

//    @Autowired
//    private RabbitTemplate rabbitTemplate;
//
//
////    public EventPublisher(RabbitTemplate rabbitTemplate) {
////        this.rabbitTemplate = rabbitTemplate;
////    }
//
//    // Publish follow event
////    public void publishFollowEvent(String followerId, String followedId) {
////        rabbitTemplate.convertAndSend(RabbitMQConfig.USER_EVENTS_EXCHANGE,
////                RabbitMQConfig.USER_FOLLOWED_ROUTING_KEY,
////                Map.of("followerId", followerId, "followedId", followedId));
////    }
//
//    // Publish unfollow event
////    public void publishUnfollowEvent(String followerId, String followedId) {
////        rabbitTemplate.convertAndSend(RabbitMQConfig.USER_EVENTS_EXCHANGE,
////                RabbitMQConfig.USER_UNFOLLOWED_ROUTING_KEY,
////                Map.of("followerId", followerId, "unfollowedId", followedId));
////    }
//
//    public void publishUserDeletedEvent(UUID userId){
//
//        String message = userId.toString();
//        rabbitTemplate.convertAndSend(
//                RabbitMQConfig.EXCHANGE,
//                RabbitMQConfig.USER_DELETED_ROUTING_KEY,
//                message
//        );
//
//    }


    private final List<Observer> observers = new ArrayList<Observer>();

    public void addObserver(Observer observer) {
        observers.add(observer);
    }

    public void removeObserver(Observer observer) {
        observers.remove(observer);
    }

    @Override
    public void notifyObserversUserDeleted(UUID userId) {
        for (Observer observer : observers) {
            observer.onUserDeleted(userId);
        }
    }
}
