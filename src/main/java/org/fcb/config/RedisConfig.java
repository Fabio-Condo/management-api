package org.fcb.config;

import org.fcb.constant.CacheNames;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class RedisConfig {

    @Value("${spring.data.redis.host}")
    private String host;

    @Value("${spring.data.redis.port}")
    private int port;

    // 1. Conexão Redis
    @Bean
    public RedisStandaloneConfiguration redisStandaloneConfiguration() {
        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration();
        config.setHostName(host);
        config.setPort(port);
        return config;
    }

    // 2. Configuração base de cache
    private RedisCacheConfiguration baseConfig() {
        return RedisCacheConfiguration.defaultCacheConfig()
                .serializeKeysWith(
                        RedisSerializationContext.SerializationPair
                                .fromSerializer(new StringRedisSerializer())
                )
                .serializeValuesWith(
                        RedisSerializationContext.SerializationPair
                                .fromSerializer(new GenericJackson2JsonRedisSerializer())
                );
    }

    // 3. Cache Manager com TTL por tipo (IMPORTANTE)
    @Bean
    public RedisCacheManager cacheManager(RedisConnectionFactory connectionFactory) {

        RedisCacheConfiguration base = baseConfig()
                .prefixCacheNameWith("management-api::")
                .disableCachingNullValues();

        Map<String, RedisCacheConfiguration> cacheConfigs = new HashMap<>();

        // ACCOUNTS
        cacheConfigs.put(CacheNames.ACCOUNT_DETAIL,
                base.entryTtl(Duration.ofMinutes(1)));
        cacheConfigs.put(CacheNames.ACCOUNT_LIST,
                base.entryTtl(Duration.ofMinutes(1)));
        cacheConfigs.put(CacheNames.ACCOUNT_FILTER,
                base.entryTtl(Duration.ofMinutes(1)));


        // CUSTOMERS
        cacheConfigs.put(CacheNames.CUSTOMER_DETAIL,
                base.entryTtl(Duration.ofMinutes(1)));
        cacheConfigs.put(CacheNames.CUSTOMER_LIST,
                base.entryTtl(Duration.ofMinutes(1)));
        cacheConfigs.put(CacheNames.CUSTOMER_FILTER,
                base.entryTtl(Duration.ofMinutes(1)));

        return RedisCacheManager.builder(connectionFactory)
                .cacheDefaults(base.entryTtl(Duration.ofDays(1)))
                .withInitialCacheConfigurations(cacheConfigs)
                .build();
    }
}