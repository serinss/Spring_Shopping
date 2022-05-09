package com.shop.controller;

import com.shop.dto.OrderDto;
import com.shop.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.validation.Valid;
import java.security.Principal;
import java.util.List;

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
}
