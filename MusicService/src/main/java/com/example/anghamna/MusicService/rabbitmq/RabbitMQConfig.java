package com.example.anghamna.MusicService.rabbitmq;


import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.Queue;



@Configuration
public class RabbitMQConfig {

    //exchanges
    //streaming exchange
    public static final String EXCHANGE = "music_exchange";

    //user deleted exchange
    //public static final String MUSIC_EVENT_EXCHANGE = "music.events";
    public static final String MUSIC_USER_DELETED_QUEUE = "music.user_deleted";
    public static final String USER_DELETED_ROUTING_KEY = "user.deleted";


    //song liked
//    public static final String SONG_LIKED_QUEUE = "song_liked_queue";
//    public static final String SONG_LIKED_ROUTING_KEY = "song_liked_routing";

    //song streamed
    public static final String SONG_STREAMED_QUEUE = "stream_played_queue";
    public static final String SONG_STREAMED_ROUTING_KEY = "stream.played";

    //song deleted
    public static final String SONG_DELETED_QUEUE = "song_deleted_queue";
    public static final String SONG_DELETED_ROUTING_KEY = "song.deleted";



    //exchange with streaming
    @Bean
    public TopicExchange exchange() {
        return new TopicExchange(EXCHANGE);
    }

    //user exchange
//    @Bean
//    public TopicExchange musicEventsExchange() {
//        return new TopicExchange(MUSIC_EVENT_EXCHANGE);
//    }

    //song liked queue and binding
//    @Bean
//    public Queue likeQueue() {
//        return new Queue(SONG_LIKED_QUEUE);
//    }
//
//    @Bean
//    public Binding likeBinding(Queue likeQueue, TopicExchange exchange) {
//        return BindingBuilder
//                .bind(likeQueue)
//                .to(exchange)
//                .with(SONG_LIKED_ROUTING_KEY);
//    }


    //song streamed queue and binding
    @Bean
    public Queue streamQueue() { return new Queue(SONG_STREAMED_QUEUE); }

    @Bean
    public Binding streamBinding(Queue streamQueue, TopicExchange exchange) {
        return BindingBuilder
                .bind(streamQueue)
                .to(exchange)
                .with(SONG_STREAMED_ROUTING_KEY);
    }

    //song deleted queue and binding
    @Bean
    public Queue deleteQueue() {
        return new Queue(SONG_DELETED_QUEUE);
    }

    @Bean
    public Binding deleteBinding(Queue deleteQueue, TopicExchange exchange) {
        return BindingBuilder
                .bind(deleteQueue)
                .to(exchange)
                .with(SONG_DELETED_ROUTING_KEY);
    }

    //user deleted queue and binding
    @Bean
    public Queue musicUserDeletedQueue() {
        return new Queue(MUSIC_USER_DELETED_QUEUE);
    }

    @Bean
    public Binding deleteUserBinding(Queue musicUserDeletedQueue, TopicExchange exchange) {
        return BindingBuilder
                .bind(musicUserDeletedQueue)
                .to(exchange)
                .with(USER_DELETED_ROUTING_KEY);
    }

}
