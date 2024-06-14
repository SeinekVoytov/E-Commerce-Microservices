package org.example.userservice.kafka.producer;

import lombok.RequiredArgsConstructor;
import org.example.userservice.kafka.message.OrderPublishedMessage;
import org.example.userservice.kafka.topics.TopicsNames;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderPublishedProducer {

    private final KafkaTemplate<String, OrderPublishedMessage> kafkaTemplate;

    public void publishOrder(OrderPublishedMessage message) {
        kafkaTemplate.send(TopicsNames.USER_ORDER_PUBLISHED_TOPIC, message);
    }
}