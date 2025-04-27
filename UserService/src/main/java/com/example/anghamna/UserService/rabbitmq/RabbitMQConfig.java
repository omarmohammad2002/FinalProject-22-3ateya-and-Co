//package com.example.hotel.NotificationService.rabbitmq;
//
//
//import org.springframework.amqp.core.BindingBuilder;
//import org.springframework.amqp.core.TopicExchange;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//import org.springframework.amqp.core.Binding;
//import org.springframework.amqp.core.Queue;
//
//
//
//@Configuration
//public class RabbitMQConfig {
//    public static final String BOOKING_QUEUE = "booking_queue_Hana_52_25989";
//    public static final String EXCHANGE = "Hana_52_25989";
//    public static final String BOOKING_ROUTING = "booking_routing_Hana_52_25989";
//
//
//    @Bean
//    public Queue queue() {
//        return new Queue(BOOKING_QUEUE);
//    }
//
//    @Bean
//    public TopicExchange exchange() {
//        return new TopicExchange(EXCHANGE);
//    }
//
//    @Bean
//    public Binding binding(Queue queue, TopicExchange exchange) {
//        return BindingBuilder
//                .bind(queue)
//                .to(exchange)
//                .with(BOOKING_ROUTING);
//    }
//
//}
