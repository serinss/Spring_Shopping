package com.shop.entity;

import com.shop.constant.OrderStatus;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
@Getter @Setter
public class Order extends BaseEntity{

    @Id @GeneratedValue
    @Column(name = "order_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY) //1(회원)-N(여러 주문)
    @JoinColumn(name = "member_id")
    private Member member;

    private LocalDateTime orderDate;

    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;

    // 주문 상품과 일대다 매핑 -> 외래키(order_id)가 OrderItem에 있으므로 연관관계의 주인은 OrderItem
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<OrderItem> orderItems = new ArrayList<>();

    private LocalDateTime regTime;

    private LocalDateTime updateTime;

    //주문 상품 정보들을 담는다 orderItem -> orderItems(order)
    public void addOrderItem(OrderItem orderItem){
        orderItems.add(orderItem);
        orderItem.setOrder(this);
    }

    public static Order createOrder(Member member, List<OrderItem> orderItemList){
        Order order = new Order();
        order.setMember(member);
        
        // 상품 페이지에서는 1개의 상품을 주문하지만, 장바구니에서는 한 번에 여러 상품을 주문할 수 있으므로
        // 여러 개의 주문 상품을 담을 수 있도록 리스트 형태로 파라미터 값을 받는다.
        for(OrderItem orderItem : orderItemList){
            order.addOrderItem(orderItem);
        }
        order.setOrderStatus(OrderStatus.ORDER); //주문 상태 세팅
        order.setOrderDate(LocalDateTime.now()); //주문 시간 현재
        return order;
    }

    // 총 주문 금액을 구하는 메서드
    public int getTotalPrice(){
        int totalPrice = 0;
        for(OrderItem orderItem:orderItems){
            totalPrice += orderItem.getTotalPrice();
        }
        return totalPrice;
    }
    
    //주문 취소
    public void cancelOrder(){
        this.orderStatus = OrderStatus.CANCEL;
        
        for(OrderItem orderItem:orderItems){
            orderItem.cancel();
        }
    }
}
