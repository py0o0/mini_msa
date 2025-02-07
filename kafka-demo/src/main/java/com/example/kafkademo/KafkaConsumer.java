package com.example.kafkademo;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KafkaConsumer {

    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "test-topic", groupId = "test-group") //그룹 id는 yml에 존재
    public void listen(String message) {
        System.out.println("프로듀서 메세지 " + message);
    }

    @KafkaListener(topics = "test-topic2", groupId = "test-group") //그룹 id는 yml에 존재
    public void listen2(String message) {
        try{
            objectMapper.readValue(message, OrderDto.class);
        }catch (Exception e){
            e.printStackTrace();
        }
        System.out.println("프로듀서 메세지 " + message);
    }
}
