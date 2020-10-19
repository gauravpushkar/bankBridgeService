package io.bankbridge.unitTest;

import io.bankbridge.cache.CacheFactory;
import io.bankbridge.utils.ApiVersionEnum;
import org.ehcache.Cache;
import org.junit.Assert;
import org.junit.Test;

/**
 * Unit Tests for Cache implementation
 */
public class CacheTest {
    private static final String DUMMY_KEY = "dummyKey";
    private static final String KEY = "key";
    private static final String VALUE = "value";

    @Test
    public void testCacheFactory() {
        Assert.assertNotNull(CacheFactory.getCache(ApiVersionEnum.V1));
        Assert.assertNotNull(CacheFactory.getCache(ApiVersionEnum.V2));
    }

    @Test
    public void testGetAndPutValueV1() {
        Cache<String, String> cache = CacheFactory.getCache(ApiVersionEnum.V1);
        Assert.assertNull(cache.get(DUMMY_KEY));
        cache.put(KEY, VALUE);
        Assert.assertNotNull(cache.get(KEY));
    }

    @Test
    public void testGetAndPutValueV2() {
        Cache<String, String> cache = CacheFactory.getCache(ApiVersionEnum.V2);
        Assert.assertNull(cache.get(DUMMY_KEY));
        cache.put(KEY, VALUE);
        Assert.assertNotNull(cache.get(KEY));
    }
}
