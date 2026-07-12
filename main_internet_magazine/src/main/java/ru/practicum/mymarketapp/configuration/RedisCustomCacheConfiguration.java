package ru.practicum.mymarketapp.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.cache.autoconfigure.RedisCacheManagerBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.serializer.JacksonJsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import ru.practicum.mymarketapp.entity.Item;
import ru.practicum.mymarketapp.pojo.PageCaching;
import ru.practicum.mymarketapp.service.ApiClient;
import ru.practicum.mymarketapp.service.DefaultApi;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

@Configuration
public class RedisCustomCacheConfiguration {
    @Value("${api.base.url}")
    String apiBaseUrl;
    @Value("${CLIENT_SECRET_CODE}")
    String clientSecret;
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
        apiClient.setBasePath(apiBaseUrl);
        defaultApi.setApiClient(apiClient);
        return  defaultApi;
    }



}

