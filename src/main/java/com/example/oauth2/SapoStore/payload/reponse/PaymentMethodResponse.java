package com.example.oauth2.SapoStore.payload.reponse;

import com.example.oauth2.SapoStore.model.PaymentMethod;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaymentMethodResponse {
    private int id;
    private String slug;
    private String paymentmethod;
    private String description;
    public static PaymentMethodResponse cloneFromPaymentMethod(PaymentMethod paymentMethod){
        PaymentMethodResponse paymentMethodResponse = new PaymentMethodResponse();
        paymentMethodResponse.setId(paymentMethod.getId());
        paymentMethodResponse.setPaymentmethod(paymentMethod.getMethod());
        paymentMethodResponse.setSlug(paymentMethod.getSlug());
        paymentMethodResponse.setDescription(paymentMethod.getDescription());
        return paymentMethodResponse;
    }

}
