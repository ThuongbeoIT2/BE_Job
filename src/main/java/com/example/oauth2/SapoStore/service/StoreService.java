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

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

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
    public Optional<Store> findStoreBystoreCode(String storeCode) {
        return storeRepository.findStoreByCode(storeCode);
    }

    @Override
    public Optional<StoreResponse> findStoreByCode(String storeCode) {
        return storeRepository.findStoreByCode(storeCode).map(StoreResponse::cloneFromStore);
    }

    @Override
    public Page<StoreResponse> getStoreByType(String slug, Pageable pageable) {
        return storeRepository.getStoreByType(slug,pageable).map(StoreResponse::cloneFromStore);
    }

    @Override
    public Optional<StoreResponse> getStoreByEmailManager(String email) {
        return storeRepository.getStoreByEmailManager(email).map(StoreResponse::cloneFromStore);
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
        store.setStoreCode(ProcessUtils.generateStoreCode());
        store.setStoreName(storeRequest.getStoreName());
        store.setAddress(storeRequest.getAddress());
        store.setDescription(storeRequest.getDescription());
        store.setStatus(false);
        store.setVNPayAccountLink(storeRequest.getVNPayAccountLink());
        store.setPhoneNumber(storeRequest.getPhoneNumber());
        store.setThumbnail(storeRequest.getThumbnail());
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        store.setEmail_manager(email);
        store.setCreatedAt(ProcessUtils.getCurrentDay());
        store.setUpdatedAt(ProcessUtils.getCurrentDay());
        store.setEvaluate(5);
        store.setView(0);
        store.setEKyc_01(storeRequest.getEKyc_01());
        store.setEKyc_02(storeRequest.getEKyc_02());
        store.setThumbnail(storeRequest.getThumbnail());
        store.setStoretype(storeRequest.getStoreType());
        storeRepository.save(store);
//        ACPStore(store);
    }

    @Override
    public void update(StoreRequest storeRequest, Store store) {
        store.setStoreName(storeRequest.getStoreName());
        store.setAddress(storeRequest.getAddress());
        store.setVNPayAccountLink(storeRequest.getVNPayAccountLink());
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
        EmailMix emailMix= new EmailMix("thuong0205966@huce.edu.vn","ypnyjaakkzrbbjyl",0);
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

    @Override
    public void WarningStore(String email_manager, String message) {
        EmailMix emailMix= new EmailMix("thuong0205966@huce.edu.vn","ypnyjaakkzrbbjyl",0);
        String body = "<html>" +
                "<body>" +
                "<h1>Xin chào,</h1>" +
                "<p>Kính gửi <strong>" + email_manager + "</strong>.Cửa hàng của bạn đã bị nhắc nhở.</p>" +
                "<p>Nội dung  là: <strong>" + message + "</strong></p>" +
                "<p>Vui lòng cân nhắc và phản hồi nếu có sai phạm ảnh hưởng đến lợi ích của bạn.</p>" +
                "<p>Trân trọng,</p>" +
                "<p>Đội ngũ hỗ trợ</p>" +
                "</body>" +
                "</html>";
        emailMix.sendContentToVer2(email_manager,"Thông báo cảnh cáo ! ",body);
    }

    @Override
    public List<StoreResponse> getAllInActive() {
        return storeRepository.getAllInActive().stream().map(StoreResponse::cloneFromStore).collect(Collectors.toList());
    }

}
