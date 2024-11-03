package com.example.oauth2.SapoStore.controller;

import com.example.oauth2.SapoStore.exception.SlugConflictException;
import com.example.oauth2.SapoStore.model.OrderStatus;
import com.example.oauth2.SapoStore.repository.OrderStatusRepository;
import com.example.oauth2.globalContanst.GlobalConstant;
import com.example.oauth2.payload.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(value = "orderstatus")
public class OrderStatusController {
    @Autowired
    private OrderStatusRepository orderStatusRepository;
    @GetMapping(value = "get-all")
    ResponseEntity<ApiResponse> getAllOrderStatus(){
        List<OrderStatus> orderStatuses= orderStatusRepository.findAll();
        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse("OK", GlobalConstant.ResultResponse.SUCCESS,orderStatuses));
    }
    @PostMapping(value = "insert")
    ResponseEntity<ApiResponse> Insert(@RequestParam String status){
        Optional<OrderStatus> orderStatus = orderStatusRepository.findOrderStatusByString(status.trim());
        if (orderStatus.isPresent()){
            throw new SlugConflictException(GlobalConstant.ErrorCode.MER420,GlobalConstant.ResultResponse.FAILURE);
        }
        OrderStatus newObj = new OrderStatus();
        newObj.setStatus(status.trim());
        orderStatusRepository.save(newObj);
        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse("OK",GlobalConstant.ResultResponse.SUCCESS,""));
    }
}
