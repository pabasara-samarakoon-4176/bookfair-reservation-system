package com.bookfair.user.service;

import com.bookfair.user.model.User;
import com.braintreegateway.*;
import com.braintreegateway.exceptions.NotFoundException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class BraintreePaymentService {

    private final BraintreeGateway gateway;

    public BraintreePaymentService(BraintreeGateway gateway) {
        this.gateway = gateway;
    }

    public String findOrCreateCustomer(User user, String nonce) {

        String customerId = String.valueOf(user.getUserId());

        try {
            Customer existingCustomer = gateway.customer().find(customerId);

            PaymentMethodRequest updateRequest = new PaymentMethodRequest()
                    .customerId(customerId)
                    .paymentMethodNonce(nonce);

            Result<? extends PaymentMethod> updateResult = gateway.paymentMethod().create(updateRequest);

            if (updateResult.isSuccess()) {
                return existingCustomer.getId();
            } else {
                throw new RuntimeException("Failed to vault new payment method: " + updateResult.getMessage());
            }
        } catch (NotFoundException e) {
            CustomerRequest createRequest = new CustomerRequest()
                    .id(customerId)
                    .firstName(user.getName())
                    .email(user.getEmail())
                    .paymentMethodNonce(nonce);

            Result<Customer> result = gateway.customer().create(createRequest);

            if (result.isSuccess()) {
                return result.getTarget().getId();
            } else {
                throw new RuntimeException("Failed to create Braintree customer: " + result.getMessage());
            }
        }
    }

    public String processPaymentWithCustomer(BigDecimal amount, String braintreeCustomerId) {

        TransactionRequest request = new TransactionRequest()
                .amount(amount)
                .customerId(braintreeCustomerId)
                .options()
                .submitForSettlement(true)
                .done();

        Result<Transaction> result = gateway.transaction().sale(request);

        if (result.isSuccess()) {
            return "SUCCESS: " + result.getTarget().getId();
        } else {
            return "FAILED: " + result.getMessage() +
                    " | Code: " + result.getTransaction().getProcessorResponseCode();
        }
    }
}
