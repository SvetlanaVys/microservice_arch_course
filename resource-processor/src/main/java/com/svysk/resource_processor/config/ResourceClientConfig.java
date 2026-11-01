package com.svysk.resource_processor.config;

import com.svysk.openapi.ApiClient;
import com.svysk.openapi.client.ResourcesApi;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class ResourceClientConfig {

    @Value("${resource-srv.url}")
    String url;

    @Bean
    @Qualifier("resourceRestClient")
    public RestClient resourceRestClient(
            @Qualifier("loadBalancedRestClientBuilder")
            RestClient.Builder restClientBuilder) {

        return restClientBuilder
                .baseUrl(url)
                .build();
    }

    @Bean
    @Qualifier("resourceRestClient")
    public ApiClient resourceApiClient(
            @Qualifier("resourceRestClient")RestClient restClient) {

        ApiClient apiClient = new ApiClient(restClient);
        apiClient.setBasePath(url);

        return apiClient;
    }

    @Bean
    public ResourcesApi resourcesApi(
            @Qualifier("resourceRestClient") ApiClient apiClient) {
        return new ResourcesApi(apiClient);
    }
}
