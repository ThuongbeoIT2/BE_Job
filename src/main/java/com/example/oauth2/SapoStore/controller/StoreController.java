package com.example.oauth2.SapoStore.controller;


import com.cloudinary.Cloudinary;
import com.example.oauth2.SapoStore.exception.NotFoundObjectException;
import com.example.oauth2.SapoStore.exception.VerifyStoreManagerException;
import com.example.oauth2.SapoStore.model.*;
import com.example.oauth2.SapoStore.page.SapoPageRequest;
import com.example.oauth2.SapoStore.payload.reponse.ProductOSImageResponse;
import com.example.oauth2.SapoStore.payload.reponse.ProductOfStoreResponse;

import com.example.oauth2.SapoStore.payload.reponse.StoreResponse;
import com.example.oauth2.SapoStore.payload.request.ProductOSImageRequest;
import com.example.oauth2.SapoStore.payload.request.ProductOfStoreRequest;
import com.example.oauth2.SapoStore.payload.request.StoreRequest;
import com.example.oauth2.SapoStore.repository.ProductRepository;
import com.example.oauth2.SapoStore.repository.StoreTypeRepository;
import com.example.oauth2.SapoStore.service.iservice.IProductOSImageService;
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
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;


@RestController

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
    @Autowired
    private IProductOSImageService iProductOSImageService;

    @GetMapping(value = "/store/getall")
    ResponseEntity<Page<StoreResponse>> getAllStore(@RequestParam(defaultValue = "0") int page) {
        SapoPageRequest sapoPageRequest = new SapoPageRequest(GlobalConstant.Value.PAGELIMIT, page * GlobalConstant.Value.PAGELIMIT);
        Page<StoreResponse> storeResponses = iStoreService.findAll(sapoPageRequest);
        return ResponseEntity.ok(storeResponses);
    }

    @GetMapping(value = "/store/view/{storeCode}")
    ResponseEntity<StoreResponse> viewStore(@PathVariable String storeCode) {
        Optional<StoreResponse> storeResponses = iStoreService.findStoreByCode(storeCode);
        if (storeResponses.isEmpty()) {
            throw new NotFoundObjectException(GlobalConstant.ObjectClass.STORE, GlobalConstant.ErrorCode.MER404);
        }
        Store store = iStoreService.findStoreBystoreCode(storeCode).get();
        store.setView(store.getView() + 1);
        iStoreService.Save(store);
        return ResponseEntity.ok(storeResponses.get());
    }

    @PostMapping(value = "/store/register-store")
    ResponseEntity<ApiResponse> registerStore(@RequestParam String storeName,
                                              @RequestParam String address,
                                              @RequestParam String phoneNumber,
                                              @RequestParam MultipartFile thumbnail,
                                              @RequestParam String VNPayAccountLink,
                                              @RequestParam String description,
                                              @RequestParam MultipartFile eKyc_01,
                                              @RequestParam MultipartFile eKyc_02,
                                              @RequestParam String storeType) {
        StoreRequest storeRequest = new StoreRequest();
        storeRequest.setStoreName(storeName);
        storeRequest.setAddress(address);
        storeRequest.setDescription(description);
        storeRequest.setVNPayAccountLink(VNPayAccountLink.trim());
        storeRequest.setPhoneNumber(phoneNumber);
        storeRequest.setStoreType(findStoretypeBySlug(storeType));
        if (thumbnail.isEmpty() || eKyc_01.isEmpty() || eKyc_02.isEmpty()) {
            throw new NotFoundObjectException(GlobalConstant.ObjectClass.STORE, GlobalConstant.ErrorCode.MER430);
        }
        Map<String, Object> uploadthumbnail = upload(thumbnail);
        storeRequest.setThumbnail(uploadthumbnail.get("secure_url").toString());
        Map<String, Object> uploadEKYC_01 = upload(eKyc_01);
        storeRequest.setEKyc_01(uploadEKYC_01.get("secure_url").toString());
        Map<String, Object> uploadEKYC_02 = upload(eKyc_02);
        storeRequest.setEKyc_02(uploadEKYC_02.get("secure_url").toString());
        iStoreService.insert(storeRequest);
        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse("OK",GlobalConstant.ResultResponse.SUCCESS,""));
    }

    @PostMapping(value = "/store/update/{storeCode}")
    ResponseEntity<ApiResponse> updateStore(@PathVariable String storeCode,
                                       @RequestParam String storeName,
                                       @RequestParam String address,
                                       @RequestParam String VNPayAccountLink,
                                       @RequestParam String description,
                                       @RequestParam String phoneNumber
    ) {
        Optional<Store> store = iStoreService.findStoreBystoreCode(storeCode);
        if (store.isEmpty()) {
            throw new NotFoundObjectException(GlobalConstant.ObjectClass.STORE, GlobalConstant.ErrorCode.MER404);
        }
        StoreRequest storeRequest = new StoreRequest();
        storeRequest.setStoreName(storeName);
        storeRequest.setVNPayAccountLink(VNPayAccountLink.trim());
        storeRequest.setAddress(address);
        storeRequest.setDescription(description);
        storeRequest.setPhoneNumber(phoneNumber);
        iStoreService.update(storeRequest, store.get());
        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse("OK",GlobalConstant.ResultResponse.SUCCESS,""));
    }

    @PostMapping(value = "/store/warningstore/{storeCode}")
    ResponseEntity<String> WarningStore(@PathVariable String storeCode,
                                        @RequestParam String email,
                                        @RequestParam String mesage) {
        Optional<Store> store = iStoreService.findStoreBystoreCode(storeCode);
        if (store.isEmpty()) {
            throw new NotFoundObjectException(GlobalConstant.ObjectClass.STORE, GlobalConstant.ErrorCode.MER404);
        }
        isManagerStore(storeCode,email);
        iStoreService.WarningStore(email,mesage);
        return ResponseEntity.ok(GlobalConstant.ResultResponse.SUCCESS);
    }

    @GetMapping(value = "/store/list-store/{slug}")
    ResponseEntity<Page<StoreResponse>> getStoreByType(@PathVariable String slug, @RequestParam(defaultValue = "0") int page) {
        SapoPageRequest sapoPageRequest = new SapoPageRequest(GlobalConstant.Value.PAGELIMIT, page * GlobalConstant.Value.PAGELIMIT);
        Page<StoreResponse> storeResponses = iStoreService.getStoreByType(slug, sapoPageRequest);
        return ResponseEntity.ok(storeResponses);
    }

    @GetMapping(value = "/store/search")
    ResponseEntity<Page<StoreResponse>> getStoreByKey(@RequestParam String key, @RequestParam(defaultValue = "0") int page) {
        SapoPageRequest sapoPageRequest = new SapoPageRequest(GlobalConstant.Value.PAGELIMIT, page * GlobalConstant.Value.PAGELIMIT);
        Page<StoreResponse> storeResponses = iStoreService.searchStoreByKey(key, sapoPageRequest);
        return ResponseEntity.ok(storeResponses);
    }


    @PostMapping(value = "/store/login-store")
    ResponseEntity<ApiResponse> loginStore(
            @RequestParam String storeCode,
            @RequestParam String password,
            HttpSession httpSession){

        String email_manager = getEmailAuthentication();
        Store store = isManagerStore(storeCode, email_manager);
        boolean login =passwordEncoder.matches(password,store.getPassword());
        if (!login){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse("FAILD",GlobalConstant.ResultResponse.FAILURE,""));
        }else {
            httpSession.setAttribute("verifyStore", storeCode);
            return ResponseEntity.ok(new ApiResponse("OK",GlobalConstant.ResultResponse.SUCCESS,""));
        }
    }
    @GetMapping(value = "/store/manager-store/my-store")
    ResponseEntity<StoreResponse> myStore(){
        Optional<StoreResponse> store = iStoreService.getStoreByEmailManager(getEmailAuthentication());
        return store.map(ResponseEntity::ok).orElse(null);
    }
    @PostMapping(value = "/productOS/product")
    ResponseEntity<Page<ProductOfStoreResponse>> getListProductOfMyStore(@RequestParam String storeCode,
                                                                  @RequestParam(defaultValue = "0") int page){


        SapoPageRequest sapoPageRequest = new SapoPageRequest(GlobalConstant.Value.PAGELIMIT, page * GlobalConstant.Value.PAGELIMIT);
        Page<ProductOfStoreResponse> productResponses = iProductOfStoreService.findProductOfStoreByStore(storeCode,sapoPageRequest);
        return ResponseEntity.ok(productResponses);
    }
    @PostMapping(value = "/productOS/search")
    ResponseEntity<Page<ProductOfStoreResponse>> SearchListProductOfMyStore(@RequestParam String storeCode,
                                                                         @RequestParam String key,
                                                                         @RequestParam(defaultValue = "0") int page){


        SapoPageRequest sapoPageRequest = new SapoPageRequest(GlobalConstant.Value.PAGELIMIT, page * GlobalConstant.Value.PAGELIMIT);
        Page<ProductOfStoreResponse> productResponses = iProductOfStoreService.searchProductOfStoreByKey(key,storeCode,sapoPageRequest);
        return ResponseEntity.ok(productResponses);
    }
    @PostMapping(value = "/productOS/getPOSBySlug")
    ResponseEntity<Page<ProductOfStoreResponse>> getListProductOfStoreBySlug(
                                                                            @RequestParam String slug,
                                                                            @RequestParam(defaultValue = "0") int page){


        SapoPageRequest sapoPageRequest = new SapoPageRequest(GlobalConstant.Value.PAGELIMIT, page * GlobalConstant.Value.PAGELIMIT);
        Page<ProductOfStoreResponse> productResponses = iProductOfStoreService.getListProductOfStoreBySlug(slug,sapoPageRequest);
        return ResponseEntity.ok(productResponses);
    }
    @PostMapping(value = "/productOS/{productOfStoreid}")
    ResponseEntity<ProductOfStoreResponse> getProductOfMyStore( @RequestParam String storeCode,
                                                                     @PathVariable Long productOfStoreid){

        Optional<ProductOfStoreResponse> productOfStoreResponse = iProductOfStoreService.getProductOfStoreById(productOfStoreid);
        if (productOfStoreResponse.isEmpty()){
            throw new NotFoundObjectException(GlobalConstant.ObjectClass.PRODUCTOS,GlobalConstant.ErrorCode.MER404);
        }
        if (!productOfStoreResponse.get().getStoreCode().equalsIgnoreCase(storeCode) ){
            throw  new VerifyStoreManagerException("Sản phẩm không thuộc quyền sở hữu của bạn");
        }
        return ResponseEntity.ok(productOfStoreResponse.get());
    }
    @PostMapping(value = "/productOS/insert")
    ResponseEntity<ApiResponse> InsertProductOfMyStore(@RequestParam String storeCode,
                                                  @RequestParam Long priceI,
                                                  @RequestParam Long priceO,
                                                  @RequestParam int quantity,
                                                  @RequestParam double discount,
                                                       @RequestParam String description,
                                                  @RequestParam String slugProduct
                                                              ){


        String email = getEmailAuthentication();
        Store store= isManagerStore(storeCode,email);
        Optional<Product> product = productRepository.findProductBySlug(slugProduct);
        if (product.isEmpty()){
            throw new NotFoundObjectException(GlobalConstant.ObjectClass.PRODUCT,GlobalConstant.ErrorCode.MER404);
        }
        Optional<ProductOfStore> productOfStore = isExistProductOfStore(storeCode,slugProduct);
        if (productOfStore.isPresent()){
            return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse("FAILD",GlobalConstant.ResultResponse.FAILURE,"ProductOS is exist"));
        }
        ProductOfStoreRequest productOfStoreRequest = new ProductOfStoreRequest();
        productOfStoreRequest.setDescription(description);
        productOfStoreRequest.setPriceI(priceI);
        productOfStoreRequest.setPriceO(priceO);
        productOfStoreRequest.setQuantity(quantity);
        productOfStoreRequest.setDiscount(discount);
        productOfStoreRequest.setProduct(product.get());
        productOfStoreRequest.setStore(store);
        iProductOfStoreService.insert(productOfStoreRequest);
        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse("OK",GlobalConstant.ResultResponse.SUCCESS,""));
    }
    @PostMapping(value = "/productOS/update/{id}")
    ResponseEntity<ApiResponse> UpdateProductOfMyStore(@RequestParam String storeCode,
                                                  @RequestParam Long priceI,
                                                  @RequestParam Long priceO,
                                                  @RequestParam int quantity,
                                                  @RequestParam double discount,
                                                  @RequestParam String description,
                                                  @PathVariable Long id
    ){


        String email = getEmailAuthentication();
        Store store= isManagerStore(storeCode,email);;
        Optional<ProductOfStore> productOfStore = iProductOfStoreService.ProductOfStoreById(id);
        if (productOfStore.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body(new ApiResponse("FAILD",GlobalConstant.ResultResponse.SUCCESS,""));
        }
        ProductOfStoreRequest productOfStoreRequest = new ProductOfStoreRequest();
        productOfStoreRequest.setPriceI(priceI);
        productOfStoreRequest.setPriceO(priceO);
        productOfStoreRequest.setQuantity(quantity);
        productOfStoreRequest.setDiscount(discount);
        productOfStoreRequest.setDescription(description);
        productOfStoreRequest.setStore(store);
        productOfStoreRequest.setProduct(productOfStore.get().getProduct());
        iProductOfStoreService.update(productOfStoreRequest,productOfStore.get());
        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse("OK",GlobalConstant.ResultResponse.SUCCESS,""));
    }
    @PostMapping(value = "/productOS/active/{id}")
    ResponseEntity<ApiResponse> ActiveProductOfMyStore(@RequestParam String storeCode,
                                                       @PathVariable Long id
    ){
        String email = getEmailAuthentication();
        isManagerStore(storeCode,email);;
        Optional<ProductOfStore> productOfStore = iProductOfStoreService.ProductOfStoreById(id);
        if (productOfStore.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body(new ApiResponse("FAILD",GlobalConstant.ResultResponse.SUCCESS,""));
        }
        iProductOfStoreService.enable(productOfStore.get());
        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse("OK",GlobalConstant.ResultResponse.SUCCESS,""));
    }
    @PostMapping(value = "/productOS/delete/{id}")
    ResponseEntity<ApiResponse> DeleteProductOfMyStore(@RequestParam String storeCode,
                                                  @PathVariable Long id
    ){

        String email = getEmailAuthentication();
        isManagerStore(storeCode,email);
        Optional<ProductOfStore> productOfStore = iProductOfStoreService.ProductOfStoreById(id);
        if (productOfStore.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse("FAILED","FAILED",""));
        }
        iProductOfStoreService.softDelete(productOfStore.get());
        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse("OK",GlobalConstant.ResultResponse.SUCCESS,""));
    }
    private Optional<ProductOfStore> isExistProductOfStore(String storeCode,String slugProduct) {
        return iProductOfStoreService.isExistProductOfStore(slugProduct,storeCode);
    }
    @PostMapping(value = "/productOS/image/getAll")
    ResponseEntity<ApiResponse> getAllProductOSImage(@RequestParam  String storeCode,
                                                     @RequestParam Long productOSID){

        Optional<ProductOfStoreResponse> productOfStoreResponse = iProductOfStoreService.getProductOfStoreById(productOSID);
        if (productOfStoreResponse.isEmpty()){
            throw new NotFoundObjectException(GlobalConstant.ErrorCode.MER404,GlobalConstant.ResultResponse.FAILURE);
        }
        List<ProductOSImageResponse> productOSImageResponses = iProductOSImageService.getAllProductOSImage(storeCode,productOSID);
        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse("OK",GlobalConstant.ResultResponse.SUCCESS,productOSImageResponses));
    }

    @PostMapping(value = "/productOS/image/insert")
    ResponseEntity<ApiResponse> insertProductOSImage(@RequestParam String storeCode,
                                                     @RequestParam long productOSID,
                                                     @RequestParam String title,
                                                     @RequestParam String description,
                                                     @RequestParam MultipartFile imageOS){
        String email = getEmailAuthentication();
        isManagerStore(storeCode,email);;
        Optional<ProductOfStore> productOfStore = iProductOfStoreService.ProductOfStoreById(productOSID);
        if (productOfStore.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body(new ApiResponse("FAILD",GlobalConstant.ResultResponse.SUCCESS,""));
        }
        if (imageOS.isEmpty() ) {
            throw new NotFoundObjectException(GlobalConstant.ObjectClass.STORE, GlobalConstant.ErrorCode.MER430);
        }
        Map<String, Object> uploadthumbnail = upload(imageOS);
        ProductOSImageRequest productOSImageRequest = new ProductOSImageRequest();

        productOSImageRequest.setUrlImage(uploadthumbnail.get("secure_url").toString());
        productOSImageRequest.setTitle(title);
        productOSImageRequest.setDescription(description);
        productOSImageRequest.setProductOfStore(productOfStore.get());
        iProductOSImageService.Insert(productOSImageRequest);
        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse("OK",GlobalConstant.ResultResponse.SUCCESS,""));
    }

    @PostMapping(value = "productOS/image/active/{id}")
    ResponseEntity<ApiResponse> activeImageOS(@PathVariable long id,
                                              @RequestParam String storeCode,
                                              @RequestParam long productOSID){
        String email = getEmailAuthentication();
        isManagerStore(storeCode,email);;
        Optional<ProductOfStore> productOfStore = iProductOfStoreService.ProductOfStoreById(productOSID);
        if (productOfStore.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body(new ApiResponse("FAILD",GlobalConstant.ResultResponse.SUCCESS,""));
        }
        Optional<ProductOfStoreImage> productOfStoreImage = iProductOSImageService.getProductOSImageById(id);
        if (productOfStoreImage.isPresent()){
            iProductOSImageService.activeImage(productOfStoreImage.get());
            return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse("OK",GlobalConstant.ResultResponse.SUCCESS,""));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse("FAIL",GlobalConstant.ResultResponse.FAILURE,""));
    }

    @PostMapping(value = "productOS/image/inactive/{id}")
    ResponseEntity<ApiResponse> inActiveImageOS(@PathVariable long id,
                                              @RequestParam String storeCode,
                                              @RequestParam long productOSID){
        String email = getEmailAuthentication();
        isManagerStore(storeCode,email);;
        Optional<ProductOfStore> productOfStore = iProductOfStoreService.ProductOfStoreById(productOSID);
        if (productOfStore.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body(new ApiResponse("FAILD",GlobalConstant.ResultResponse.SUCCESS,""));
        }
        Optional<ProductOfStoreImage> productOfStoreImage = iProductOSImageService.getProductOSImageById(id);
        if (productOfStoreImage.isPresent()){
            iProductOSImageService.inActive(productOfStoreImage.get());
            return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse("OK",GlobalConstant.ResultResponse.SUCCESS,""));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse("FAIL",GlobalConstant.ResultResponse.FAILURE,""));
    }
    private String verifySession(HttpSession httpSession) {
        return httpSession.getAttribute("verifyStore").toString();
    }

    private Store isManagerStore(String storeCode, String emailManager) {
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
