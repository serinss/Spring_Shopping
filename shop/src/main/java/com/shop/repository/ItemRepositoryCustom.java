package com.shop.repository;

import com.shop.dto.ItemSearchDto;
import com.shop.entity.Item;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ItemRepositoryCustom {

    //상품 조회 조건을 담고 있는 ItemSearchDto 객체와 페이징 정보를 갖고 있는 Pageable 객체를 파라미터로 받는다
    Page<Item> getAdminItemPage(ItemSearchDto itemSearchDto, Pageable pageable);
}
