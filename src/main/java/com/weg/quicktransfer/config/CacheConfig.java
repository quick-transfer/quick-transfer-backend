package com.weg.quicktransfer.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.cache.support.NoOpCacheManager;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class CacheConfig {

    @Bean
    @ConditionalOnProperty(name = "app.cache.local.enabled", havingValue = "true")
    public CacheManager cacheManager() {

        CaffeineCacheManager manager = new CaffeineCacheManager(
                "courses",
                "courseById",
                "places",
                "placeById",
                "skills",
                "skillById",
                "vacancySkills",
                "vacancySkillById",
                "vacancies",
                "vacancyById"
        );

        manager.setCaffeine(
                Caffeine.newBuilder()
                        .maximumSize(1000)
                        .expireAfterWrite(Duration.ofMinutes(10))
        );

        return manager;
    }

    @Bean
    @ConditionalOnProperty(name = "app.cache.local.enabled", havingValue = "false", matchIfMissing = true)
    public CacheManager noOpCacheManager() {
        return new NoOpCacheManager();
    }
}
