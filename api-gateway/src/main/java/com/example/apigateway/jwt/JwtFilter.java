package com.example.apigateway.jwt;

import com.example.apigateway.jwt.JwtTokenProvider;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.ArrayList;

@Component
@RequiredArgsConstructor
public class JwtFilter implements WebFilter, ApplicationContextAware {

    private final JwtTokenProvider jwtTokenProvider;
    private ApplicationContext applicationContext;

    // 인증에 관련없는 URL 목록 정의
    private final String[] FREE_PATHS = {
            "/",
            "/auth/login",
            "/user/signup",
            "/user/valid"
    };

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    /*
    * Http 요청을 가로채서, 필터 체인에 적용된 루틴을 반영하여 필터링 처리
    * - 요청 필터링 : 요청에 대한 검증, 로그, 인증, 권한 등 검사
    * - 응답 필터링 : 응답을 가로채서 응답코드 변경, 추가값 설정 등 가능함
    *
    * - 아래과정이 간단하게 구성되기 위해서 -> JwtTokenProvider(토큰 관리, 공급, 검증)
    */
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        // 1. 요청 URL 확인 로그 출력
        String reqUrl = exchange.getRequest().getURI().getPath();
        // 2. 스프링 시큐리티에서 퍼밋 올인 URL들은 바로 통과 (체크 필요)
        AntPathMatcher matcher = new AntPathMatcher(); // 해당 객체를 통해서 반복 검사
        for(String path : FREE_PATHS) {
            if(matcher.match(path, reqUrl)) {
                System.out.println("인증없이 통과 처리 {}" + reqUrl);
                return chain.filter(exchange);
            }
        }
        // 3. 인증을 필요로 하는 요청만 도달 -> 요청 프로토콜의 헤더에서 토큰 추출
        String token = exchange.getRequest().getHeaders().getFirst("Authorization");
        System.out.println("token :" + token);
        // 4. 토큰이 존재한다면
        if(token != null) {
            try {
                // 4-1. 인증 절차 (회원 정보 중에서 이메일 집중적으로 체크) -> 토큰의 추가 정보에서 추출됨

                String email = jwtTokenProvider.getEmailFromToken(token);
                System.out.println("email : " + email);

                UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken
                        (new User(email,"",new ArrayList<>()),null,null);

                return chain.filter( exchange.mutate()
                        .request(exchange.getRequest().mutate()
                                .header("X-Auth-User",email).build())
                        .build())
                        .contextWrite(ReactiveSecurityContextHolder.withAuthentication(auth));

                // 4-3. 토큰의 유효성 검사 -> 인증이 통과되었다면 인증객체를 요청에 세팅해서 전달
            }catch(ExpiredJwtException e){
                // 회원가입 -> 로그인 완료 후 진행
                // 리프레시 토큰 활용

                // 4-2. 기간 만료 토큰 -> 리플레시 토큰을 통해서 엑세스 토큰 재발급 시나리오 진행
                // 1. 이메일 정보를 토큰으로 부터 추출 (기간이 만료되었어도 정보 추출 가능)
                // 2. 이메일을 이용 redis를 통해서 리프레시 토큰 흭득 -> 로그인 진행 시 액세스/리프레시 토큰 발급(redis 저장예정)
                // 3. 리플레시 토큰의 유효성 검사, 존재여부 검사
                // 4. (엑세스) 토큰 발급
                // 5. 응답 헤더에 엑세스 토큰 세팅 -> 응답을 받은후 쿠키쪽에서 갱신 처리 됨
                // 6. 위에서 진행했던 절차 반복 : 요청 헤더데 표식, 인증정보 시큐리티컨텍스트에 설정

            }catch(Exception e) {
                throw new RuntimeException(e);
            }
        }
        return chain.filter(exchange);
    }
}
