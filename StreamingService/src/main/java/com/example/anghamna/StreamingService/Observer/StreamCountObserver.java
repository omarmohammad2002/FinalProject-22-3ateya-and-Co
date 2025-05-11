package com.example.anghamna.StreamingService.Observer;

import com.example.anghamna.StreamingService.Models.StreamPlayedEvent;
import com.example.anghamna.StreamingService.rabbitmq.StreamEventProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class StreamCountObserver implements StreamObserver {
    private StreamEventProducer streamEventProducer;
    private static final Logger logger = LoggerFactory.getLogger(StreamCountObserver.class);

    @Override
    public void onStream(UUID songId) {
        StreamPlayedEvent event = new StreamPlayedEvent();
        event.setSongId(songId);
        logger.info("🎧 Stream event observed for songId: {}", songId);
        streamEventProducer.sendStreamPlayed(event);
        // Here you could send to RabbitMQ, or just simulate the action.
        // e.g. streamEventPublisher.publish(new StreamEvent(songId));
    }
}
