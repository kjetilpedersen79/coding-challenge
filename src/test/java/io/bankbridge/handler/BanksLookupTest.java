package io.bankbridge.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import spark.Request;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static io.bankbridge.handler.SparkHandler.ID;
import static io.bankbridge.handler.SparkHandler.NAME;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

public abstract class BanksLookupTest<T extends SparkHandler> {

    protected T banksLookup;
    protected Request request;

    @Test
    public void thatId5678ReturnsCreditSweets() throws Exception {
        banksLookup.init();
        when(request.queryParams(ID)).thenReturn("5678");
        String output = banksLookup.handle(request, null);
        assertBanks(output, new Bank("5678", "Credit Sweets"));
    }

    @Test
    public void thatId0ReturnsEmpty() throws Exception {
        banksLookup.init();
        when(request.queryParams(ID)).thenReturn("0");
        String output = banksLookup.handle(request, null);
        assertEquals("[]", output);
    }

    @Test
    public void thatNameEreturnsAll() throws Exception {
        banksLookup.init();
        when(request.queryParams(NAME)).thenReturn("e");
        String output = banksLookup.handle(request, null);
        assertAllBanks(output);
    }

    @Test
    public void thatNameSreturnsCreditSweetsAndBanco() throws Exception {
        banksLookup.init();
        when(request.queryParams(NAME)).thenReturn("s");
        String output = banksLookup.handle(request, null);
        assertBanks(output, new Bank("5678", "Credit Sweets"), new Bank("9870", "Banco de espiritu santo"));
    }

    @Test
    public void thatNameSweetsReturnsCreditSweets() throws Exception {
        banksLookup.init();
        when(request.queryParams(NAME)).thenReturn("Sweets");
        String output = banksLookup.handle(request, null);
        assertBanks(output, new Bank("5678", "Credit Sweets"));
    }

    @Test
    public void thatNameSWEETSreturnsCreditSweets() throws Exception {
        banksLookup.init();
        when(request.queryParams(NAME)).thenReturn("SWEETS");
        String output = banksLookup.handle(request, null);
        assertBanks(output, new Bank("5678", "Credit Sweets"));
    }

    @Test
    public void thatNameDummyReturnsEmpty() throws Exception {
        banksLookup.init();
        when(request.queryParams(NAME)).thenReturn("dummy");
        String output = banksLookup.handle(request, null);
        assertEquals("[]", output);
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

    protected static class BankList {
        public List<Bank> banks = new ArrayList<>();
    }

    protected static class Bank {
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
