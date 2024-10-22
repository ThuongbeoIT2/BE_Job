package com.example.oauth2.SapoStore.controller;


import com.cloudinary.Cloudinary;
import com.example.oauth2.SapoStore.exception.NotFoundObjectException;
import com.example.oauth2.SapoStore.exception.VerifyStoreManagerException;
import com.example.oauth2.SapoStore.model.Product;
import com.example.oauth2.SapoStore.model.ProductOfStore;
import com.example.oauth2.SapoStore.model.Store;
import com.example.oauth2.SapoStore.model.StoreType;
import com.example.oauth2.SapoStore.page.SapoPageRequest;
import com.example.oauth2.SapoStore.payload.reponse.ProductOfStoreResponse;

import com.example.oauth2.SapoStore.payload.reponse.StoreResponse;
import com.example.oauth2.SapoStore.payload.request.ProductOfStoreRequest;
import com.example.oauth2.SapoStore.payload.request.StoreRequest;
import com.example.oauth2.SapoStore.repository.ProductRepository;
import com.example.oauth2.SapoStore.repository.StoreTypeRepository;
import com.example.oauth2.SapoStore.service.iservice.IProductOfStoreService;
import com.example.oauth2.SapoStore.service.iservice.IStoreService;
import com.example.oauth2.globalContanst.GlobalConstant;
import com.example.oauth2.payload.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
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
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private ProductRepository productRepository;

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
    ResponseEntity<ApiResponse> registerStore(@RequestParam String storeName,
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
        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse("OK",GlobalConstant.ResultResponse.SUCCESS,""));
    }

    @PostMapping(value = "/update/{storeCode}")
    ResponseEntity<ApiResponse> updateStore(@PathVariable UUID storeCode,
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
        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse("OK",GlobalConstant.ResultResponse.SUCCESS,""));
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


    @PostMapping(value = "/login-store")
    ResponseEntity<String> loginStore(
            @RequestParam UUID storeCode,
            @RequestParam String password,
            HttpSession httpSession){

        String email_manager = getEmailAuthentication();
        Store store = isManagerStore(storeCode, email_manager);
        boolean login =passwordEncoder.matches(store.getPassword(), password);
        if (!login){
            return ResponseEntity.badRequest().body(GlobalConstant.ResultResponse.FAILURE);
        }else {
            httpSession.setAttribute("verifyStore", storeCode);
            return ResponseEntity.ok(GlobalConstant.ResultResponse.SUCCESS);
        }
    }
    @GetMapping(value = "/manager-store/my-store")
    ResponseEntity<StoreResponse> myStore(HttpSession httpSession){
        String storeCode = verifySession(httpSession);
        String email_manager = getEmailAuthentication();
        UUID UUIDstoreCode = UUID.fromString(storeCode);
        Store store = isManagerStore(UUIDstoreCode,email_manager);
        StoreResponse storeResponse = StoreResponse.cloneFromStore(store);
         return ResponseEntity.ok(storeResponse);
    }
    @GetMapping(value = "/manager-store/my-store/product")
    ResponseEntity<Page<ProductOfStoreResponse>> getListProductOfMyStore(HttpSession httpSession,
                                                                  @RequestParam(defaultValue = "0") int page){
        String storeCode = verifySession(httpSession);
        UUID UUIDstoreCode = UUID.fromString(storeCode);
        SapoPageRequest sapoPageRequest = new SapoPageRequest(GlobalConstant.Value.PAGELIMIT, page * GlobalConstant.Value.PAGELIMIT);
        Page<ProductOfStoreResponse> productResponses = iProductOfStoreService.findProductOfStoreByStore(UUIDstoreCode,sapoPageRequest);
        return ResponseEntity.ok(productResponses);
    }
    @GetMapping(value = "/manager-store/my-store/product/search")
    ResponseEntity<Page<ProductOfStoreResponse>> SearchListProductOfMyStore(HttpSession httpSession,
                                                                         @RequestParam String key,
                                                                         @RequestParam(defaultValue = "0") int page){
        String storeCode = verifySession(httpSession);
        UUID UUIDstoreCode = UUID.fromString(storeCode);
        SapoPageRequest sapoPageRequest = new SapoPageRequest(GlobalConstant.Value.PAGELIMIT, page * GlobalConstant.Value.PAGELIMIT);
        Page<ProductOfStoreResponse> productResponses = iProductOfStoreService.searchProductOfStoreByKey(key,UUIDstoreCode,sapoPageRequest);
        return ResponseEntity.ok(productResponses);
    }

    @GetMapping(value = "/manager-store/my-store/product/{productOfStoreid}")
    ResponseEntity<ProductOfStoreResponse> getProductOfMyStore(HttpSession httpSession,
                                                                     @PathVariable Long productOfStoreid){
        String storeCode = verifySession(httpSession);
        UUID UUIDstoreCode = UUID.fromString(storeCode);
        Optional<ProductOfStoreResponse> productOfStoreResponse = iProductOfStoreService.getProductOfStoreById(productOfStoreid);
        if (productOfStoreResponse.isEmpty()){
            throw new NotFoundObjectException(GlobalConstant.ObjectClass.PRODUCTOS,GlobalConstant.ErrorCode.MER404);
        }
        if (productOfStoreResponse.get().getStoreCode() != UUIDstoreCode){
            throw  new VerifyStoreManagerException("Sản phẩm không thuộc quyền sở hữu của bạn");
        }
        return ResponseEntity.ok(productOfStoreResponse.get());
    }
    @PostMapping(value = "/manager-store/my-store/product/insert")
    ResponseEntity<String> InsertProductOfMyStore(HttpSession httpSession,
                                                  @RequestParam Long priceI,
                                                  @RequestParam Long priceO,
                                                  @RequestParam double discount,
                                                  @RequestParam String slugProduct
                                                              ){
        String storeCode = verifySession(httpSession);
        UUID UUIDstoreCode = UUID.fromString(storeCode);
        String email = getEmailAuthentication();
        Store store= isManagerStore(UUIDstoreCode,email);
        Optional<Product> product = productRepository.findProductBySlug(slugProduct);
        if (product.isEmpty()){
            throw new NotFoundObjectException(GlobalConstant.ObjectClass.PRODUCT,GlobalConstant.ErrorCode.MER404);
        }
        Optional<ProductOfStore> productOfStore = isExistProductOfStore(UUIDstoreCode,slugProduct);
        if (productOfStore.isPresent()){
            return ResponseEntity.badRequest().body(GlobalConstant.ResultResponse.FAILURE);
        }
        ProductOfStoreRequest productOfStoreRequest = new ProductOfStoreRequest();
        productOfStoreRequest.setPriceI(priceI);
        productOfStoreRequest.setPriceO(priceO);
        productOfStoreRequest.setDiscount(discount);
        productOfStoreRequest.setProduct(product.get());
        productOfStoreRequest.setStore(store);
        iProductOfStoreService.insert(productOfStoreRequest);
        return ResponseEntity.ok(GlobalConstant.ResultResponse.SUCCESS);
    }
    @PostMapping(value = "/manager-store/my-store/product/update/{id}")
    ResponseEntity<String> UpdateProductOfMyStore(HttpSession httpSession,
                                                  @RequestParam Long priceI,
                                                  @RequestParam Long priceO,
                                                  @RequestParam double discount,
                                                  @PathVariable Long id
    ){
        String storeCode = verifySession(httpSession);
        UUID UUIDstoreCode = UUID.fromString(storeCode);
        String email = getEmailAuthentication();
        Store store= isManagerStore(UUIDstoreCode,email);;
        Optional<ProductOfStore> productOfStore = iProductOfStoreService.ProductOfStoreById(id);
        if (productOfStore.isEmpty()){
            return ResponseEntity.badRequest().body(GlobalConstant.ResultResponse.FAILURE);
        }
        ProductOfStoreRequest productOfStoreRequest = new ProductOfStoreRequest();
        productOfStoreRequest.setPriceI(priceI);
        productOfStoreRequest.setPriceO(priceO);
        productOfStoreRequest.setDiscount(discount);
        productOfStoreRequest.setStore(store);
        productOfStoreRequest.setProduct(productOfStore.get().getProduct());
        iProductOfStoreService.update(productOfStoreRequest,productOfStore.get());
        return ResponseEntity.ok(GlobalConstant.ResultResponse.SUCCESS);
    }
    @GetMapping(value = "/manager-store/my-store/product/delete/{id}")
    ResponseEntity<String> DeleteProductOfMyStore(HttpSession httpSession,
                                                  @PathVariable Long id
    ){
        String storeCode = verifySession(httpSession);
        UUID UUIDstoreCode = UUID.fromString(storeCode);
        String email = getEmailAuthentication();
        isManagerStore(UUIDstoreCode,email);
        Optional<ProductOfStore> productOfStore = iProductOfStoreService.ProductOfStoreById(id);
        if (productOfStore.isEmpty()){
            return ResponseEntity.badRequest().body(GlobalConstant.ResultResponse.FAILURE);
        }
        iProductOfStoreService.softDelete(productOfStore.get());
        return ResponseEntity.ok(GlobalConstant.ResultResponse.SUCCESS);
    }
    private Optional<ProductOfStore> isExistProductOfStore(UUID storeCode,String slugProduct) {
        return iProductOfStoreService.isExistProductOfStore(slugProduct,storeCode);
    }


    private String verifySession(HttpSession httpSession) {
        return httpSession.getAttribute("verifyStore").toString();
    }

    private Store isManagerStore(UUID storeCode, String emailManager) {
        Optional<Store> store= iStoreService.findStoreBystoreCode(storeCode);
        if (store.isEmpty()){
            throw new NotFoundObjectException(GlobalConstant.ObjectClass.STORE,GlobalConstant.ErrorCode.MER404);
        }
        if (!store.get().getEmail_manager().equals(emailManager)){
            throw  new VerifyStoreManagerException("Store không thuộc quyền sở hữu của bạn");
        }
       return store.get();

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
    public String getEmailAuthentication(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getName();
    }

}
