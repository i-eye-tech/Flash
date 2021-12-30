package com.ieye.recur;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class TokensManager {

    private static final Map<String, Map<String, String>> tokens = new HashMap<>();

    public void init(String requestId) {
        tokens.put(requestId, new HashMap<>());
    }

    public void addToken(String requestId, String key, String value) {
        synchronized (this) {
            if(tokens.containsKey(requestId)) {
                log.debug("Adding token {} in request {}", key, requestId);
                tokens.get(requestId).put(key, value);
            }
        }
    }

    public String getToken(String requestId, String key) {
        return tokens.containsKey(requestId) ? tokens.get(requestId).getOrDefault(key, null) : null;
    }

    public void delete(String requestId) {
        tokens.remove(requestId);
    }

    public void delete(String requestId, String key) {
        if(tokens.containsKey(requestId))
            tokens.get(requestId).remove(key);
    }

}
