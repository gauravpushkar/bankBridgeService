package io.bankbridge.cache;

import io.bankbridge.utils.ApiVersionEnum;
import io.bankbridge.utils.BanksPropertyHandler;
import org.ehcache.Cache;
import org.ehcache.CacheManager;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.CacheManagerBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/***
 * Cache factory which returns Cache Implementation
 * This factory returns ehcache based cache ( in real world we could create a wrapper to return different types of
 * caches like memcached/redis/ehcache etc)
 */
public class CacheFactory {
    private static final Logger LOGGER = LoggerFactory.getLogger(CacheFactory.class);
    private static CacheManager cacheManager;

    static {
        CacheConfigurationBuilder<String, String> configurationBuilder = CacheConfigurationBuilder
                .newCacheConfigurationBuilder(String.class, String.class,
                        ResourcePoolsBuilder.heap(BanksPropertyHandler.getInstance().getApplicationProperties()
                                .getEhcacheHeapEntryUnit()));
        //Two different caches for different versions with unique aliases
        cacheManager = CacheManagerBuilder
                .newCacheManagerBuilder().withCache(ApiVersionEnum.V1.name(), configurationBuilder)
                .build();
        cacheManager.init();
        cacheManager.createCache(ApiVersionEnum.V2.name(), configurationBuilder);

    }


    public static Cache<String, String> getCache(ApiVersionEnum version) {
        Cache<String, String> cache = cacheManager.getCache(version.name(), String.class, String.class);
        LOGGER.debug("Returning cache for alias {} ", version.name());
        return cache;

    }
}
