package com.example.oauth2.SapoStore.repository;

import com.example.oauth2.SapoStore.model.Comment;

import com.example.oauth2.SapoStore.page.SapoPageRequest;

import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentRepository extends JpaRepository<Comment,Long> {
    @Query("select o from comment o where o.productOfStore.id=:productOSId order by o.createdAt desc ")
    Page<Comment> findFeedBackByProductId(long productOSId, SapoPageRequest sapoPageRequest);
    @Query("select count(1) from comment o where o.productOfStore.store=:storeCode")
    int findFeedBackOfStore(String storeCode);
}
