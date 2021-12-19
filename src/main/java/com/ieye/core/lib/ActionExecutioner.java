package com.ieye.core.lib;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.ieye.core.helper.RestHelper;
import com.ieye.core.lib.currenttest.CurrentTest;
import com.ieye.model.core.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public final class ActionExecutioner {

    @Autowired
    CurrentTest currentTest;

    @Autowired
    RestManager restManager;

    @Autowired
    RestHelper restHelper;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final ObjectNode emptyNode = objectMapper.createObjectNode();

    public void execute(ApiSpecification apiSpecification, Map<String, Action> actions) {
        actions.forEach((k, action) -> {
            if(action.isActive())
                sleep(action.getSleepInMs());
            Object response = getResponse(apiSpecification, action);
            if(action.getType().equals(ActionType.REST)) {
                HashMap<String, Object> m = new HashMap<>();
                if(response != null)
                    m.putAll((Map<String, Object>) response); // back compatibility
                m.put("response", response == null ? emptyNode : objectMapper.valueToTree(response));
                m.put("request", objectMapper.valueToTree(action.getRest()));
                ((ObjectNode) currentTest.getData()).putPOJO(k, m);
            } else {
                ((ObjectNode) currentTest.getData()).putPOJO(k, response);
            }
        });
    }

    private <T> T getResponse(ApiSpecification apiSpecification, Action action) {
        switch (action.getType()) {
            case REST:
                return getRestResponse(apiSpecification, action.getRest());
            default:
                return null;
        }
    }

    private <T> T getRestResponse(ApiSpecification apiSpecification, RestTemplate restTemplate) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            RestSpecification request = restManager.createRestSpecification(apiSpecification, restTemplate);
            return mapper.readValue(restHelper.execute(request).asString(), new TypeReference<>() {});
        } catch (Exception e) {
            log.error("{} - Exception in executing rest action {}, " + "nested exception is : {}",
                    currentTest.getRequestId(), restTemplate.toString(), e.getMessage());
            return null;
        }
    }

    private void sleep(int s) {
        try {
            Thread.sleep(s);
        } catch (InterruptedException ignore) {}
    }

}
