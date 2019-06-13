package io.bankbridge;

import io.bankbridge.handler.BanksCacheBased;
import io.bankbridge.handler.BanksRemoteCalls;

import static spark.Spark.get;
import static spark.Spark.port;

public class Main {

    public static void main(String[] args) throws Exception {

        port(8080);

        BanksCacheBased.init();
        BanksRemoteCalls.init();

        get("/v1/banks/all", BanksCacheBased::handle); // simplify call
        get("/v2/banks/all", BanksRemoteCalls::handle); // simplify call
    }
}