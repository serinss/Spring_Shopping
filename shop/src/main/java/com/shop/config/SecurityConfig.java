package com.shop.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableWebSecurity //WebSecurityConfigurerAdapter를 상속받는 클래스에 해당 어노테이션을 선언하면 SpringSecurityFilterChain이 자동으로 포함된다.
public class SecurityConfig extends WebSecurityConfigurerAdapter { //메서드 오버라이딩을 통해 보안 설정을 커스터마이징 가능

    @Override
    protected void configure(HttpSecurity http) throws Exception { //http 요청에 대한 보안 설정

    }

    @Bean
    public PasswordEncoder passwordEncoder(){ //비밀번호를 DB에 그대로 저장하면, 해킹당했을 경우 회원 정보가 그대로 노출됨 -> 암호화하여 저장하자
        return new BCryptPasswordEncoder();
    }
}
