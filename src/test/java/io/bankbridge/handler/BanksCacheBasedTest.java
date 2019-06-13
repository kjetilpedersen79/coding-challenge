package io.bankbridge.handler;

import org.ehcache.Cache;
import org.ehcache.CacheManager;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.CacheManagerBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class BanksCacheBasedTest {

    @Test
    public void thatInitCreatesBanksCache() throws Exception {
        BanksCacheBased.init();
        Cache<String, String> cache = BanksCacheBased.cacheManager.getCache("banks", String.class, String.class);
        assertNotNull(cache);
    }

    @Test
    public void thatHandleReturnsNameAndIdForBanksInCache() throws Exception {
        BanksCacheBased.init();
        String output = BanksCacheBased.handle(null, null);
        assertEquals("[{\"name\":\"Credit Sweets\",\"id\":\"5678\"},{\"name\":\"Banco de espiritu santo\",\"id\":\"9870\"},{\"name\":\"Royal Bank of Boredom\",\"id\":\"1234\"}]", output);
    }

    @Test
    public void thatHandleReturnsEmptyArrayOnEmptyCache() throws Exception {
        CacheManager cacheManager = CacheManagerBuilder
                .newCacheManagerBuilder().withCache("banks", CacheConfigurationBuilder
                        .newCacheConfigurationBuilder(String.class, String.class, ResourcePoolsBuilder.heap(10)))
                .build();
        cacheManager.init();
        BanksCacheBased.cacheManager = cacheManager;
        String output = BanksCacheBased.handle(null, null);
        assertEquals("[]", output);
    }

    @Test
    public void thatHandleReturnsOneBank() throws Exception {
        CacheManager cacheManager = CacheManagerBuilder
                .newCacheManagerBuilder().withCache("banks", CacheConfigurationBuilder
                        .newCacheConfigurationBuilder(String.class, String.class, ResourcePoolsBuilder.heap(10)))
                .build();
        cacheManager.init();
        Cache<String, String> cache = cacheManager.getCache("banks", String.class, String.class);
        cache.put("111", "dummy");
        BanksCacheBased.cacheManager = cacheManager;
        String output = BanksCacheBased.handle(null, null);
        assertEquals("[{\"name\":\"dummy\",\"id\":\"111\"}]", output);
    }

    @AfterEach
    public void afterEach() {
        BanksCacheBased.cacheManager.close();
    }
}
