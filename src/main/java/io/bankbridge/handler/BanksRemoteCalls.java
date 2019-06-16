package io.bankbridge.handler;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.bankbridge.model.BankModel;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class BanksRemoteCalls extends BankLookup {

    private Map<String, String> config;
    private CloseableHttpClient httpClient;

    @Override
    public void init() throws IOException {
        config = new ObjectMapper().readValue(Thread.currentThread().getContextClassLoader().getResource("banks-v2.json"), new TypeReference<HashMap<String, String>>() {
        });
        httpClient = HttpClientBuilder.create().build();
    }

    @Override
    protected Map<String, String> getBanks() {
        Map<String, String> banks = new HashMap<>();
        config.forEach((key, value) -> addBank(banks, key, value));
        return banks;
    }

    private void addBank(Map<String, String> banks, String key, String value) {
        BankModel bank;
        try {
            bank = new ObjectMapper().readValue(getBank(value), BankModel.class);
            bank.name = key;
            banks.put(bank.bic, bank.name);
        } catch (IOException e) {
            throw new RuntimeException("Error reading bank");
        }
    }

    private String getBank(String url) throws IOException {
        HttpGet httpGet = new HttpGet(url);
        HttpResponse httpResponse;
        httpResponse = httpClient.execute(httpGet);
        int statusCode = httpResponse.getStatusLine().getStatusCode();
        if (statusCode != HttpStatus.SC_OK) {
            throw new RuntimeException("Error on request : " + statusCode);
        }
        return EntityUtils.toString(httpResponse.getEntity());
    }

    public void close() {
        try {
            httpClient.close();
        } catch (IOException e) {
            throw new RuntimeException("Error on close");
        }
    }
}