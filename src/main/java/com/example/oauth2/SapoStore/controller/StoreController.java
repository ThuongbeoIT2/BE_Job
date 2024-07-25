package com.example.oauth2.SapoStore.controller;


import com.cloudinary.Cloudinary;
import com.example.oauth2.SapoStore.exception.NotFoundObjectException;
import com.example.oauth2.SapoStore.model.Store;
import com.example.oauth2.SapoStore.model.StoreType;
import com.example.oauth2.SapoStore.page.SapoPageRequest;
import com.example.oauth2.SapoStore.payload.reponse.StoreResponse;
import com.example.oauth2.SapoStore.payload.request.StoreRequest;
import com.example.oauth2.SapoStore.repository.StoreTypeRepository;
import com.example.oauth2.SapoStore.service.iservice.IProductOfStoreService;
import com.example.oauth2.SapoStore.service.iservice.IStoreService;
import com.example.oauth2.globalContanst.GlobalConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;


@RestController
@RequestMapping("/store")
public class StoreController {
    @Autowired
    private IStoreService iStoreService;
    @Autowired
    private Cloudinary cloudinary;
    @Autowired
    private StoreTypeRepository storeTypeRepository;
    @Autowired
    private IProductOfStoreService iProductOfStoreService;

    @GetMapping(value = "/getall")
    ResponseEntity<Page<StoreResponse>> getAllStore(@RequestParam(defaultValue = "0") int page) {
        SapoPageRequest sapoPageRequest = new SapoPageRequest(GlobalConstant.Value.PAGELIMIT, page * GlobalConstant.Value.PAGELIMIT);
        Page<StoreResponse> storeResponses = iStoreService.findAll(sapoPageRequest);
        return ResponseEntity.ok(storeResponses);
    }

    @GetMapping(value = "/view/{storeCode}")
    ResponseEntity<StoreResponse> viewStore(@PathVariable UUID storeCode) {
        Optional<StoreResponse> storeResponses = iStoreService.findStoreByCode(storeCode);
        if (storeResponses.isEmpty()) {
            throw new NotFoundObjectException(GlobalConstant.ObjectClass.STORE, GlobalConstant.ErrorCode.MER404);
        }
        Store store = iStoreService.findStoreBystoreCode(storeCode).get();
        store.setView(store.getView() + 1);
        iStoreService.Save(store);
        return ResponseEntity.ok(storeResponses.get());
    }

    @PostMapping(value = "/register-store")
    ResponseEntity<String> registerStore(@RequestParam String storeName,
                                         @RequestParam String address,
                                         @RequestParam String phoneNumber,
                                         @RequestParam MultipartFile thumbnail,
                                         @RequestParam String description,
                                         @RequestParam MultipartFile eKyc,
                                         @RequestParam String storeType) {
        StoreRequest storeRequest = new StoreRequest();
        storeRequest.setStoreName(storeName);
        storeRequest.setAddress(address);
        storeRequest.setDescription(description);
        storeRequest.setPhoneNumber(phoneNumber);
        storeRequest.setStoreType(findStoretypeBySlug(storeType));
        if (thumbnail.isEmpty() || eKyc.isEmpty()) {
            throw new NotFoundObjectException(GlobalConstant.ObjectClass.STORE, GlobalConstant.ErrorCode.MER430);
        }
        Map<String, Object> uploadthumbnail = upload(thumbnail);
        storeRequest.setThumbnail(uploadthumbnail.get("secure_url").toString());
        Map<String, Object> uploadEKYC = upload(eKyc);
        storeRequest.setEKyc(uploadEKYC.get("secure_url").toString());
        iStoreService.insert(storeRequest);
        return ResponseEntity.ok(GlobalConstant.ResultResponse.SUCCESS);
    }

    @PostMapping(value = "/update/{storeCode}")
    ResponseEntity<String> updateStore(@PathVariable UUID storeCode,
                                       @RequestParam String storeName,
                                       @RequestParam String address,
                                       @RequestParam String description,
                                       @RequestParam String phoneNumber
    ) {
        Optional<Store> store = iStoreService.findStoreBystoreCode(storeCode);
        if (store.isEmpty()) {
            throw new NotFoundObjectException(GlobalConstant.ObjectClass.STORE, GlobalConstant.ErrorCode.MER404);
        }
        StoreRequest storeRequest = new StoreRequest();
        storeRequest.setStoreName(storeName);
        storeRequest.setAddress(address);
        storeRequest.setDescription(description);
        storeRequest.setPhoneNumber(phoneNumber);
        iStoreService.update(storeRequest, store.get());
        return ResponseEntity.ok(GlobalConstant.ResultResponse.SUCCESS);
    }

    @GetMapping(value = "/acpstore/{storeCode}")
    ResponseEntity<String> ACPStore(@PathVariable UUID storeCode) {
        Optional<Store> store = iStoreService.findStoreBystoreCode(storeCode);
        if (store.isEmpty()) {
            throw new NotFoundObjectException(GlobalConstant.ObjectClass.STORE, GlobalConstant.ErrorCode.MER404);
        }
        iStoreService.ACPStore(store.get());
        return ResponseEntity.ok(GlobalConstant.ResultResponse.SUCCESS);
    }

    @GetMapping(value = "/list-store/{slug}")
    ResponseEntity<Page<StoreResponse>> getStoreByType(@PathVariable String slug, @RequestParam(defaultValue = "0") int page) {
        SapoPageRequest sapoPageRequest = new SapoPageRequest(GlobalConstant.Value.PAGELIMIT, page * GlobalConstant.Value.PAGELIMIT);
        Page<StoreResponse> storeResponses = iStoreService.getStoreByType(slug, sapoPageRequest);
        return ResponseEntity.ok(storeResponses);
    }

    @GetMapping(value = "/search")
    ResponseEntity<Page<StoreResponse>> getStoreByKey(@RequestParam String key, @RequestParam(defaultValue = "0") int page) {
        SapoPageRequest sapoPageRequest = new SapoPageRequest(GlobalConstant.Value.PAGELIMIT, page * GlobalConstant.Value.PAGELIMIT);
        Page<StoreResponse> storeResponses = iStoreService.searchStoreByKey(key, sapoPageRequest);
        return ResponseEntity.ok(storeResponses);
    }

    public Map upload(MultipartFile file) {
        try {
            Map data = cloudinary.uploader().upload(file.getBytes(), Map.of());
            return data;
        } catch (IOException io) {
            throw new RuntimeException("Image upload fail");
        }
    }

    public StoreType findStoretypeBySlug(String slug) {
        StoreType storeType = storeTypeRepository.findBySlug(slug);
        if (storeType == null) {
            throw new NotFoundObjectException(GlobalConstant.ObjectClass.STORETYPE, GlobalConstant.ErrorCode.MER404);
        }
        return storeType;
    }


}
