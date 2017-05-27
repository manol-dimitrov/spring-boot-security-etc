package com.zamrad.utility;

import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.google.common.base.Supplier;
import io.searchbox.client.JestClient;
import io.searchbox.client.JestClientFactory;
import io.searchbox.client.JestResult;
import io.searchbox.client.config.HttpClientConfig;
import io.searchbox.indices.CreateIndex;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import vc.inreach.aws.request.AWSSigner;
import vc.inreach.aws.request.AWSSigningRequestInterceptor;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Component
public class SearchService {

    private static final String REGION = "eu-west-1";
    private static final String SERVICE = "es";

    private static final Logger LOGGER = LoggerFactory.getLogger(SearchService.class);
    private JestClient client;

    public SearchService() {
        final Supplier<LocalDateTime> clock = () -> LocalDateTime.now(ZoneOffset.UTC);
        final AWSSigner awsSigner = new AWSSigner(new DefaultAWSCredentialsProviderChain(), REGION, SERVICE, clock);
        final AWSSigningRequestInterceptor requestInterceptor = new AWSSigningRequestInterceptor(awsSigner);

        final JestClientFactory factory = new JestClientFactory() {
            @Override
            protected HttpClientBuilder configureHttpClient(HttpClientBuilder builder) {
                builder.addInterceptorLast(requestInterceptor);
                return builder;
            }

            @Override
            protected HttpAsyncClientBuilder configureHttpClient(HttpAsyncClientBuilder builder) {
                builder.addInterceptorLast(requestInterceptor);
                return builder;
            }
        };

        factory.setHttpClientConfig(new HttpClientConfig
                .Builder("https://search-zamrad-jdwo2kp5xjwpn332mw36pjoouy.eu-west-1.es.amazonaws.com")
                .multiThreaded(true)
                .build());

        client = factory.getObject();
    }

    public void loadAllData() {
        try {
            final JestResult profiles = client.execute(new CreateIndex.Builder("profiles").build());
            LOGGER.info("Result of cluster creation is: {}", profiles.getJsonString());
        } catch (IOException e) {
            throw new RuntimeException("Failed to create index: ", e);
        }
    }

    public void put() {

    }

    public void delete() {

    }

    public void update() {

    }
}
