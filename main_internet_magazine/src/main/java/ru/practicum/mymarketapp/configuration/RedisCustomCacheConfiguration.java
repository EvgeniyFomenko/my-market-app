package ru.practicum.mymarketapp.configuration;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.cache.autoconfigure.RedisCacheManagerBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.*;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriUtils;
import ru.practicum.mymarketapp.entity.Item;
import ru.practicum.mymarketapp.pojo.PageCaching;
import ru.practicum.mymarketapp.service.ApiClient;
import ru.practicum.mymarketapp.service.DefaultApi;
import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.ObjectMapper;

import java.net.URI;
import java.time.Duration;
import java.time.temporal.ChronoUnit;

@Configuration
public class RedisCustomCacheConfiguration {
    @Value("${api.base.url}")
    String apiBaseUrl;
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
        defaultApi.setApiClient(apiClient.setBasePath(apiBaseUrl));
        return  defaultApi;
    }



}

