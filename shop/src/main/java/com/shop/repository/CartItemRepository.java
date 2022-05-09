package com.shop.repository;

import com.shop.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    
    //카트 아이디 & 상품 아이디로 상품이 장바구니에 있는지 조회
    CartItem findByCartIdAndItemId(Long cartId, Long itemId);
}
