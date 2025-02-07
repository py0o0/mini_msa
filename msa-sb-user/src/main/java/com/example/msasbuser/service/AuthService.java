package com.example.msasbuser.service;

import com.example.msasbuser.entity.User;
import com.example.msasbuser.jwt.JwtTokenProvider;
import com.example.msasbuser.repository.UserRepository;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final TokenService tokenService;

    public String login(String email, String password, HttpServletResponse res) {

        try {
            User user = userRepository.findByEmail(email).orElseThrow(() -> new IllegalArgumentException("Invalid email"));

            if(!bCryptPasswordEncoder.matches(password, user.getPassword())) {
                throw new IllegalArgumentException("Invalid password");
            }

            // 토큰 발급
            String accessToken = jwtTokenProvider.createAccessToken(email,password);

            String refreshToken = tokenService.getRefreshToken(accessToken);

            if(refreshToken == null) {
                refreshToken = jwtTokenProvider.createRefreshToken();

                tokenService.saveRefreshToken(email,refreshToken);
            }
            res.addHeader("RefreshToken", refreshToken);
            res.addHeader("AccessToken", accessToken);

            res.addHeader("X-Auth-User",email);


        } catch(Exception e){
            System.out.println("로그인 실패" + e.getMessage());
            return "로그인 실패";
        }

        return "로그인 성공";
    }


    public void logout(String email, String accessToken) {

        if(!jwtTokenProvider.validateToken(accessToken)) {
            throw new IllegalArgumentException("부적절한 토큰");
        }

        tokenService.removeRefreshToken(email);
    }
}
