package io.bankbridge;

import io.bankbridge.handler.BanksCacheBased;
import io.bankbridge.handler.BanksLookup;
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

    private static void getBanks(String path, BanksLookup banksCallable) throws IOException {
        banksCallable.init();
        get(path, banksCallable::handle); // simplify call
    }
}