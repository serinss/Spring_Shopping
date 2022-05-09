package com.shop.dto;

import com.shop.constant.ItemSellStatus;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ItemSearchDto {
    
    private String searchDateType; // 현재시간 & 상품 등록일 비교 (all, 1d, 1w, 1m, 6m)
    private ItemSellStatus searchSellStatus;
    private String searchBy; // 조회 유형 (itemNm, createBy)
    private String searchQuery=""; // 조회 검색어 저장변수
}
