package com.example.apigateway.config;

import com.example.apigateway.handler.CustomAccessDeniedHandler;
import com.example.apigateway.handler.CustomAuthenticationEntryPoint;
import com.example.apigateway.jwt.JwtFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.context.NoOpServerSecurityContextRepository;


@Configuration
// spring webflux 방식의 시큐리티 설정 필요
@EnableWebFluxSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtFilter jwtFilter;


    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // webflux 보안 구성 설정, SecurityFilterChain (x)
    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {

        // http 설정
        http
            // cors 설정
                .cors(Customizer.withDefaults())
            // csrf 공격에 대한 보호 설정 -> 비활성
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
            // iframe 허용에 대한 설정
                .headers(header -> header.frameOptions(ServerHttpSecurity.HeaderSpec.FrameOptionsSpec::disable))

            // 보안 컨텍스트 저정소 활성화 부분 -> 레디스 사용
            // 인증, 세션관리를 스프링에서 처리하지 않고, 별도로 관리한다면 -> 옵션 적용 가능
            // Context -> 세션을 보관하는 곳
                .securityContextRepository(NoOpServerSecurityContextRepository.getInstance())

            // 인증 없이 접근 가능한 페이지
                .authorizeExchange(auth -> auth
                        .pathMatchers("/",
                                "/auth/login",
                                "/user/signup",
                                "/user/valid").permitAll()
                        .anyExchange().authenticated()
                )
            // 간편한 예외 처리
                .exceptionHandling(ex -> ex
                        .accessDeniedHandler(new CustomAccessDeniedHandler())  //403
                        .authenticationEntryPoint(new CustomAuthenticationEntryPoint()) //401
                )
            // jwt 인증 처리
                .addFilterAt(jwtFilter, SecurityWebFiltersOrder.AUTHENTICATION);

        return http.build();
    }
}
