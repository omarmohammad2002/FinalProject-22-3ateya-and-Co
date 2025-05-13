//package com.example.anghamna.StreamingService.rabbitmq.Services;
//import com.example.anghamna.StreamingService.Models.SongAddedEvent;
//import com.example.anghamna.StreamingService.Models.StreamPlayedEvent;
//import com.example.anghamna.StreamingService.Services.AudioService;
//import com.example.anghamna.StreamingService.rabbitmq.RabbitMQConfig;
//import org.springframework.amqp.rabbit.annotation.RabbitListener;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import java.io.IOException;
//import java.util.UUID;
//
//@Service
//public class StreamingService {
//
//    @Autowired
//    private AudioService audioService;
//
//
//    @RabbitListener(queues = RabbitMQConfig.SONG_ADDED_QUEUE)
//    public void handleSongAdded(SongAddedEvent event) throws IOException {
//        System.out.println("Received new song: " + event.getSongId());
//        audioService.uploadAudio(event.getSongId(), event.getFile());
//    }
//
//    @RabbitListener(queues = RabbitMQConfig.SONG_DELETED_QUEUE)
//    public void handleSongDeleted(UUID songId) throws IOException {
//        System.out.println("Received request to delete song: " + songId);
//        audioService.deleteAudio(songId);
//    }
//
//    @RabbitListener(queues = RabbitMQConfig.STREAM_PLAYED_QUEUE)
//    public void handleStreamPlayed(StreamPlayedEvent event) {
//        System.out.println("Stream played: " + event.getSongId() + " by user " + event.getUserId());
//        // Optional: log stream or notify observers
//    }
//}
