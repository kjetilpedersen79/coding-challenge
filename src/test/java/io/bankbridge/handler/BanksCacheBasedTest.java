package io.bankbridge.handler;

import org.ehcache.Cache;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import spark.Request;

import static io.bankbridge.handler.SparkHandler.ID;
import static io.bankbridge.handler.SparkHandler.NAME;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

public class BanksCacheBasedTest {

    private BanksCacheBased banksCacheBased;
    private Request request;

    @BeforeEach
    public void beforeAll() {
        banksCacheBased = new BanksCacheBased();
        request = Mockito.mock(Request.class);
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
        String output = banksCacheBased.handle(request, null);
        assertEqualsAll(output);
    }

    @Test
    public void thatHandleReturnsEmptyOnEmptyCache() throws Exception {
        String output = banksCacheBased.handle(request, null);
        assertEquals("[]", output);
    }

    @Test
    public void thatHandleReturnsOneBank() throws Exception {
        banksCacheBased.init("banks-test-v1.json");
        String output = banksCacheBased.handle(request, null);
        assertEquals("[{\"name\":\"dummy\",\"id\":\"111\"}]", output);
    }

    @Test
    public void thatId5678ReturnsCreditSweets() throws Exception {
        banksCacheBased.init();
        when(request.queryParams(ID)).thenReturn("5678");
        String output = banksCacheBased.handle(request, null);
        assertEqualsCreditSweets(output);
    }

    @Test
    public void thatId0ReturnsEmpty() throws Exception {
        banksCacheBased.init();
        when(request.queryParams(ID)).thenReturn("0");
        String output = banksCacheBased.handle(request, null);
        assertEquals("[]", output);
    }

    @Test
    public void thatNameEreturnsAll() throws Exception {
        banksCacheBased.init();
        when(request.queryParams(NAME)).thenReturn("e");
        String output = banksCacheBased.handle(request, null);
        assertEqualsAll(output);
    }

    @Test
    public void thatNameSreturnsCreditSweetsAndBanco() throws Exception {
        banksCacheBased.init();
        when(request.queryParams(NAME)).thenReturn("s");
        String output = banksCacheBased.handle(request, null);
        assertEquals("[{\"name\":\"Credit Sweets\",\"id\":\"5678\"},{\"name\":\"Banco de espiritu santo\",\"id\":\"9870\"}]", output);
    }

    @Test
    public void thatNameSweetsReturnsCreditSweets() throws Exception {
        banksCacheBased.init();
        when(request.queryParams(NAME)).thenReturn("Sweets");
        String output = banksCacheBased.handle(request, null);
        assertEqualsCreditSweets(output);
    }

    @Test
    public void thatNameSWEETSreturnsCreditSweets() throws Exception {
        banksCacheBased.init();
        when(request.queryParams(NAME)).thenReturn("SWEETS");
        String output = banksCacheBased.handle(request, null);
        assertEqualsCreditSweets(output);
    }

    @Test
    public void thatNameDummyReturnsEmpty() throws Exception {
        banksCacheBased.init();
        when(request.queryParams(NAME)).thenReturn("dummy");
        String output = banksCacheBased.handle(request, null);
        assertEquals("[]", output);
    }

    @AfterEach
    public void afterEach() {
        banksCacheBased.close();
    }

    private void assertEqualsCreditSweets(String output) {
        assertEquals("[{\"name\":\"Credit Sweets\",\"id\":\"5678\"}]", output);
    }

    private void assertEqualsAll(String output) {
        assertEquals("[{\"name\":\"Credit Sweets\",\"id\":\"5678\"},{\"name\":\"Banco de espiritu santo\",\"id\":\"9870\"},{\"name\":\"Royal Bank of Boredom\",\"id\":\"1234\"}]", output);
    }
}
