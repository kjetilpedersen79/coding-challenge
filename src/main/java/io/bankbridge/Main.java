package io.bankbridge;

import io.bankbridge.handler.BanksCacheBased;
import io.bankbridge.handler.SparkHandler;
import io.bankbridge.handler.BanksRemoteCalls;

import java.io.IOException;

import static spark.Spark.get;
import static spark.Spark.port;

public class Main {

    public static void main(String[] args) throws Exception {
        port(8080);
        getBanks("/v1/banks/all", new BanksCacheBased());
        getBanks("/v2/banks/all", new BanksRemoteCalls());
    }

    private static void getBanks(String path, SparkHandler sparkHandler) throws IOException {
        sparkHandler.init();
        get(path, sparkHandler::handle); // simplify call
    }
}