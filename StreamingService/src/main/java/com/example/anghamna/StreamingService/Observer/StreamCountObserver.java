package com.example.anghamna.StreamingService.Observer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class StreamCountObserver implements StreamObserver {

    private static final Logger logger = LoggerFactory.getLogger(StreamCountObserver.class);

    @Override
    public void onStream(UUID songId) {
        logger.info("🎧 Stream event observed for songId: {}", songId);
        // Here you could send to RabbitMQ, or just simulate the action.
        // e.g. streamEventPublisher.publish(new StreamEvent(songId));
    }
}
