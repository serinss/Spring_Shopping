package com.shop.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter @Setter
@Table(name = "cart_item")
public class CartItem {
    /*
    장바구니(Cart) -> 장바구니에 든 아이템(CartItem) -> 해당 아이템의 정보(Item)
    둘 다 다대일 단방향 매핑
     */
    @Id
    @GeneratedValue
    @Column(name = "cart_item_id")
    private Long id;

    @ManyToOne //1(하나의 장바구니)-N(여러개 물품)
    @JoinColumn(name = "cart_id")
    private Cart cart;

    @ManyToOne //1(하나의 상품)-N(여러개 장바구니)
    @JoinColumn(name = "item_id")
    private Item item;

    private int count;
}
