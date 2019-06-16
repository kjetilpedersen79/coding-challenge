package io.bankbridge.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.bankbridge.model.BankModel;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import spark.Request;
import spark.Response;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BanksRemoteCalls implements SparkHandler {

    private Map<String, String> config;
    private CloseableHttpClient httpClient;

    @Override
    public void init() throws IOException {
        config = new ObjectMapper().readValue(Thread.currentThread().getContextClassLoader().getResource("banks-v2.json"), new TypeReference<HashMap<String, String>>() {
        });
        httpClient = HttpClientBuilder.create().build();
    }

    @Override
    public String handle(Request request, Response response) {
        List<Map> result = new ArrayList<>();

        config.forEach((key, value) -> mapBank(request, result, key, value));

        try {
            return new ObjectMapper().writeValueAsString(result);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error while processing request");
        }
    }

    public void close() {
        try {
            httpClient.close();
        } catch (IOException e) {
            throw new RuntimeException("Error while closing");
        }
    }

    private void mapBank(Request request, List<Map> result, String key, String value) {
        try {
            BankModel bank = new ObjectMapper().readValue(getBank(value), BankModel.class);
            bank.name = key;
            boolean match = true;
            if (paramAndNoMatch(request.queryParams(ID), bank.bic)) match = false;
            if (notContains(request.queryParams(NAME), bank.name)) match = false;
            if (match) {
                Map<String, String> map = new HashMap<>();
                map.put("id", bank.bic);
                map.put("name", bank.name);
                result.add(map);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String getBank(String url) {
        HttpGet httpGet = new HttpGet(url);
        HttpResponse httpResponse;
        try {
            httpResponse = httpClient.execute(httpGet);
            int statusCode = httpResponse.getStatusLine().getStatusCode();
            if (statusCode != HttpStatus.SC_OK) {
                throw new RuntimeException("Error on request : " + statusCode);
            }
            return EntityUtils.toString(httpResponse.getEntity());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "failed";
    }

    private boolean notContains(String param, String value) {
        return param != null && !StringUtils.containsIgnoreCase(value, param);
    }

    private boolean paramAndNoMatch(String param, Object value) {
        return param != null && !value.equals(param);
    }

}