package com.example.kafkademo;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KafkaProducer {
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;
    public void sengMsg(String topic, String message) {
        kafkaTemplate.send(topic, message);
    }

    public void sengMsg(String topic, OrderDto orderDto) throws JsonProcessingException {
        kafkaTemplate.send(topic, objectMapper.writeValueAsString(orderDto));
    }
}
