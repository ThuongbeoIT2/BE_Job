package com.example.oauth2.config;

import com.example.oauth2.SapoStore.model.Comment;
import com.example.oauth2.SapoStore.model.ProductOfStore;
import com.example.oauth2.SapoStore.repository.CommentRepository;
import com.example.oauth2.SapoStore.repository.ProductOfStoreRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.util.List;

@Component
public class EvaluateScheduler {

    @Autowired
    private ProductOfStoreRepository productOfStoreRepository;


    /*
     * Job chạy mỗi ngày lúc 02:00 để cập nhật đánh giá cho tất cả sản phẩm.
     */
    @Scheduled(cron = "0 0 2 * * ?") // Cập nhật cron job để chạy vào 2:00 sáng
    @Transactional
    public void updateAllProductEvaluates() {
        System.out.println("Job bắt đầu cập nhật đánh giá cho sản phẩm...");

        // Lấy tất cả sản phẩm từ DB
        List<ProductOfStore> products = productOfStoreRepository.findAll();

        for (ProductOfStore product : products) {
            // Lấy tất cả comment liên quan tới sản phẩm
            List<Comment> comments = product.getComments();

            if (!comments.isEmpty()) {
                // Tính trung bình đánh giá (chỉ tính các comment hiển thị)
                double averageEvaluate = comments.stream()
//                        .filter(Comment::isDisplay) // Lọc comment được hiển thị
                        .mapToDouble(Comment::getEvaluate) // Lấy giá trị đánh giá
                        .average()
                        .orElse(0.0); // Giá trị mặc định nếu không có đánh giá

                System.out.printf("Sản phẩm ID %d: Đánh giá cũ = %.2f, đánh giá mới = %.2f%n",
                        product.getId(), product.getEvaluate(), averageEvaluate);
                product.setEvaluate(averageEvaluate);
                productOfStoreRepository.save(product);
            }
        }

        System.out.println("Job cập nhật đánh giá hoàn tất!");
    }
}
