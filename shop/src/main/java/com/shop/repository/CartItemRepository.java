package com.shop.repository;

import com.shop.dto.CartDetailDto;
import com.shop.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    
    //카트 아이디 & 상품 아이디로 상품이 장바구니에 있는지 조회
    CartItem findByCartIdAndItemId(Long cartId, Long itemId);

    //카트 상세 조회
    @Query("select new com.shop.dto.CartDetailDto(ci.id, i.itemNm, i.price, ci.count, im.imgUrl) " +
            "from CartItem  ci, ItemImg  im " +
            "join ci.item i " +
            "where ci.cart.id = :cartId " +
            "and im.item.id = ci.item.id " +
            "and im.repimgYn = 'Y' " +
            "order by ci.regTime desc "
    )
    List<CartDetailDto> findCartDetailDtoList(Long cartId);
    //DTO를 반환할 때에는 해당 DTO의 패키지, 클래스 명을 모두 적어주고,생성자 파라미터 순서도 동일해야 한다
    //장바구니에 담겨 있는 상품의 대표 이미지만 가져오도록 작성

}
