package com.shop.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter @Setter
@Table(name = "cart_item")
public class CartItem extends BaseEntity{
    /*
    장바구니(Cart) -> 장바구니에 든 아이템(CartItem) -> 해당 아이템의 정보(Item)
    둘 다 다대일 단방향 매핑
     */
    @Id
    @GeneratedValue
    @Column(name = "cart_item_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY) //1(하나의 장바구니)-N(여러개 물품)
    @JoinColumn(name = "cart_id")
    private Cart cart;

    @ManyToOne(fetch = FetchType.LAZY) //1(하나의 상품)-N(여러개 장바구니)
    @JoinColumn(name = "item_id")
    private Item item;

    private int count;

    // 장바구니에 상품 담기
    public static CartItem createCartItem(Cart cart, Item item, int count){
        CartItem cartItem = new CartItem();
        cartItem.setCart(cart);
        cartItem.setItem(item);
        cartItem.setCount(count);
        return cartItem;
    }

    //기존에 이미 담겨있는 상품일 경우 수량만 +1
    public void addCount(int count){
        this.count += count;
    }
}
