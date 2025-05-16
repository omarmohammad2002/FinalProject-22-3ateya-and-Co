package com.example.anghamna.StreamingService.rabbitmq;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class StreamEventProducer {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    public void sendStreamPlayed(UUID songID) {
        String message = songID.toString();
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.EXCHANGE,
                RabbitMQConfig.STREAM_PLAYED_ROUTING,
                message
        );
        System.out.println("Sent stream played event: " + songID);
    }
}
