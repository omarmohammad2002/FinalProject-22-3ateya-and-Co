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

    // Define routing keys for follow/unfollow events
    public static final String USER_FOLLOWED_ROUTING_KEY = "user.followed";
    public static final String USER_UNFOLLOWED_ROUTING_KEY = "user.unfollowed";

    // Define queue names for follow/unfollow
    public static final String SOCIAL_MEDIA_QUEUE = "social_media.user_followed";

    // Declare the Exchange for user events
    @Bean
    public TopicExchange userEventsExchange() {
        return new TopicExchange(USER_EVENTS_EXCHANGE);
    }

    // Declare the Social exchange for social-related events
    @Bean
    public TopicExchange socialEventsExchange() {
        return new TopicExchange(SOCIAL_EVENTS_EXCHANGE);
    }

    // Declare queue for the social media service to listen to follow/unfollow events
    @Bean
    public Queue socialMediaQueue() {
        return new Queue(SOCIAL_MEDIA_QUEUE);
    }

    // Bind queue to exchange for the follow/unfollow routing key
    @Bean
    public Binding socialMediaBinding(Queue socialMediaQueue, TopicExchange userEventsExchange) {
        return BindingBuilder
                .bind(socialMediaQueue)
                .to(userEventsExchange)
                .with(USER_FOLLOWED_ROUTING_KEY);
    }

    @Bean
    public Binding socialMediaUnfollowBinding(Queue socialMediaQueue, TopicExchange userEventsExchange) {
        return BindingBuilder
                .bind(socialMediaQueue)
                .to(userEventsExchange)
                .with(USER_UNFOLLOWED_ROUTING_KEY);
    }



}
