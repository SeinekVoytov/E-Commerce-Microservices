package org.example.userservice.kafka.topics;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TopicsConfig {

    @Bean
    public NewTopic userTopic() {
        return new NewTopic(TopicsNames.USER_ORDER_PUBLISHED_TOPIC, 1, (short) 1);
    }
}