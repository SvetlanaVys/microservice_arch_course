package com.svysk.song_service.config;

import com.svysk.openapi.ApiClient;
import com.svysk.openapi.client.ResourcesApi;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {

    @Value("${resource-srv.url}")
    String url;

    @Bean
    @Primary
    public RestClient.Builder restClientBuilder() {
        return RestClient.builder();
    }

    @Bean
    @LoadBalanced
    public RestClient.Builder loadBalancedRestClientBuilder() {
        return RestClient.builder();
    }

    @Bean
    public RestClient restClient(
            @Qualifier("loadBalancedRestClientBuilder")
            RestClient.Builder restClientBuilder) {

        return restClientBuilder
                .baseUrl(url)
                .build();
    }

    @Bean
    public ApiClient apiClient(RestClient restClient) {
        ApiClient apiClient = new ApiClient(restClient);
        apiClient.setBasePath(url);

        return apiClient;
    }

    @Bean
    public ResourcesApi songsApi(ApiClient apiClient) {
        return new ResourcesApi(apiClient);
    }
}
