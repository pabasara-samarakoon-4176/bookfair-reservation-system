package com.bookfair.user.service;

import com.braintreegateway.BraintreeGateway;
import com.braintreegateway.Result;
import com.braintreegateway.Transaction;
import com.braintreegateway.TransactionRequest;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class BraintreePaymentService {

    private final BraintreeGateway gateway;

    public BraintreePaymentService(BraintreeGateway gateway) {
        this.gateway = gateway;
    }

    public String processMockPayment(BigDecimal amount, String paymentMethodNonce) {
        TransactionRequest request = new TransactionRequest()
                .amount(amount)
                .paymentMethodNonce(paymentMethodNonce)
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
