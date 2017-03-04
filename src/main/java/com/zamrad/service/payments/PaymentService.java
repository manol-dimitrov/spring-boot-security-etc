package com.zamrad.service.payments;

import com.zamrad.clients.StripeClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Manages stripe customer and charge objects.
 */
@Component
public class PaymentService {

    @Autowired
    private StripeClient stripeClient;

    public void createCustomer() {
        //stripeClient.createCustomer();
    }

    public void getCustomer(){

    }

    public void createCharge(){
        //stripeClient.createCharge();
    }

    public void getDeferredAccount(){

    }

    public void createDeferredAccount(){

    }
}
