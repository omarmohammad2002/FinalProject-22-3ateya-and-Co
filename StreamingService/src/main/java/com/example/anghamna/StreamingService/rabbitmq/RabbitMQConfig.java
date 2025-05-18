package com.example.anghamna.StreamingService.rabbitmq;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String SONG_ADDED_QUEUE = "song_added_queue";
    public static final String SONG_ADDED_ROUTING = "song.added";

    public static final String SONG_DELETED_QUEUE = "song_deleted_queue";
    public static final String SONG_DELETED_ROUTING = "song.deleted";

    public static final String STREAM_PLAYED_QUEUE = "stream_played_queue";
    public static final String STREAM_PLAYED_ROUTING = "stream.played";

    public static final String EXCHANGE = "music_exchange";

    @Bean
    public TopicExchange exchange() {
        return new TopicExchange(EXCHANGE);
    }

    @Bean
    public Queue songAddedQueue() {
        return new Queue(SONG_ADDED_QUEUE);
    }

    @Bean
    public Queue songDeletedQueue() {
        return new Queue(SONG_DELETED_QUEUE);
    }

    @Bean
    public Queue streamPlayedQueue() {
        return new Queue(STREAM_PLAYED_QUEUE);
    }

    @Bean
    public Binding bindingSongAdded(Queue songAddedQueue, TopicExchange exchange) {
        return BindingBuilder.bind(songAddedQueue).to(exchange).with(SONG_ADDED_ROUTING);
    }

    @Bean
    public Binding bindingSongDeleted(Queue songDeletedQueue, TopicExchange exchange) {
        return BindingBuilder.bind(songDeletedQueue).to(exchange).with(SONG_DELETED_ROUTING);
    }

    @Bean
    public Binding bindingStreamPlayed(Queue streamPlayedQueue, TopicExchange exchange) {
        return BindingBuilder.bind(streamPlayedQueue).to(exchange).with(STREAM_PLAYED_ROUTING);
    }
}
