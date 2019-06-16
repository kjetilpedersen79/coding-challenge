package io.bankbridge.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.ehcache.Cache;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import spark.Request;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static io.bankbridge.handler.SparkHandler.ID;
import static io.bankbridge.handler.SparkHandler.NAME;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

public class BanksCacheBasedTest {

    private BanksCacheBased banksCacheBased;
    private Request request;

    @BeforeEach
    public void beforeEach() {
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
        assertAllBanks(output);
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
        assertBanks(output, new Bank("111", "dummy"));
    }

    @Test
    public void thatId5678ReturnsCreditSweets() throws Exception {
        banksCacheBased.init();
        when(request.queryParams(ID)).thenReturn("5678");
        String output = banksCacheBased.handle(request, null);
        assertBanks(output, new Bank("5678", "Credit Sweets"));
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
        assertAllBanks(output);
    }

    @Test
    public void thatNameSreturnsCreditSweetsAndBanco() throws Exception {
        banksCacheBased.init();
        when(request.queryParams(NAME)).thenReturn("s");
        String output = banksCacheBased.handle(request, null);
        assertBanks(output, new Bank("5678", "Credit Sweets"), new Bank("9870", "Banco de espiritu santo"));
    }

    @Test
    public void thatNameSweetsReturnsCreditSweets() throws Exception {
        banksCacheBased.init();
        when(request.queryParams(NAME)).thenReturn("Sweets");
        String output = banksCacheBased.handle(request, null);
        assertBanks(output, new Bank("5678", "Credit Sweets"));
    }

    @Test
    public void thatNameSWEETSreturnsCreditSweets() throws Exception {
        banksCacheBased.init();
        when(request.queryParams(NAME)).thenReturn("SWEETS");
        String output = banksCacheBased.handle(request, null);
        assertBanks(output, new Bank("5678", "Credit Sweets"));
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

    private void assertAllBanks(String output) throws IOException {
        assertBanks(output,
                new Bank("1234", "Royal Bank of Boredom"),
                new Bank("5678", "Credit Sweets"),
                new Bank("9870", "Banco de espiritu santo")
        );
    }

    private void assertBanks(String output, Bank... expectedBanks) throws IOException {
        BankList banks = new ObjectMapper().readValue("{\"banks\":" + output + "}", BankList.class);
        assertEquals(expectedBanks.length, banks.banks.size());
        for (Bank expectedBank : expectedBanks) {
            assertTrue(containsBank(banks, expectedBank));
        }
    }

    private boolean containsBank(BankList banks, Bank expectedBank) {
        for (Bank bank : banks.banks) {
            if (expectedBank.id.equals(bank.id) && expectedBank.name.equals(bank.name)) return true;
        }
        return false;
    }

    private static class BankList {
        public List<Bank> banks = new ArrayList<>();
    }

    private static class Bank {
        public String id;
        public String name;

        public Bank() {
        }

        public Bank(String id, String name) {
            this.id = id;
            this.name = name;
        }
    }
}
