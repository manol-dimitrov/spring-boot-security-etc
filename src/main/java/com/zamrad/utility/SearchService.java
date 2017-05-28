package com.zamrad.utility;

import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.google.common.base.Supplier;
import com.google.common.base.Throwables;
import com.zamrad.domain.profiles.Profile;
import io.searchbox.client.JestClient;
import io.searchbox.client.JestClientFactory;
import io.searchbox.client.JestResult;
import io.searchbox.client.config.HttpClientConfig;
import io.searchbox.core.Index;
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
import java.util.UUID;

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
        put(Profile.builder().firstName("Manol").secondName("Dimitrov").bio("rock star"));
        put(Profile.builder().firstName("Alex").secondName("Buck").bio("hip hop rapper star"));
        put(Profile.builder().firstName("Josh").secondName("King").bio("indie rock star"));
        put(Profile.builder().firstName("Dave").secondName("Jackson").bio("drum and bass, d & b"));
    }

    private void createIndex() throws IOException {
        final JestResult profiles = client.execute(new CreateIndex.Builder("profiles").build());
        LOGGER.info("Result of cluster creation is: {}", profiles.getJsonString());
    }

    public void put(Object source) {
        Index index = new Index.Builder(source).index("profiles").id(UUID.randomUUID().toString()).build();
        try {
            client.execute(index);
        } catch (IOException e) {
            Throwables.propagate(e);
        }
    }

    public void delete() {

    }

    public void update() {

    }
}
