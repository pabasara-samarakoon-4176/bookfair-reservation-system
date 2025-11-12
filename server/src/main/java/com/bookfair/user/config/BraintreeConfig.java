package com.bookfair.user.config;

import com.braintreegateway.BraintreeGateway;
import com.braintreegateway.Environment;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BraintreeConfig {

    @Value("${braintree.environment}")
    private String environment;

    @Value("${braintree.merchantId}")
    private String merchantId;

    @Value("${braintree.publicKey}")
    private String publicKey;

    @Value("${braintree.privateKey}")
    private String privateKey;

    @Bean
    public BraintreeGateway braintreeGateway() {
        return new BraintreeGateway(
                getEnvironment(),
                merchantId,
                publicKey,
                privateKey
        );
    }

    private Environment getEnvironment() {
        return "SANDBOX".equalsIgnoreCase(environment) ? Environment.SANDBOX : Environment.PRODUCTION;
    }
}
