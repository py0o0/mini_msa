package com.example.msasbuser.service;

import com.example.msasbuser.entity.User;
import com.example.msasbuser.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final RedisTemplate<String, Object> redisTemplate;
    private final JavaMailSender mailSender;

    public void createUser(String email, String password, String userName, String role) {

        if(userRepository.findByEmail(email).isPresent())
            throw new IllegalArgumentException("User already exists");

        User user = new User();
        user.setEmail(email);
        user.setPassword(bCryptPasswordEncoder.encode(password));
        user.setUserName(userName);
        user.setRole(role);

        userRepository.save(user);

        // 인증 이메일 발송
        sendVaildEmail(user);
    }

    private void sendVaildEmail(User user) {
        // 이메일 내용 안에 인증 요청을 GET방식으로 요청하도록 URL 구성
        // 게이트웨이에 프리패스로 URL 등록되어야 한다
        // URL 합당하게 처리되기 위해 토큰 값 같이 전달
        // 일종의 토큰을 발급 -> 레디스에 저장(이메일 기준),유저에게 발송
        // 유저는 이메일로 확인 -> 클릭 -> 게이트웨이 -> 서비스진입 -> 전달된 토큰과 레디스에 저장된 토큰이 일치 -> 인증
        // 필요 시 레디스 저장할 때 만료시간 지정

        // 1. 토큰 발행
        String token = UUID.randomUUID().toString();
        // 2. redis 저장 -> 키는 이메일, 값은 토큰, 만료시간 6시간,
        redisTemplate.opsForValue().set(token, user.getEmail(), 6, TimeUnit.HOURS);
        // 3. URL 구성 -> 가입한 사용자의 이메일에서 인증메일에 전송된 링크
        String url = "http://localhost:8080/user/valid?token=" + token;
        // 4. 메일 전송
        sendMail( user.getEmail(), "Email 인증", "링크를 눌러서 인증: " + url );
    }

    private void sendMail(String email, String subject, String content) {
        // 1. 메세지 구성
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject(subject);
        message.setText(content);

        // 2. 전송
        mailSender.send(message);
    }

    // enable 컬럼 : f -> t (유효할 때만)
    public void updateActivate(String token) {
        // 1. 레디스 토큰 -> 이메일 흭득
        String email = (String) redisTemplate.opsForValue().get(token);
        // 2. 없다면 -> 잘못된 토큰 혹은 만료된 토큰
        if(email == null)
            throw new IllegalArgumentException("Invalid token");
        // 3. 존재한다면 -> 엔티티 흭득
        User user = userRepository.findByEmail(email)
                .orElseThrow( ()-> new IllegalArgumentException("사용자 오류(존재x)") );

        // 4. enable 컬럼 : f->t 저장
        user.setEnable(true);
        userRepository.save(user);
        // 4. 레디스 토큰 삭제
        redisTemplate.delete(token);
    }
}
