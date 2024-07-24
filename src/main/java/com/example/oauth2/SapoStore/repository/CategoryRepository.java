package com.example.oauth2.SapoStore.repository;

import com.example.oauth2.SapoStore.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Integer> {
    @Query("select o from Category o where o.slug=:slug")
    Category findCategoriesBySlug(String slug);
}
