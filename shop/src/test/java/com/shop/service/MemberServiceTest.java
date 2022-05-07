package com.shop.service;

import com.shop.dto.MemberFormDto;
import com.shop.entity.Member;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

@SpringBootTest
@Transactional
@TestPropertySource(locations = "classpath:application-test.properties")
public class MemberServiceTest {

    @Autowired
    MemberService memberService;

    @Autowired
    PasswordEncoder passwordEncoder;

    public Member createMember(){
        MemberFormDto memberFormDto = new MemberFormDto();
        memberFormDto.setEmail("test@email.com");
        memberFormDto.setName("홍길동");
        memberFormDto.setAddress("서울시 마포구 합정동");
        memberFormDto.setPassword("1234");
        return Member.createMember(memberFormDto, passwordEncoder);
    }

    //@Test
    @DisplayName("회원가입 테스트")
    public void saveMemberTest(){
        Member member = new Member();
        Member savedMember = memberService.saveMember(member);

        //import static org.junit.Assert.assertEquals; 임포트 필요
        //메소드를 이용하여 저장하고, 요청했던 값과 실제 저장된 데이터를 비교한다
       assertEquals(member.getEmail(), savedMember.getEmail());
       assertEquals(member.getName(), savedMember.getName());
       assertEquals(member.getAddress(), savedMember.getAddress());
       assertEquals(member.getPassword(), savedMember.getPassword());
       assertEquals(member.getRole(), savedMember.getRole());
    }
    //테스트가 통과되면 회원가입이 정상적으로 작동 된다는 것을 알 수 있다.

    @Test
    @DisplayName("중복 회원 가입 테스트")
    public void saveDuplicateMemberTest(){ //해당 진행과 결과값이 실제 실행 코드와 완전히 동일해야 함을 주의
        Member member1 = createMember();
        Member member2 = createMember();
        memberService.saveMember(member1);

        Throwable e = assertThrows(IllegalStateException.class, ()->{
            memberService.saveMember(member2);
        });

        assertEquals("이미 가입된 회원입니다.", e.getMessage());
    }
}
