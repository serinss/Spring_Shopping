package com.shop.entity;

import com.shop.dto.MemberFormDto;
import com.shop.repository.CartRepository;
import com.shop.repository.MemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.PersistenceContext;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Transactional
@TestPropertySource(locations = "classpath:application-test.properties")
class CartTest {
    @Autowired
    CartRepository cartRepository;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @PersistenceContext
    EntityManager em;

    public Member createMember(){ //테스트용 회원 엔티티 생성
        MemberFormDto memberFormDto = new MemberFormDto();
        memberFormDto.setEmail("test@email.com");
        memberFormDto.setName("홍길동");
        memberFormDto.setAddress("서울시 마포구 합정동");
        memberFormDto.setPassword("1234");
        return Member.createMember(memberFormDto, passwordEncoder);
    }

    @Test
    @DisplayName("장바구니 회원 엔티티 매핑 조회 테스트")
    public void findCartAndMemberTest(){

        Member member = createMember();
        memberRepository.save(member);
        /*
        insert
        into
            member
            (address, email, name, password, role, member_id)
        values
            (?, ?, ?, ?, ?, ?)
         */

        Cart cart = new Cart();
        cart.setMember(member);
        cartRepository.save(cart);
        /*
        insert
        into
            cart
            (member_id, cart_id)
        values
            (?, ?)
         */

        em.flush(); // JPA는 영속성 컨텍스트에 데이터를 저장한 후, 트랜잭션이 끝날 때 flush()를 호출하여 DB에 반영
        em.clear(); // 실제 DB에서 장바구니 엔티티를 가지고 올 때, 회원 엔티티도 같이 가져오는지 보기 위해서 영속성 컨텍스트를 비운다

        Cart savedCart = cartRepository.findById(cart.getId()) // 장바구니 엔티티 조회
                .orElseThrow(EntityNotFoundException::new);
        assertEquals(savedCart.getMember().getId(), member.getId()); // 처음 저장한 member_id와 savedCart에 매핑된 member_id 비교
        /* cart 테이블과 member테이블을 조인해서 가져오는 쿼리문이 실행된다 -> 즉, cart 엔티티를 조회하면 member엔티티도 동시에 가져온다.
           -> 즉, 일대일/다대일 매핑인 경우 기본 Fetch전략이 EAGER이다.
        select
            cart0_.cart_id as cart_id1_0_0_,
            cart0_.member_id as member_i2_0_0_,
            member1_.member_id as member_i1_2_1_,
            member1_.address as address2_2_1_,
            member1_.email as email3_2_1_,
            member1_.name as name4_2_1_,
            member1_.password as password5_2_1_,
            member1_.role as role6_2_1_
        from
            cart cart0_
        left outer join
            member member1_
                on cart0_.member_id=member1_.member_id
        where
            cart0_.cart_id=?
         */
    }
}
