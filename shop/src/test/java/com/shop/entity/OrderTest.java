package com.shop.entity;

import com.shop.constant.ItemSellStatus;
import com.shop.repository.ItemRepository;
import com.shop.repository.MemberRepository;
import com.shop.repository.OrderItemRepository;
import com.shop.repository.OrderRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.PersistenceContext;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@TestPropertySource(locations="classpath:application-test.properties")
@Transactional
class OrderTest {

    @Autowired
    OrderRepository orderRepository;

    @Autowired
    ItemRepository itemRepository;

    @PersistenceContext
    EntityManager em;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    OrderItemRepository orderItemRepository;

    public Item createItem(){
        Item item = new Item();
        item.setItemNm("테스트 상품");
        item.setPrice(10000);
        item.setItemDetail("상세설명");
        item.setItemSellStatus(ItemSellStatus.SELL);
        item.setStockNumber(100);
        item.setRegTime(LocalDateTime.now());

        item.setUpdateTime(LocalDateTime.now());
        return item;
    }

    //@Test
    @DisplayName("영속성 전이 테스트")
    public void cascadeTest() {

        Order order = new Order();

        for(int i=0;i<3;i++){
            Item item = this.createItem();
            itemRepository.save(item);
            OrderItem orderItem = new OrderItem();
            orderItem.setItem(item);
            orderItem.setCount(10);
            orderItem.setOrderPrice(1000);
            orderItem.setOrder(order);
            order.getOrderItems().add(orderItem); //orderItem을 order엔티티에 담기
        }
        /*
        insert
        into
            item
            (item_detail, item_nm, item_sell_status, price, reg_time, stock_number, update_time, item_id)
        values
            (?, ?, ?, ?, ?, ?, ?, ?)
         */

        orderRepository.saveAndFlush(order); //DB에 반영
        em.clear(); // 초기화하여 테스트

        Order savedOrder = orderRepository.findById(order.getId()) //DB에서 주문 엔티티 조회
                .orElseThrow(EntityNotFoundException::new);
        assertEquals(3, savedOrder.getOrderItems().size()); //실제로 3개가 DB에 저장되었는지 확인한다.
    }

    public Order createOrder(){
        Order order = new Order();
        for(int i=0;i<3;i++){
            Item item = createItem();
            itemRepository.save(item);
            OrderItem orderItem = new OrderItem();
            orderItem.setItem(item);
            orderItem.setCount(10);
            orderItem.setOrderPrice(1000);
            orderItem.setOrder(order);
            order.getOrderItems().add(orderItem);
        }
        Member member = new Member();
        memberRepository.save(member);

        order.setMember(member);
        orderRepository.save(order);

        return order;
    }

    //@Test
    @DisplayName("고아객체 제거 테스트")
    public void orphanRemovalTest(){
        Order order = this.createOrder();
        order.getOrderItems().remove(0);
        em.flush();
        /* 고아 객체 제거 완
        delete
        from
            order_item
        where
            order_item_id=?
         */
    }

    @Test
    @DisplayName("지연 로딩 테스트")
    public void lazyLoadingTest(){
        Order order = this.createOrder(); //주문 데이터 저장
        Long orderItemId = order.getOrderItems().get(0).getId();
        em.flush();
        em.clear();

        OrderItem orderItem = orderItemRepository.findById(orderItemId) //order엔티티에 저장했던 주문 상품 아이디를 이용하여 orderItem 조회
                .orElseThrow(EntityNotFoundException::new);

        System.out.println("Order class : " + orderItem.getOrder().getClass());
        System.out.println("===========================");
        orderItem.getOrder().getOrderDate();
        System.out.println("===========================");
        /*
        Order class : class com.shop.entity.Order$HibernateProxy$vjc7rD3f <-프록시 객체
        기존의 EAGER방식으로는 테이블에 연관된 모든 정보들을 select 해서 가져왔지만, LAZY방식으로는 딱 order_id, member_id에 관해서만 정보를 조인해서 가져옴
        ===========================
        Hibernate: 
            select
                order0_.order_id as order_id1_5_0_,
                order0_.member_id as member_i6_5_0_,
                order0_.order_date as order_da2_5_0_,
                order0_.order_status as order_st3_5_0_,
                order0_.reg_time as reg_time4_5_0_,
                order0_.update_time as update_t5_5_0_,
                member1_.member_id as member_i1_3_1_,
                member1_.address as address2_3_1_,
                member1_.email as email3_3_1_,
                member1_.name as name4_3_1_,
                member1_.password as password5_3_1_,
                member1_.role as role6_3_1_ 
            from
                orders order0_ 
            left outer join
                member member1_ 
                    on order0_.member_id=member1_.member_id 
            where
                order0_.order_id=?
         */
    }
}
