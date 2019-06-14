package io.bankbridge.handler;

import spark.Request;
import spark.Response;

import java.io.IOException;

// generic interface to make it easy to switch between cache and remote
public interface BanksLookup {

    String ID = "id";
    String NAME = "name";

    void init() throws IOException;

    String handle(Request request, Response response);

}