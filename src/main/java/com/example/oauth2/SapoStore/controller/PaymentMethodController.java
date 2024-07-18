package com.example.oauth2.SapoStore.controller;

import com.example.oauth2.SapoStore.exception.NotFoundObjectException;
import com.example.oauth2.SapoStore.exception.SlugConflictException;
import com.example.oauth2.SapoStore.model.PaymentMethod;
import com.example.oauth2.SapoStore.payload.reponse.PaymentMethodResponse;
import com.example.oauth2.SapoStore.payload.request.PaymentMethodRequest;
import com.example.oauth2.SapoStore.repository.PaymentMethodRepository;
import com.example.oauth2.globalContanst.GlobalConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/paymentmethod")
public class PaymentMethodController {
    @Autowired
    private PaymentMethodRepository paymentMethodRepository;
    @PostMapping("/insert")
    ResponseEntity<String> InsertPaymentMethod(@RequestBody PaymentMethodRequest paymentMethodRequest){
        if (findPaymentmethodBySlug(paymentMethodRequest.getSlug())==null){
            PaymentMethod paymentMethod= new PaymentMethod();
            paymentMethod.setSlug(paymentMethodRequest.getSlug());
            paymentMethod.setMethod(paymentMethodRequest.getPaymentmethod());
            paymentMethod.setDescription(paymentMethodRequest.getDescription());
            paymentMethod.setBillPayments(new HashSet<>());
            paymentMethodRepository.save(paymentMethod);
            return ResponseEntity.ok("Insert Payment method success");
        }
        return ResponseEntity.badRequest().body("Slug Already exist");
    }
    @GetMapping(value = "getAll")
    ResponseEntity<List<PaymentMethodResponse>> getAllpaymentMethod(){
        return ResponseEntity.ok(paymentMethodRepository.findAll()
                        .stream()
                        .map(paymentMethod -> {
                            PaymentMethodResponse paymentMethodResponse = PaymentMethodResponse.cloneFromPaymentMethod(paymentMethod);
                            return paymentMethodResponse;
                        }).collect(Collectors.toList())
                );
    }
    @PostMapping(value = "/update/{id}")
    ResponseEntity<String> updatePaymentMethod(@PathVariable int id, @RequestBody PaymentMethodRequest paymentMethodRequest){
        Optional<PaymentMethod> paymentMethod = findPaymentMethodById(id);
        if (paymentMethod.isEmpty()){
            throw  new NotFoundObjectException(GlobalConstant.ObjectClass.PAYMENTMETHOD,GlobalConstant.ErrorCode.MER404);
        }
        if (findPaymentmethodBySlug(paymentMethodRequest.getSlug())!=null && paymentMethod.get().getId()!=id){
            throw new SlugConflictException(GlobalConstant.ObjectClass.PAYMENTMETHOD,GlobalConstant.ErrorCode.MER420);
        }
        paymentMethod.get().setSlug(paymentMethodRequest.getSlug());
        paymentMethod.get().setDescription(paymentMethodRequest.getDescription());
        paymentMethod.get().setMethod(paymentMethodRequest.getPaymentmethod());
        paymentMethodRepository.save(paymentMethod.get());
        return ResponseEntity.ok("update success");
    }
    @GetMapping(value = "/delete/{id}")
    ResponseEntity<String> deletePaymentMethod(@PathVariable int id){
        Optional<PaymentMethod> paymentMethod = findPaymentMethodById(id);
        if (paymentMethod.isEmpty()){
            throw  new NotFoundObjectException(GlobalConstant.ObjectClass.PAYMENTMETHOD,GlobalConstant.ErrorCode.MER404);
        }
        paymentMethodRepository.delete(paymentMethod.get());
        return ResponseEntity.ok("delete success");
    }
    public PaymentMethod findPaymentmethodBySlug(String slug){
        PaymentMethod paymentMethod = paymentMethodRepository.findBySlug(slug);
        return paymentMethod;
    }
    public Optional<PaymentMethod> findPaymentMethodById(int id){
        return paymentMethodRepository.findById(id);
    }
}
