package com.example.anghamna.UserService.rabbitmq;

import com.example.anghamna.UserService.Observers.Observer;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class RabbitMQEventListener implements Observer {

    private final RabbitTemplate rabbitTemplate;

    public RabbitMQEventListener(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @Override
    public void onUserDeleted(UUID userId) {
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.EXCHANGE,
                RabbitMQConfig.USER_DELETED_ROUTING_KEY,
                userId.toString()
        );
    }


}
