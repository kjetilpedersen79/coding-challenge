package io.bankbridge;

import static spark.Spark.get;
import static spark.Spark.port;

public class MockRemotes {

    public static void main(String[] args) { // removed throws Exception which is never thrown
        port(1234);

        get("/rbb", (request, response) -> "{\n" +
                "\"bic\":\"1234\",\n" +
                "\"countryCode\":\"GB\",\n" +
                "\"auth\":\"OAUTH\"\n" +
                "}");
        get("/cs", (request, response) -> "{\n" +
                "\"bic\":\"5678\",\n" +
                "\"countryCode\":\"CH\",\n" +
                "\"auth\":\"OpenID\"\n" +
                "}");
        /*
         The last bank doesn't contain a bic, only the name - intentional or not?
         Setting correct bic so that the Mock response matches the README api description
          */
        get("/bes", (request, response) -> "{\n" +
                "\"bic\":\"9870\",\n" +
                "\"countryCode\":\"PT\",\n" +
                "\"auth\":\"SSL\"\n" +
                "}");
    }
}