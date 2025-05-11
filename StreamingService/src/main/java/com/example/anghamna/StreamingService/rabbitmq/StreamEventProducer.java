package com.example.anghamna.StreamingService.rabbitmq;

import com.example.anghamna.StreamingService.Models.StreamPlayedEvent;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class StreamEventProducer {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    public void sendStreamPlayed(StreamPlayedEvent event) {
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.EXCHANGE,
                RabbitMQConfig.STREAM_PLAYED_ROUTING,
                event
        );
        System.out.println("Sent stream played event: " + event.getSongId());
    }
}
