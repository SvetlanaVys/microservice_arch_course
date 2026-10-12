package com.svysk.resource_service.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;
import software.amazon.awssdk.services.s3.model.CreateBucketRequest;
import software.amazon.awssdk.services.s3.model.HeadBucketRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;

import java.net.URI;

@Configuration
public class S3Config {

    @Bean
    public S3Client s3Client(
            @Value("${storage.s3.endpoint}") String endpoint,
            @Value("${storage.s3.region}") String region,
            @Value("${storage.s3.access-key}") String accessKey,
            @Value("${storage.s3.secret-key}") String secretKey) {

        return S3Client.builder()
                .endpointOverride(URI.create(endpoint))
                .region(Region.of(region))
                .credentialsProvider(
                        StaticCredentialsProvider.create(
                                AwsBasicCredentials.create(
                                        accessKey, secretKey)))
                .serviceConfiguration(
                        S3Configuration.builder()
                                .pathStyleAccessEnabled(true)
                                .build())
                .build();
    }

    @Bean
    public CommandLineRunner initializeBucket(
            S3Client s3Client,
            @Value("${storage.s3.bucket}") String bucket
    ) {
        return args -> {
            try {
                s3Client.headBucket(
                        HeadBucketRequest.builder()
                                .bucket(bucket)
                                .build()
                );

                System.out.printf("S3 bucket already exist: %s", bucket);
            } catch (S3Exception e) {
                if(e.statusCode() == 404) {
                    s3Client.createBucket(
                            CreateBucketRequest.builder()
                                    .bucket(bucket)
                                    .build()
                    );

                    System.out.printf(
                            "S3 bucket created: %s", bucket
                    );
                } else {
                    throw e;
                }
            }
        };
    }
}
