package com.shop.config;

import com.shop.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity //WebSecurityConfigurerAdapter를 상속받는 클래스에 해당 어노테이션을 선언하면 SpringSecurityFilterChain이 자동으로 포함된다.
public class SecurityConfig extends WebSecurityConfigurerAdapter { //메서드 오버라이딩을 통해 보안 설정을 커스터마이징 가능

    @Autowired
    MemberService memberService;

    @Override
    protected void configure(HttpSecurity http) throws Exception { //http 요청에 대한 보안 설정
        http.formLogin()
                .loginPage("/members/login") //로그인 페이지 URL 설정
                .defaultSuccessUrl("/") //로그인 성공 시, 이동 URL
                .usernameParameter("email") //로그인 시, 사용할 파라미터 이름을 email로 지정
                .failureUrl("/members/login/error") // 로그인 실패 시, 이동 URL
                .and()
                .logout()
                .logoutRequestMatcher(new AntPathRequestMatcher("/members/logout")) //로그아웃 URL 설정
                .logoutSuccessUrl("/");

        http.authorizeRequests() //시큐리티 처리에 HttpServletRequest를 이용한다
                .mvcMatchers("/","/members/**","/item/**","/images/**").permitAll() //로그인(인증)없이는 해당 경로에 접근할 수 없다
                .mvcMatchers("/admin/**").hasRole("ADMIN") // "/admin" 경로는 해당 계정이 ADMIN Role일 경우에만 접근 가능
                .anyRequest().authenticated(); //위에서 설정한 경로를 제외한 나머지는 모두 인증을 요구하도록 설정

        http.exceptionHandling()
                .authenticationEntryPoint(new CustomAuthenticationEntryPoint()); //인증되지 않는 사용자가 리소스에 접근하였을 때 수행되는 핸들러
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/css/**", "/js/**", "/img/**"); //static 디렉터리 하위 파일은 인증을 무시
    }

    @Bean
    public PasswordEncoder passwordEncoder(){ //비밀번호를 DB에 그대로 저장하면, 해킹당했을 경우 회원 정보가 그대로 노출됨 -> 암호화하여 저장하자
        return new BCryptPasswordEncoder();
    }

    /*
    시큐리티에서 인증은 AuthenticationManager를 통해 이루어지므로 AuthenticationManagerBuilder를 통해 만들어야 한다.
     */
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception{
        auth.userDetailsService(memberService).passwordEncoder(passwordEncoder());
    }

}
