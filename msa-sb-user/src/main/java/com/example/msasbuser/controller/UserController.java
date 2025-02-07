package com.example.msasbuser.controller;

import com.example.msasbuser.dto.UserDto;
import com.example.msasbuser.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.types.Expiration;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/echo")
    public String echo() {
        return "echo";
    }

    @PostMapping("/signup")
    public ResponseEntity<String> signup(String email, String password, String userName, String role) {
        System.out.println(email);
        userService.createUser(email, password, userName, role);

        return ResponseEntity.ok("성공");
    }
    // 이메일 검증 처리 -> 인증메일 처리 -> enable의 값을 f->t
    // GET, /vaild, 엑세스토큰없이 전근가능해야함(로그인전->게이트웨이 수정), 파라미터 token, 고객 테이블 업데이트

    @GetMapping("/valid")
    public ResponseEntity<String> valid(String token) {
        try{

            userService.updateActivate(token);
            return ResponseEntity.ok("인증 완료");
        }
        catch (IllegalArgumentException e){
            return ResponseEntity.status(400).body("잘못된 요청" + e.getMessage());
        }catch (Exception e){
            return ResponseEntity.status(500).body("서버 에러" + e.getMessage());
        }

    }
}
