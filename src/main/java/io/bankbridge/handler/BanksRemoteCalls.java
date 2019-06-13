package io.bankbridge.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import spark.Request;
import spark.Response;

import java.io.IOException;
import java.util.Map;

public class BanksRemoteCalls implements BanksCallable {

    private Map config;

    @Override
    public void init() throws IOException {
        config = new ObjectMapper()
                .readValue(Thread.currentThread().getContextClassLoader().getResource("banks-v2.json"), Map.class);
    }

    @Override
    public String handle(Request request, Response response) {
        System.out.println(config);
        throw new RuntimeException("Not implemented");
    }

}