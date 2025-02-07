package com.example.apigateway.handler;


import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.server.authorization.ServerAccessDeniedHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/*
* 권한이 없는 유저가 사용자 요청을 할 경우
* 엑세스 거부 403 AccessDenied
* */
@Component
public class CustomAccessDeniedHandler implements ServerAccessDeniedHandler {

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, AccessDeniedException denied) {
        // 자바의 쓰레드 생성 방법 중 Runnable 객체를 비동기적으로 생성하는 코드
        return Mono.fromRunnable(() -> {
            exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
        });
    }
}
