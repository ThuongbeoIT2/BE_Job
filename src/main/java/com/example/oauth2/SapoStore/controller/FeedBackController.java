package com.example.oauth2.SapoStore.controller;

import com.cloudinary.Cloudinary;
import com.example.oauth2.SapoStore.model.Comment;
import com.example.oauth2.SapoStore.model.OrderDetail;
import com.example.oauth2.SapoStore.model.Store;
import com.example.oauth2.SapoStore.page.SapoPageRequest;
import com.example.oauth2.SapoStore.payload.reponse.CommentResponse;
import com.example.oauth2.SapoStore.repository.CommentRepository;
import com.example.oauth2.SapoStore.repository.OrderDetailRepository;
import com.example.oauth2.SapoStore.repository.StoreRepository;
import com.example.oauth2.globalContanst.GlobalConstant;
import com.example.oauth2.payload.ApiResponse;
import com.example.oauth2.util.ProcessUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;

@RestController
public class FeedBackController {
    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private StoreRepository storeRepository;
    @Autowired
    private OrderDetailRepository orderDetailRepository;
    @GetMapping(value = "/get-all")
    ResponseEntity<Page<CommentResponse>> getAllFeedBackByProductOsId(@RequestParam long productOSId,
                                                                      @RequestParam(defaultValue = "0") int page){
        SapoPageRequest sapoPageRequest = new SapoPageRequest(GlobalConstant.Value.PAGELIMIT*2, page * GlobalConstant.Value.PAGELIMIT);
        Page<CommentResponse> productResponses = commentRepository.findFeedBackByProductId(productOSId,sapoPageRequest).map(CommentResponse::cloneCommentResponse);
        return ResponseEntity.status(HttpStatus.OK).body(productResponses);
    }
    @PostMapping(value = "insert-feedback")
    ResponseEntity<ApiResponse> insertFeedBack(@RequestParam long orderId,
                                               @RequestParam String description,
                                               @RequestParam int evaluate,
                                               @RequestParam(required = false) MultipartFile urlImage){
        Optional<OrderDetail> orderDetail = orderDetailRepository.findById(orderId);
        if (orderDetail.isEmpty() || orderDetail.get().isFeedback()){
            return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body(new ApiResponse("FAILD",GlobalConstant.ResultResponse.FAILURE,"Bạn không có quyền cho thao tác. Vui lòng kiểm tra lại thông tin."));
        }
        Comment comment = new Comment();
        comment.setDescription(description);
        comment.setEvaluate(evaluate);
        if (!urlImage.isEmpty()){
            Map<String, Object> uploadthumbnail = upload(urlImage);
            comment.setUrlImage(uploadthumbnail.get("secure_url").toString());
        }
        comment.setCreatedAt(ProcessUtils.getCurrentDay());
        comment.setEmail_user(getEmailCustomer());
        comment.setProductOfStore(orderDetail.get().getProductOfStore());
        commentRepository.save(comment);
        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse("OK",GlobalConstant.ResultResponse.SUCCESS,""));
    }

    @PostMapping(value = "/toggle-comment-display")
    public ResponseEntity<ApiResponse> toggleCommentDisplay(
            @RequestParam long commentId,
            @RequestParam boolean isDisplay,
            @RequestParam String storeCode) {

        Optional<Comment> commentOptional = commentRepository.findById(commentId);
        Optional<Store> store = storeRepository.findStoreByCode(storeCode);
        if (commentOptional.isEmpty() || store.isEmpty() || store.get().getEmail_manager().equalsIgnoreCase(getEmailCustomer())) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse("FAIL", GlobalConstant.ResultResponse.FAILURE, "Comment không tồn tại."));
        }
        Comment comment = commentOptional.get();
        comment.setDisplay(isDisplay);
        commentRepository.save(comment);
        String message = isDisplay ? "Comment đã được hiển thị." : "Comment đã được ẩn.";
        return ResponseEntity.status(HttpStatus.OK)
                .body(new ApiResponse("OK", GlobalConstant.ResultResponse.SUCCESS, message));
    }
    @Autowired
    private Cloudinary cloudinary;
    public Map upload(MultipartFile file) {
        try {
            Map data = cloudinary.uploader().upload(file.getBytes(), Map.of());
            return data;
        } catch (IOException io) {
            throw new RuntimeException("Image upload fail");
        }
    }
    String getEmailCustomer(){
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }
}
