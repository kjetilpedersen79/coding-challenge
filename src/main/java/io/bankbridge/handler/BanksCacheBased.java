package io.bankbridge.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.bankbridge.model.BankModel;
import io.bankbridge.model.BankModelList;
import org.ehcache.Cache;
import org.ehcache.CacheManager;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.CacheManagerBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;
import spark.Request;
import spark.Response;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// avoids static methods, a static cache manager could be difficult to control - BanksCacheBased can be a global resource if needed
public class BanksCacheBased implements BanksCallable {
    private CacheManager cacheManager; // can be private

    // splits init cacheManager from data load
    public BanksCacheBased() {
        cacheManager = CacheManagerBuilder
                .newCacheManagerBuilder().withCache("banks", CacheConfigurationBuilder
                        .newCacheConfigurationBuilder(String.class, String.class, ResourcePoolsBuilder.heap(10)))
                .build();
        cacheManager.init();
    }

    // load from default file
    @Override
    public void init() throws IOException { // replaced catch and throw Exception with more accurate exception
        init("banks-v1.json");
    }

    // load from specific resource file
    public void init(String resource) throws IOException {
        // model.bic and model.name are Strings, can use generics instead of unchecked cast
        Cache<String, String> cache = cacheManager.getCache("banks", String.class, String.class);
        BankModelList models = new ObjectMapper().readValue(
                Thread.currentThread().getContextClassLoader().getResource(resource), BankModelList.class);
        for (BankModel model : models.banks) {
            cache.put(model.bic, model.name);
        }
    }

    @Override
    public String handle(Request request, Response response) {
        List<Map> result = new ArrayList<>();
        cacheManager.getCache("banks", String.class, String.class).forEach(entry -> {
            /* use generics instead of unchecked cast */
            Map<String, String> map = new HashMap<>();
            map.put("id", entry.getKey());
            map.put("name", entry.getValue());
            result.add(map);
        });
        try {
            return new ObjectMapper().writeValueAsString(result);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error while processing request");
        }
    }

    // only expose the cache and not cache manager to avoid illegal manipulation
    public Cache<String, String> getCache() {
        return cacheManager.getCache("banks", String.class, String.class);
    }

    public void close() {
        cacheManager.close();
    }

}
