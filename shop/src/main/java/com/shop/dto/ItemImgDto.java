package com.shop.dto;

import com.shop.entity.ItemImg;
import lombok.Getter;
import lombok.Setter;
import org.modelmapper.ModelMapper;

@Getter @Setter
public class ItemImgDto {

    private Long id;

    private String imgName;

    private String oriImgName;

    private String imgUrl;

    private String repImgYn;
    private static ModelMapper modelMapper = new ModelMapper(); //ModelMapper 객체 생성

    //ItemImg 엔티티 객체를 파라미터로 받아서 자료형과 멤버변수 이름이 같으면 itemImgDto값을 복사해서 반환
    public static ItemImgDto of(ItemImg itemImg) {
        return modelMapper.map(itemImg,ItemImgDto.class);
    }
}
