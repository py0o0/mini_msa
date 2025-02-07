package com.example.msasbuser.kafka;

import com.example.msasbuser.dto.PaymentDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KafkaConsumer {

    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "msa-sb-products-payment", groupId = "test-group") //그룹 id는 yml에 존재
    public void listen2(String message) {
        try{
            PaymentDto paymentDto = objectMapper.readValue(message, PaymentDto.class);
            System.out.println("프로듀서 메세지 " + paymentDto.toString());
        }catch (Exception e){
            e.printStackTrace();
        }

    }
}
