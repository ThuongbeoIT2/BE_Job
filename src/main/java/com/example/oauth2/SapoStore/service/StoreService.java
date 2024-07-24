package com.example.oauth2.SapoStore.service;

import com.example.oauth2.SapoStore.model.Store;
import com.example.oauth2.SapoStore.payload.reponse.StoreResponse;
import com.example.oauth2.SapoStore.payload.request.StoreRequest;
import com.example.oauth2.SapoStore.repository.StoreRepository;
import com.example.oauth2.SapoStore.service.iservice.IStoreService;

import com.example.oauth2.config.EmailMix;
import com.example.oauth2.util.ProcessUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;
@Service
public class StoreService implements IStoreService {
    @Autowired
    private StoreRepository storeRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public Page<StoreResponse> findAll(Pageable pageable) {
        return storeRepository.findAll(pageable).map(StoreResponse::cloneFromStore);
    }

    @Override
    public Optional<Store> findStoreBystoreCode(UUID storeCode) {
        return storeRepository.findStoreByCode(storeCode);
    }

    @Override
    public Optional<StoreResponse> findStoreByCode(UUID storeCode) {
        return storeRepository.findStoreByCode(storeCode).map(StoreResponse::cloneFromStore);
    }

    @Override
    public Page<StoreResponse> getStoreByType(String slug, Pageable pageable) {
        return storeRepository.getStoreByType(slug,pageable).map(StoreResponse::cloneFromStore);
    }

    @Override
    public Page<StoreResponse> getStoreByEmailManager(String email, Pageable pageable) {
        return storeRepository.getStoreByEmailManager(email, pageable).map(StoreResponse::cloneFromStore);
    }

    @Override
    public Page<StoreResponse> searchStoreByKey(String key, Pageable pageable) {
        return storeRepository.searchStoreByKey(key, pageable).map(StoreResponse::cloneFromStore);
    }

    @Override
    public void Save(Store store) {
        storeRepository.save(store);
    }

    @Override
    public void insert(StoreRequest storeRequest) {
        Store store = new Store();
        store.setStoreCode(UUID.randomUUID());
        store.setStoreName(storeRequest.getStoreName());
        store.setAddress(storeRequest.getAddress());
        store.setDescription(storeRequest.getDescription());
        store.setStatus(false);
        store.setPhoneNumber(storeRequest.getPhoneNumber());
        store.setThumbnail(storeRequest.getThumbnail());
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        store.setEmail_manager(email);
        store.setEKyc(storeRequest.getEKyc());
        store.setCreatedAt(ProcessUtils.getCurrentDay());
        store.setUpdatedAt(ProcessUtils.getCurrentDay());
        store.setEvaluate(5);
        store.setView(0);
        store.setStoretype(storeRequest.getStoreType());
        storeRepository.save(store);
    }

    @Override
    public void update(StoreRequest storeRequest, Store store) {
        store.setStoreName(storeRequest.getStoreName());
        store.setAddress(storeRequest.getAddress());
        store.setDescription(storeRequest.getDescription());
        store.setPhoneNumber(storeRequest.getPhoneNumber());
        store.setUpdatedAt(ProcessUtils.getCurrentDay());
    }

    @Override
    public void softDelete(Store store) {
        store.setStatus(false);
    }

    @Override
    public void enableStatus(Store store) {
        store.setStatus(true);
    }

    @Override
    public void ACPStore(Store store) {
        String otp= ProcessUtils.generateTempPwd(6);
        store.setStatus(true);
        store.setPassword(passwordEncoder.encode(otp));
        EmailMix emailMix= new EmailMix("myEmail","emailpassword16",0);
        String body = "<html>" +
                "<body>" +
                "<h1>Xin chào,</h1>" +
                "<p>Cửa hàng <strong>" + store.getStoreName() + "</strong> của bạn đã được kích hoạt thành công.</p>" +
                "<p>Password của bạn là: <strong>" + otp + "</strong></p>" +
                "<p>Vui lòng sử dụng mã này để đăng nhập và đổi mật khẩu của bạn ngay sau khi đăng nhập.</p>" +
                "<p>Trân trọng,</p>" +
                "<p>Đội ngũ hỗ trợ</p>" +
                "</body>" +
                "</html>";
        emailMix.sendContentToVer2(store.getEmail_manager(),"Kích hoạt Cửa hàng : ".concat(store.getStoreName()),body);
        storeRepository.save(store);
    }

}
