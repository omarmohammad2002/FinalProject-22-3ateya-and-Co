package com.example.anghamna.MusicService.rabbitmq;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class MusicProducer {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    public void sendSongDeleted(UUID songId) {
        String message = songId.toString();
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.EXCHANGE,
                RabbitMQConfig.SONG_DELETED_ROUTING_KEY,
                message
        );
        System.out.println("Sent song deleted: " + songId);
    }
}
