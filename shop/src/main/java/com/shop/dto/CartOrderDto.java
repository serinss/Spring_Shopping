package com.shop.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter @Setter
public class CartOrderDto {

    private Long cartItemId;
    //장바구니에서 여러 개 상품을 주문하므로 자신을 List로 저장
    private List<CartOrderDto> cartOrderDtoList;
}
