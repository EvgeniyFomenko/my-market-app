package ru.practicum.mymarketapp.config;

import org.springframework.boot.cache.autoconfigure.RedisCacheManagerBuilderCustomizer;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.serializer.JacksonJsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.practicum.mymarketapp.entity.Item;
import ru.practicum.mymarketapp.pojo.PageCaching;
import ru.practicum.mymarketapp.service.ApiClient;
import ru.practicum.mymarketapp.service.DefaultApi;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

@TestConfiguration
public class RedisCustomCacheConfiguration {
    @Bean
    public RedisCacheManagerBuilderCustomizer weatherCacheCustomizer() {
        return builder -> builder.withCacheConfiguration(
                "Item",                                         // Имя кеша
                RedisCacheConfiguration.defaultCacheConfig()
                        .entryTtl(Duration.of(1, ChronoUnit.MINUTES))  // TTL
                        .serializeValuesWith(                          // Сериализация JSON
                                RedisSerializationContext.SerializationPair.fromSerializer(
                                        new JacksonJsonRedisSerializer(Item.class)
                                )
                        )

        ).withCacheConfiguration("Page",
                RedisCacheConfiguration.defaultCacheConfig()
                        .entryTtl(Duration.of(1, ChronoUnit.MINUTES))// TTL
                        .serializeValuesWith(                          // Сериализация JSON
                                RedisSerializationContext.SerializationPair.fromSerializer(
                                        new JacksonJsonRedisSerializer(PageCaching.class)
                                )
                        ));
    }

    @Bean
    public DefaultApi defaultApi() {
        DefaultApi defaultApi = new DefaultApi();
        ApiClient apiClient = new ApiClient();
        defaultApi.setApiClient(apiClient.setBasePath("http://pay-service:8080"));
        return  defaultApi;
    }



}

