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
        get("/bes", (request, response) -> "{\n" +
                "\"name\":\"Banco de espiritu santo\",\n" +
                "\"countryCode\":\"PT\",\n" +
                "\"auth\":\"SSL\"\n" +
                "}");
    }
}