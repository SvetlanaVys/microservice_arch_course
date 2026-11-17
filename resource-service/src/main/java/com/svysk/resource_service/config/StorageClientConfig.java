package com.svysk.resource_service.config;

import com.svysk.openapi.storage.ApiClient;
import com.svysk.openapi.storage.client.StoragesApi;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class StorageClientConfig {

    @Value("${storage-srv.url}")
    String url;

    @Bean
    @Qualifier("storageRestClient")
    public RestClient storageRestClient(
            @Qualifier("loadBalancedRestClientBuilder")
            RestClient.Builder restClientBuilder) {

        return restClientBuilder
                .baseUrl(url)
                .build();
    }

    @Bean
    @Qualifier("storageRestClient")
    public ApiClient storageApiClient(
            @Qualifier("storageRestClient") RestClient storageRestClient) {
        ApiClient apiClient = new ApiClient(storageRestClient);
        apiClient.setBasePath(url);

        return apiClient;
    }

    @Bean
    public StoragesApi storagesApi(
            @Qualifier("storageRestClient") ApiClient apiClient) {
        return new StoragesApi(apiClient);
    }
}
