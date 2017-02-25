package com.zamrad.clients;

import com.stripe.Stripe;
import com.stripe.exception.*;
import com.stripe.model.Account;
import com.stripe.model.Charge;
import com.stripe.model.Customer;
import com.stripe.model.Token;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class StripeClient {

    private static final Map<String, String> ENVIRONMENT_VARIABLES = System.getenv();

    @Autowired
    public StripeClient() {

    }

    @Test
    public void createTestCard() {
        Stripe.apiKey = ENVIRONMENT_VARIABLES.get("STRIPE_API_KEY");

        Map<String, Object> tokenParams = new HashMap<>();
        Map<String, Object> cardParams = new HashMap<>();
        cardParams.put("number", "4242424242424242");
        cardParams.put("exp_month", 2);
        cardParams.put("exp_year", 2018);
        cardParams.put("cvc", "314");
        tokenParams.put("card", cardParams);

        try {
            Token.create(tokenParams);
        } catch (AuthenticationException | InvalidRequestException | CardException | APIConnectionException | APIException e) {
            throw new RuntimeException("Could not create test card: ", e);
        }
    }

    /**
     * Customer objects are created when a booker creates an event.
     * All bookers must have a customer object.
     * Source token parameter is passed via the client app.
     */
    public Customer createCustomer(String clientToken, String email) {
        Stripe.apiKey = ENVIRONMENT_VARIABLES.get("STRIPE_API_KEY");

        Map<String, Object> customerParams = new HashMap<>();
        customerParams.put("description", String.format("%s%s", "Customer stripe object for ", email));
        customerParams.put("source", clientToken);

        try {
            return Customer.create(customerParams);
        } catch (AuthenticationException | InvalidRequestException | APIConnectionException | APIException | CardException e) {
            throw new RuntimeException("Failed to create stripe customer object: ", e);
        }
    }

    /**
     * Retrieves a stripe customer.
     *
     * @param stripeCustomerId stripe customer id.
     */
    public void getCustomer(String stripeCustomerId) {
        Stripe.apiKey = ENVIRONMENT_VARIABLES.get("STRIPE_API_KEY");

        try {
            Customer.retrieve(stripeCustomerId);
        } catch (AuthenticationException | InvalidRequestException | CardException | APIConnectionException | APIException e) {
            throw new RuntimeException("Failed to retrieve customer: ", e);
        }
    }

    /**
     * Creates a charge for a specified amount
     *
     * @param customerId stripe customer id.
     * @param amount     amount of the charge.
     * @param currency   currency to be applied.
     */
    public void createCharge(String customerId, String amount, String currency) {
        Stripe.apiKey = ENVIRONMENT_VARIABLES.get("STRIPE_API_KEY");

        Map<String, Object> chargeParams = new HashMap<>();
        chargeParams.put("amount", amount);
        chargeParams.put("currency", currency);
        chargeParams.put("description", "Charge.");
        chargeParams.put("customer", customerId);

        try {
            Charge.create(chargeParams);
        } catch (AuthenticationException | InvalidRequestException | CardException | APIConnectionException | APIException e) {
            throw new RuntimeException("Failed to create a charge: ", e);
        }
    }

    /**
     * Creates a connect stripe account for artists
     *
     * @param country country where account should be created.
     * @param email   user email.
     */
    public Account createConnectAccount(String country, String email) {
        Stripe.apiKey = ENVIRONMENT_VARIABLES.get("STRIPE_API_KEY");

        Map<String, Object> accountParams = new HashMap<>();
        accountParams.put("country", country);
        accountParams.put("managed", false);
        accountParams.put("email", email);

        try {
            return Account.create(accountParams);
        } catch (AuthenticationException | InvalidRequestException | APIConnectionException | CardException | APIException e) {
            throw new RuntimeException("A deferred account could not be created: ", e);
        }

    }

    public Account getAccount(String accountId) {
        Stripe.apiKey = ENVIRONMENT_VARIABLES.get("STRIPE_API_KEY");

        try {
            return Account.retrieve(accountId, null);
        } catch (AuthenticationException | InvalidRequestException | CardException | APIConnectionException | APIException e) {
            throw new RuntimeException("Failed to retrieve account: ", e);
        }
    }
}
