package com.example.oauth2.SapoStore.payload.reponse;


import com.example.oauth2.SapoStore.model.StoreType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StoreTypeResponse {
    private int id;
    private String typeName;
    private String description;
    private String thumnail;
    private String slug;
    public static StoreTypeResponse cloneFromStoreType(StoreType storeType){
        StoreTypeResponse storeTypeResponse= new StoreTypeResponse();
        storeTypeResponse.setTypeName(storeType.getTypeName());
        storeTypeResponse.setId(storeType.getId());
        storeTypeResponse.setSlug(storeType.getSlug());
        storeTypeResponse.setDescription(storeType.getDescription());
        storeTypeResponse.setThumnail(storeType.getThumnail());
        return storeTypeResponse;
    }
}
