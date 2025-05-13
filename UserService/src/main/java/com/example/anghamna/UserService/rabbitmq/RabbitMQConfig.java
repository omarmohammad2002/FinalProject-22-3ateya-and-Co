package com.example.anghamna.UserService.rabbitmq;


import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.Queue;



@Configuration
public class RabbitMQConfig {

    // Define exchange names
    public static final String USER_EVENTS_EXCHANGE = "user.events";
    public static final String SOCIAL_EVENTS_EXCHANGE = "social.events";

    public static String MUSIC_EVENT_EXCHANGE = "music.events";


    public static final String USER_DELETED_ROUTING_KEY = "user.deleted";


    public static final String MUSIC_USER_DELETED_QUEUE = "music.user_deleted";

    // Declare the Exchange for user events
    @Bean
    public TopicExchange userEventsExchange() {
        return new TopicExchange(USER_EVENTS_EXCHANGE);
    }

    // Declare the Social exchange for social-related events
//    @Bean
//    public TopicExchange socialEventsExchange() {
//        return new TopicExchange(SOCIAL_EVENTS_EXCHANGE);
//    }

    @Bean
    public TopicExchange musicEventsExchange(){
        return  new TopicExchange(MUSIC_EVENT_EXCHANGE);
    }






}
