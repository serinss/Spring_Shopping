package com.shop.service;

import com.shop.entity.Member;
import com.shop.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class MemberService implements UserDetailsService {

    //@RequiredArgsContructor 을 사용하여 final이나 @NonNull이 붙은 필드에 생성자를 생성해준다.
    //빈을 주입하는 @Autowired의 다른 방법
    private final MemberRepository memberRepository;

    public Member saveMember(Member member){
        validateDuplicateMember(member);
        return memberRepository.save(member);
    }

    private void validateDuplicateMember(Member member){ //이미 가입된 회원의 경우 IllegalStateException을 발생시킨다.
        Member findMember = memberRepository.findByEmail(member.getEmail());
        if(findMember != null){
            throw new IllegalStateException("이미 가입된 회원입니다.");
        }
    }
    
    @Override //UserDetailsService를 상속받아 loadUserByUsername() 메서드를 오버라이딩 한다. => 로그인할 유저의 email을 파라미터로 전달 받는다
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException{
        Member member = memberRepository.findByEmail(email);
        
        if(member == null){
            throw new UsernameNotFoundException(email);
        }

        //User 객체를 입력받은 내용으로 생성하여 리턴한다.
        return User.builder()
                .username(member.getEmail())
                .password(member.getPassword())
                .roles(member.getRole().toString())
                .build();
    }
}
