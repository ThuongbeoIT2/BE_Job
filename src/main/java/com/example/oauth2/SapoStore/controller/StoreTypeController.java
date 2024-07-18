package com.example.oauth2.SapoStore.controller;

import com.cloudinary.Cloudinary;
import com.example.oauth2.SapoStore.exception.NotFoundObjectException;
import com.example.oauth2.SapoStore.exception.SlugConflictException;
import com.example.oauth2.SapoStore.model.PaymentMethod;
import com.example.oauth2.SapoStore.model.StoreType;
import com.example.oauth2.SapoStore.payload.reponse.PaymentMethodResponse;
import com.example.oauth2.SapoStore.payload.reponse.StoreTypeResponse;
import com.example.oauth2.SapoStore.payload.request.PaymentMethodRequest;
import com.example.oauth2.SapoStore.repository.StoreTypeRepository;
import com.example.oauth2.config.ConfigCloudinary;
import com.example.oauth2.globalContanst.GlobalConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/storetype")
public class StoreTypeController {
    @Autowired
    private Cloudinary cloudinary;
    @Autowired
    private StoreTypeRepository storeTypeRepository;
    @PostMapping("/insert")
    ResponseEntity<String> InsertStoreType(@RequestParam String typeName,
                                           @RequestParam String slug,
                                           @RequestParam String description,
                                           @RequestParam MultipartFile thumnailimg){
        if (findStoreTypeBySlug(slug)==null){
            StoreType storeType= new StoreType();
            storeType.setSlug(slug);
            storeType.setTypeName(typeName);
            storeType.setDescription(description);
            Map<String, Object> uploadResult = upload(thumnailimg);
            storeType.setThumnail(uploadResult.get("secure_url").toString());
            storeTypeRepository.save(storeType);
            return ResponseEntity.ok("Insert Payment method success");
        }
        return ResponseEntity.badRequest().body("Slug Already exist");
    }
    @GetMapping(value = "getAll")
    ResponseEntity<List<StoreTypeResponse>> getAllStoreType(){
        return ResponseEntity.ok(storeTypeRepository.findAll()
                .stream()
                .map(storeType -> {
                    StoreTypeResponse storeTypeResponse = StoreTypeResponse.cloneFromStoreType(storeType);
                    return storeTypeResponse;
                }).collect(Collectors.toList())
        );
    }
    @PostMapping(value = "/update/{id}")
    ResponseEntity<String> updateStoretype(@PathVariable int id,
                                               @RequestParam String typeName,
                                               @RequestParam String slug,
                                               @RequestParam String description,
                                               @RequestParam MultipartFile thumnailimg
                                               ){
        Optional<StoreType> storeType = findStoreTypeById(id);
        if (storeType.isEmpty()){
            throw  new NotFoundObjectException(GlobalConstant.ObjectClass.STORETYPE,GlobalConstant.ErrorCode.MER404);
        }
        if (findStoreTypeBySlug(slug)!=null && storeType.get().getId()!=id){
            throw new SlugConflictException(GlobalConstant.ObjectClass.STORETYPE,GlobalConstant.ErrorCode.MER420);
        }
        if (!thumnailimg.isEmpty()){
            Map<String, Object> uploadResult = upload(thumnailimg);
            storeType.get().setThumnail(uploadResult.get("secure_url").toString());
        }
        storeType.get().setSlug(slug);
        storeType.get().setDescription(description);
        storeType.get().setTypeName(typeName);
        storeTypeRepository.save(storeType.get());
        return ResponseEntity.ok("update success");
    }
    @GetMapping(value = "/delete/{id}")
    ResponseEntity<String> deleteStoreType(@PathVariable int id){
        Optional<StoreType> storeType = findStoreTypeById(id);
        if (storeType.isEmpty()){
            throw  new NotFoundObjectException(GlobalConstant.ObjectClass.STORETYPE,GlobalConstant.ErrorCode.MER404);
        }
        storeTypeRepository.delete(storeType.get());
        return ResponseEntity.ok("delete success");
    }
    public StoreType findStoreTypeBySlug(String slug){
        StoreType storeType = storeTypeRepository.findBySlug(slug);
        return storeType;
    }
    public Optional<StoreType> findStoreTypeById(int id){
        return storeTypeRepository.findById(id);
    }

    public  Map upload(MultipartFile file)  {
        try{
            Map data = cloudinary.uploader().upload(file.getBytes(), Map.of());
            return data;
        }catch (IOException io){
            throw new RuntimeException("Image upload fail");
        }
    }
}
