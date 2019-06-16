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

import static io.bankbridge.handler.SparkHandler.NAME;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

public class BanksCacheBasedTest extends BanksLookupTest<BanksCacheBased> {

    @BeforeEach
    public void beforeEach() {
        banksLookup = new BanksCacheBased();
        request = Mockito.mock(Request.class);
    }

    @Test
    public void thatInitCreatesBanksCache() throws Exception {
        banksLookup.init();
        Cache<String, String> cache = banksLookup.getCache();
        assertNotNull(cache);
    }

    @Test
    public void thatHandleReturnsNameAndIdForBanksInCache() throws Exception {
        banksLookup.init();
        String output = banksLookup.handle(request, null);
        assertAllBanks(output);
    }

    @Test
    public void thatHandleReturnsEmptyOnEmptyCache() throws Exception {
        String output = banksLookup.handle(request, null);
        assertEquals("[]", output);
    }

    @Test
    public void thatHandleReturnsOneBank() throws Exception {
        banksLookup.init("banks-test-v1.json");
        String output = banksLookup.handle(request, null);
        assertBanks(output, new Bank("111", "dummy"));
    }

    @Test
    public void thatNameDummyReturnsEmpty() throws Exception {
        banksLookup.init();
        when(request.queryParams(NAME)).thenReturn("dummy");
        String output = banksLookup.handle(request, null);
        assertEquals("[]", output);
    }

    @AfterEach
    public void afterEach() {
        banksLookup.close();
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
