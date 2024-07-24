package com.example.oauth2.SapoStore.payload.reponse;

import com.example.oauth2.SapoStore.model.Category;
import lombok.Getter;
import lombok.Setter;



@Getter
@Setter
public class CategoryResponse {
    private int id;
    private String cateName;
    private String description;
    private String thumbnail;
    private String slug;
    public static CategoryResponse cloneFromCategory(Category category) {
        CategoryResponse categoryResponse= new CategoryResponse();
        categoryResponse.setCateName(category.getCateName());
        categoryResponse.setId(category.getId());
        categoryResponse.setSlug(category.getSlug());
        categoryResponse.setDescription(category.getDescription());
        categoryResponse.setThumbnail(category.getThumbnail());
        return categoryResponse;
    }
}
