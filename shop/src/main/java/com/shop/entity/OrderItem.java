package com.shop.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter @Setter
public class OrderItem extends BaseEntity{

    @Id @GeneratedValue
    @Column(name = "order_item_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY) //1(상품)-N(여러 주문) -> 주문상품 기준으로 다대일 단방향 매핑 설정
    @JoinColumn(name = "item_id")
    private Item item;

    @ManyToOne(fetch = FetchType.LAZY) //1(주문)-N(여러 상품) -> 주문상품 엔티티와 주문 엔티티를 다대일 단방향 매핑 먼저 설정 -> 그 다음 Order테이블에서 일대다 매핑
    @JoinColumn(name = "order_id")
    private Order order;

    private int orderPrice;
    private int count;
    //private LocalDateTime regTime; 삭제, BaseEntity 상속
    //private LocalDateTime updateTime;
    
    //상품 주문
    public static OrderItem createOrderItem(Item item, int count){
        OrderItem orderItem = new OrderItem();
        orderItem.setItem(item);
        orderItem.setCount(count);
        orderItem.setOrderPrice(item.getPrice());
        
        //주문한 수량만큼 재고 감소
        item.removeStock(count);
        return orderItem;
    }
    
    //주문 가격과 수량을 곱하여 총 가격을 계산
    public int getTotalPrice(){
        return orderPrice*count;
    }

    //주문 취소
    public void cancel(){
        this.getItem().addStock(count);
    }
}
