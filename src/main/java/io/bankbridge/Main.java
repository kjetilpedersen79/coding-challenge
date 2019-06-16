package io.bankbridge;

import io.bankbridge.handler.BanksCacheBased;
import io.bankbridge.handler.BanksRemoteCalls;
import io.bankbridge.handler.SparkHandler;

import java.io.IOException;

import static spark.Spark.get;
import static spark.Spark.port;

public class Main {

    public static void main(String[] args) throws Exception {
        port(8080);
        addHandler("/v1/banks/all", new BanksCacheBased());
        addHandler("/v2/banks/all", new BanksRemoteCalls());
    }

    /**
     * Generic add handler method
     */
    private static void addHandler(String path, SparkHandler sparkHandler) throws IOException {
        sparkHandler.init();
        get(path, sparkHandler::handle);
    }
}