package com.example.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/redis")
@RequiredArgsConstructor
public class RedisController {
    private final RedisService redisService;
    // 데이터 추가
    @PostMapping("/store")
    public ResponseEntity<String> store(@RequestParam("email") String email) {
        System.out.println(email);
        String token = UUID.randomUUID().toString(); // 랜덤으로 기기에서 토큰 생성

        // 저장
        redisService.store(email, token);

        // 응답
        return ResponseEntity.ok("테스트 완료");

    }

    @GetMapping("/detail")
    public ResponseEntity<String> detail(@RequestParam("email") String email) {
        return ResponseEntity.ok("토큰 : " + redisService.getTokenByEmail(email));
    }

    // 데이터 추출
}
