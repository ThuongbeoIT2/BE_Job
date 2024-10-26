package com.example.oauth2.SapoStore.repository;

import com.example.oauth2.SapoStore.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Integer> {
    @Query("select o from category o where o.slug=:slug")
    Category findCategoriesBySlug(String slug);

    @Query("SELECT c FROM category c WHERE LOWER(c.cateName) LIKE LOWER(CONCAT('%', :key, '%'))")
    List<Category> searchCategoriesByKey(String key);
}
