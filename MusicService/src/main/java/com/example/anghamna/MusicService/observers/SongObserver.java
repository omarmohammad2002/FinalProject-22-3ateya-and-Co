package com.example.anghamna.MusicService.observers;

import com.example.anghamna.MusicService.rabbitmq.MusicProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class SongObserver implements Observer {

    @Autowired
    private MusicProducer producer;

    @Override
    public void onSongDeleted(UUID songId) {
        if (producer != null) {
            producer.sendSongDeleted(songId);
        } else {
            System.out.println("MusicProducer is null - cannot send deletion notification");
        }
    }
}
