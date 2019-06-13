package io.bankbridge.handler;

import org.ehcache.Cache;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class BanksCacheBasedTest {

    private BanksCacheBased banksCacheBased;

    @BeforeEach
    public void beforeAll() {
        banksCacheBased = new BanksCacheBased();
    }

    @Test
    public void thatInitCreatesBanksCache() throws Exception {
        banksCacheBased.init();
        Cache<String, String> cache = banksCacheBased.getCache();
        assertNotNull(cache);
    }

    @Test
    public void thatHandleReturnsNameAndIdForBanksInCache() throws Exception {
        banksCacheBased.init();
        String output = banksCacheBased.handle(null, null);
        assertEquals("[{\"name\":\"Credit Sweets\",\"id\":\"5678\"},{\"name\":\"Banco de espiritu santo\",\"id\":\"9870\"},{\"name\":\"Royal Bank of Boredom\",\"id\":\"1234\"}]", output);
    }

    @Test
    public void thatHandleReturnsEmptyArrayOnEmptyCache() throws Exception {
        String output = banksCacheBased.handle(null, null);
        assertEquals("[]", output);
    }

    @Test
    public void thatHandleReturnsOneBank() throws Exception {
        banksCacheBased.init("banks-test-v1.json");
        String output = banksCacheBased.handle(null, null);
        assertEquals("[{\"name\":\"dummy\",\"id\":\"111\"}]", output);
    }

    @AfterEach
    public void afterEach() {
        banksCacheBased.close();
    }
}
