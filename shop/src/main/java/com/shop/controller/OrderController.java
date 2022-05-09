package com.shop.controller;

import com.shop.dto.OrderDto;
import com.shop.dto.OrderHistDto;
import com.shop.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;
import java.util.List;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping(value = "/order")
    public @ResponseBody ResponseEntity order(@RequestBody @Valid OrderDto orderDto, BindingResult bindingResult, Principal principal){
    // 스프링 비동기 처리
        //@RequestBody : HTTP 요청의 본문 body에 담긴 내용을 자바 객체로 전달
        //@ResponseBody : 자바 객체를 HTTP요청의 body로 전달

        //orderDto 객체에 데이터 바인딩 에러가 있는지 검사
        if(bindingResult.hasErrors()){
            StringBuilder sb = new StringBuilder();
            List<FieldError> fieldErrors = bindingResult.getFieldErrors();
            for(FieldError fieldError:fieldErrors){
                sb.append(fieldError.getDefaultMessage());
            }
            return new ResponseEntity<String>(sb.toString(), HttpStatus.BAD_REQUEST);
        }

        // 현재 로그인한 유저의 정보는 @Controller 가 선언된 클래스에서 메서드 인자로 principal 객체를 넘겨줄 경우 접근 가능
        String email = principal.getName();
        Long orderId;

        try{
            // 뷰단에서 넘어오는 주문 정보와 회원 이메일 정보를 주문 로직에 전달하여 실행행
           orderId = orderService.order(orderDto, email);
        } catch (Exception e){
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }

        // 생성된 주문 번호와 성공 HTTP응답코드 반환
        return new ResponseEntity<Long>(orderId, HttpStatus.OK);
    }

    //주문한 목록 조회
    @GetMapping(value = {"/orders", "/orders/{page}"})
    public String orderHist(@PathVariable("page")Optional<Integer> page, Principal principal, Model model){
        Pageable pageable = PageRequest.of(page.isPresent()? page.get() : 0,4);

        Page<OrderHistDto> orderHistDtoList = orderService.getOrderList(principal.getName(), pageable);

        model.addAttribute("orders", orderHistDtoList);
        model.addAttribute("page", pageable.getPageNumber());
        model.addAttribute("maxPage", 5);

        return "order/orderHist";
    }

    //주문 취소
    @PostMapping("/order/{orderId}/cancel")
    public @ResponseBody ResponseEntity cancelOrder(@PathVariable("orderId") Long orderId, Principal principal){

        // 다른 사람이 주문을 취소하지 못하도록 권한 검사
        if(!orderService.validateOrder(orderId, principal.getName())){
            return new ResponseEntity<String>("주문 취소 권한이 없습니다.", HttpStatus.FORBIDDEN);
        }

        orderService.cancelOrder(orderId);
        return new ResponseEntity<Long>(orderId, HttpStatus.OK);
    }
}
