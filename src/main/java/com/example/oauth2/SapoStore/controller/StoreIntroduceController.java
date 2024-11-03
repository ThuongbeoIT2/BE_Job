package com.example.oauth2.SapoStore.controller;

import com.example.oauth2.SapoStore.exception.NotFoundObjectException;
import com.example.oauth2.SapoStore.model.Store;
import com.example.oauth2.SapoStore.model.StoreIntroduce;
import com.example.oauth2.SapoStore.repository.IntroduceRepository;
import com.example.oauth2.SapoStore.service.iservice.IStoreService;
import com.example.oauth2.globalContanst.GlobalConstant;
import com.example.oauth2.payload.ApiResponse;
import com.example.oauth2.util.ProcessUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping(value = "introduce")
public class StoreIntroduceController {
    @Autowired
    private IntroduceRepository introduceRepository;
    @Autowired
    private IStoreService iStoreService;
    @PostMapping(value = "/insert/{storeCode}")
    ResponseEntity<ApiResponse> insertStoreIntroduce(@PathVariable String storeCode,
                                                     @RequestParam String title,
                                                     @RequestParam String description,
                                                     @RequestParam String link_facebook,
                                                     @RequestParam String link_instagram,
                                                     @RequestParam String link_zalo,
                                                     @RequestParam String hotline){
        Optional<Store> store = iStoreService.findStoreBystoreCode(storeCode);
        if (store.isEmpty()){
            throw new NotFoundObjectException(GlobalConstant.ObjectClass.STORE,GlobalConstant.ErrorCode.MER404);
        }
        if (store.get().getStoreIntroduce()== null){
            StoreIntroduce storeIntroduce= new StoreIntroduce();
            storeIntroduce.setTitle(title);
            storeIntroduce.setDescription(description);
            storeIntroduce.setLink_facebook(link_facebook);
            storeIntroduce.setLink_instagram(link_instagram);
            storeIntroduce.setLink_zalo(link_zalo);
            storeIntroduce.setHotline(hotline == null ? store.get().getPhoneNumber() : hotline);
            introduceRepository.save(storeIntroduce);
            store.get().setStoreIntroduce(storeIntroduce);
            store.get().setUpdatedAt(ProcessUtils.getCurrentDay());
            iStoreService.Save(store.get());
            return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse("OK",GlobalConstant.ResultResponse.SUCCESS,""));
        }
        return ResponseEntity.badRequest().body(new ApiResponse("FAILED",GlobalConstant.ResultResponse.FAILURE,""));
    }
    @PostMapping(value = "/update/{storeCode}")
    ResponseEntity<ApiResponse> updateStoreIntroduce(@PathVariable String storeCode,
                                                @RequestParam String title,
                                                @RequestParam String description,
                                                @RequestParam String link_facebook,
                                                @RequestParam String link_instagram,
                                                @RequestParam String link_zalo,
                                                @RequestParam String hotline){
        Optional<Store> store = iStoreService.findStoreBystoreCode(storeCode);
        if (store.isEmpty()){
            throw new NotFoundObjectException(GlobalConstant.ObjectClass.STORE,GlobalConstant.ErrorCode.MER404);
        }
        if (store.get().getStoreIntroduce()!= null){
            store.get().getStoreIntroduce().setTitle(title);
            store.get().getStoreIntroduce().setDescription(description);
            store.get().getStoreIntroduce().setLink_facebook(link_facebook);
            store.get().getStoreIntroduce().setLink_instagram(link_instagram);
            store.get().getStoreIntroduce().setLink_zalo(link_zalo);
            store.get().getStoreIntroduce().setHotline(hotline == null ? store.get().getStoreIntroduce().getHotline() : hotline);
            introduceRepository.save(store.get().getStoreIntroduce());
            store.get().setStoreIntroduce(store.get().getStoreIntroduce());
            store.get().setUpdatedAt(ProcessUtils.getCurrentDay());
            iStoreService.Save(store.get());
            return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse("OK",GlobalConstant.ResultResponse.SUCCESS,""));
        }
        return ResponseEntity.badRequest().body(new ApiResponse("FAILED",GlobalConstant.ResultResponse.FAILURE,""));
    }
    @GetMapping(value = "/view/{storeCode}")
    ResponseEntity<StoreIntroduce> viewIntroduce(@PathVariable String storeCode){
        Optional<Store> store = iStoreService.findStoreBystoreCode(storeCode);
        if (store.isEmpty()){
            throw new NotFoundObjectException(GlobalConstant.ObjectClass.STORE,GlobalConstant.ErrorCode.MER404);
        }
        if (store.get().getStoreIntroduce()== null){
           throw new NotFoundObjectException(GlobalConstant.ObjectClass.INTRODUCE,GlobalConstant.ErrorCode.MER404);
        }
        return ResponseEntity.ok(store.get().getStoreIntroduce());
    }
}
