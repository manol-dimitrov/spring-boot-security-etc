package com.zamrad.utility;

import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.google.common.base.Supplier;
import com.google.common.base.Throwables;
import com.zamrad.domain.profiles.Profile;
import io.searchbox.annotations.JestId;
import io.searchbox.client.JestClient;
import io.searchbox.client.JestClientFactory;
import io.searchbox.client.JestResult;
import io.searchbox.client.config.HttpClientConfig;
import io.searchbox.core.Index;
import io.searchbox.core.Search;
import io.searchbox.core.SearchResult;
import io.searchbox.indices.CreateIndex;
import io.searchbox.indices.mapping.PutMapping;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.index.mapper.DocumentMapper;
import org.elasticsearch.index.mapper.core.StringFieldMapper;
import org.elasticsearch.index.mapper.object.RootObjectMapper;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.highlight.HighlightBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import vc.inreach.aws.request.AWSSigner;
import vc.inreach.aws.request.AWSSigningRequestInterceptor;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Optional;
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

        //createProfilesMapping();

        loadAllData();
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
        Index index = new Index.Builder(source).index("profiles").type("profile").id(UUID.randomUUID().toString()).build();
        try {
            client.execute(index);
        } catch (IOException e) {
            Throwables.propagate(e);
        }
    }

    private void createProfilesMapping() {
        final StringFieldMapper.Builder firstName = new StringFieldMapper.Builder("first_name");
        final StringFieldMapper.Builder lastName = new StringFieldMapper.Builder("last_name");
        final StringFieldMapper.Builder bio = new StringFieldMapper.Builder("bio");

        RootObjectMapper.Builder rootObjectMapperBuilder = new RootObjectMapper.Builder("profiles_mapping")
                .add(firstName.store(true))
                .add(lastName.store(true))
                .add(bio.store(true));

        final Settings indexSettings = ImmutableSettings.builder().put("index.version.created", "5010199").put("uuid", "_8FiehfuRU6SNLUzCh6VLg").build();
        DocumentMapper documentMapper = new DocumentMapper.Builder("profiles", indexSettings, rootObjectMapperBuilder).build(null);

        String expectedMappingSource = documentMapper.mappingSource().toString();
        PutMapping putMapping = new PutMapping.Builder(
                "profiles",
                "profile",
                expectedMappingSource
        ).build();

        try {
            client.execute(putMapping);
        } catch (IOException e) {
            Throwables.propagate(e);
        }
    }

    public void delete() {

    }

    public void update() {

    }

    public Optional<SearchResult> search(String query) {
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.simpleQueryStringQuery(query));

        HighlightBuilder highlightBuilder = new HighlightBuilder();
        highlightBuilder.field("firstName");

        Search search = new Search.Builder(searchSourceBuilder.toString())
                .addIndex("profiles")
                .addType("profile")
                .build();

        try {
            return Optional.ofNullable(client.execute(search));
        } catch (IOException e) {
            Throwables.propagate(e);
            return Optional.empty();
        }
    }
}
