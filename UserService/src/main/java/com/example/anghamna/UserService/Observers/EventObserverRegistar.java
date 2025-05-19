package com.example.anghamna.UserService.Observers;

import com.example.anghamna.UserService.rabbitmq.EventPublisher;
import com.example.anghamna.UserService.rabbitmq.RabbitMQEventListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;

@Component
public class EventObserverRegistar {

    @Autowired
    private EventPublisher eventPublisher;

    @Autowired
    private RabbitMQEventListener rabbitMQEventListener;

    @PostConstruct
    public void registerObservers() {
        eventPublisher.addObserver(rabbitMQEventListener);
    }
}
