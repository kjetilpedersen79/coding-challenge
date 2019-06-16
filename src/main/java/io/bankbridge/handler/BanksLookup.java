package io.bankbridge.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import spark.Request;
import spark.Response;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class BanksLookup implements SparkHandler {

    public static String ID = "id";
    public static String NAME = "name";

    @Override
    public String handle(Request request, Response response) {
        List<Map> result = new ArrayList<>();
        getBanks().forEach((key, value) -> filter(request, result, key, value));
        try {
            return new ObjectMapper().writeValueAsString(result);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error while processing request");
        }
    }

    protected abstract Map<String, String> getBanks();

    private void filter(Request request, List<Map> result, String key, String value) {
        boolean match = true;
        if (paramAndNoMatch(request.queryParams(ID), key)) match = false;
        if (notContains(request.queryParams(NAME), value)) match = false;
        if (match) {
            Map<String, String> map = new HashMap<>();
            map.put(ID, key);
            map.put(NAME, value);
            result.add(map);
        }
    }

    private boolean notContains(String param, String value) {
        return param != null && !StringUtils.containsIgnoreCase(value, param);
    }

    private boolean paramAndNoMatch(String param, Object value) {
        return param != null && !param.equals(value);
    }
}
