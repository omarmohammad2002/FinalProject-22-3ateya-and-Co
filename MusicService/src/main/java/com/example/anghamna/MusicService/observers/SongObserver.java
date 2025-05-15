package com.example.anghamna.MusicService.observers;

import com.example.anghamna.MusicService.rabbitmq.MusicProducer;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class SongObserver implements Observer {

    private MusicProducer producer;

    @Override
    public void onSongDeleted(UUID songId) {
        producer.sendSongDeleted(songId);
    }
}
