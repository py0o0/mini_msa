package com.example.msasbuser.controller;

import com.example.msasbuser.service.AuthService;
import com.example.msasbuser.service.TokenService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
    private final TokenService tokenService;

    @PostMapping("/login")
    public ResponseEntity<String> login(String email, String password,
                                        HttpServletResponse res) {

        return ResponseEntity.ok(authService.login(email,password, res));
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(@RequestHeader("X-Auth-User") String email,
                                         @RequestHeader("Authorization") String accessToken) {
        System.out.println("재바랍랍랍ㄹ바잘");
        authService.logout(email, accessToken);
        return ResponseEntity.ok("로그아웃 성공");
    }
}
