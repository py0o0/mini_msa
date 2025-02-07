package com.example.kafkademo;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/kafka")
@RequiredArgsConstructor
public class KafkaController {
    private final KafkaProducer producer;

    @GetMapping("/send")
    public String sendMessage(String message) {
        producer.sengMsg("test-topic", message);
        return "전송 서섹스 : " + message;
    }

    @PostMapping("/send")
    public String sendMessage1(OrderDto orderDto) {
        try {
            producer.sengMsg("test-topic2", orderDto);
        }catch (Exception e) {
            throw new RuntimeException(e);
        }
        return "전송 서섹스 : " + orderDto.toString();
    }
}
