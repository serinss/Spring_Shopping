package com.shop.service;

import com.shop.dto.CartDetailDto;
import com.shop.dto.CartItemDto;
import com.shop.dto.CartOrderDto;
import com.shop.dto.OrderDto;
import com.shop.entity.Cart;
import com.shop.entity.CartItem;
import com.shop.entity.Item;
import com.shop.entity.Member;
import com.shop.repository.CartItemRepository;
import com.shop.repository.CartRepository;
import com.shop.repository.ItemRepository;
import com.shop.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.util.StringUtils;

import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CartService {

    private final ItemRepository itemRepository;
    private final MemberRepository memberRepository;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final OrderService orderService;

    public Long addCart(CartItemDto cartItemDto, String email){
        // 장바구니 담을 상품 엔티티 조회
        Item item = itemRepository.findById(cartItemDto.getItemId())
                .orElseThrow(EntityNotFoundException::new);
        // 현재 로그인한 회원 엔티티 조회
        Member member = memberRepository.findByEmail(email);

        // 로그인 한 아이디의 장바구니 엔티티 조회
        Cart cart = cartRepository.findByMemberId(member.getId());
        if(cart == null){ // 처음으로 장바구니 담을 경우 회원 장바구니 엔티티 생성
            cart = Cart.createCart(member);
            cartRepository.save(cart);
        }

        // 이미 장바구니에 있느 상품인지 조회
        CartItem savedCartItem = cartItemRepository.findByCartIdAndItemId(cart.getId(), item.getId());
        if(savedCartItem != null){ // 이미 장바구니에 있는 경우 현재 장바구니에 담을 수량만큼 ++
            savedCartItem.addCount(cartItemDto.getCount());

            return savedCartItem.getId();
        } else{
            // 없는 경우 장바구니 엔티티, 상품 엔티티, 장바구니에 담을 수량을 이용하여 CartItem 엔티티 생성
            CartItem cartItem = CartItem.createCartItem(cart, item, cartItemDto.getCount());
            cartItemRepository.save(cartItem);

            return cartItem.getId();
        }
    }

    // 현재 로그인 한 회원의 장바구니 조회
    @Transactional(readOnly = true)
    public List<CartDetailDto> getCartList(String email){

        List<CartDetailDto> cartDetailDtoList = new ArrayList<>();

        Member member = memberRepository.findByEmail(email);
        Cart cart = cartRepository.findByMemberId(member.getId());
        if(cart == null){
            return cartDetailDtoList; //없으면 빈 리스트 반환
        }

        // 상품 있으면 해당 상품 정보 조회
        cartDetailDtoList = cartItemRepository.findCartDetailDtoList(cart.getId());

        return cartDetailDtoList;
    }

    // 장바구니 수량을 업데이트할 경우
    // 로그인한 회원과 해당 장바구니 상품을 저장한 회원이 맞는지 체크
    @Transactional(readOnly = true)
    public boolean validateCartItem(Long cartItemId, String email){
        Member curMember = memberRepository.findByEmail(email);
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(EntityNotFoundException::new);
        Member savedMember = cartItem.getCart().getMember();

        if(!StringUtils.equals(curMember.getEmail(), savedMember.getEmail())){
            return false;
        }

        return true;
    }

    // 장바구니 수량 업데이트
    public void updateCartItemCount(Long cartItemId, int count){
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(EntityNotFoundException::new);
        cartItem.updateCount(count);
    }

    //장바구니 상품 삭제
    public void deleteCartItem(Long cartItemId){
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(EntityNotFoundException::new);
        cartItemRepository.delete(cartItem);
    }

    //장바구니 상품 주문
    public Long orderCartItem(List<CartOrderDto> cartOrderDtoList, String email){
        List<OrderDto> orderDtoList = new ArrayList<>();

        //장바구니 페이지에서 전달받은 주문 상품 번호를 이용하여 orderDto객체 생성
        for(CartOrderDto cartOrderDto : cartOrderDtoList){
            CartItem cartItem = cartItemRepository.findById(cartOrderDto.getCartItemId())
                    .orElseThrow(EntityNotFoundException::new);
            OrderDto orderDto = new OrderDto();
            orderDto.setItemId(cartItem.getItem().getId());
            orderDto.setCount(cartItem.getCount());
            orderDtoList.add(orderDto);
        }

        // 장바구니에 담은 상품을 주문
        Long orderId = orderService.orders(orderDtoList, email);

        // 주문한 상품들은 장바구니에서 삭제
        for(CartOrderDto cartOrderDto : cartOrderDtoList){
            CartItem cartItem = cartItemRepository.findById(cartOrderDto.getCartItemId())
                    .orElseThrow(EntityNotFoundException::new);
            cartItemRepository.delete(cartItem);
        }

        return orderId;
    }
}
