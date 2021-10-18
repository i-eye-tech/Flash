//package com.flash.service.managers.subscriptionservice;
//
//import com.fasterxml.jackson.core.JsonProcessingException;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.flash.dto.request.subscriptionservice.CheckoutApiRequest;
//import com.flash.mongo.model.ApiSpecification;
//import com.flash.mongo.model.TestDataModel;
//import com.flash.service.managers.RequestManager;
//import com.flash.service.utilities.KafkaHelper;
//import com.stripe.Stripe;
//import com.stripe.exception.StripeException;
//import com.stripe.model.Subscription;
//import io.restassured.response.Response;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import java.util.List;
//import java.util.Map;
//import java.util.UUID;
//
//public class CheckoutApiManager extends RequestManager {
//
//    private static final Logger logger = LoggerFactory.getLogger(CheckoutApiManager.class);
//
//    public CheckoutApiManager(String requestId) {
//        super(requestId);
//    }
//
//    public boolean validateCheckoutApi(TestDataModel testDataModel, ApiSpecification apiSpecification, Map<String, Object> resultant) {
//        CheckoutApiRequest checkoutApiRequest = null;
//        ObjectMapper mapper = new ObjectMapper();
//        try {
//            checkoutApiRequest = mapper.readValue(testDataModel.getRequestBody(), CheckoutApiRequest.class);
//            if (resultant != null && resultant.containsKey("tokenApi") && ((Map<String, Object>) resultant.get("tokenApi")).containsKey("data"))
//                checkoutApiRequest.setToken(((Map<String, Object>) resultant.get("tokenApi")).get("data").toString());
//            if (!"Empty".equalsIgnoreCase(checkoutApiRequest.getOrderId())) {
//                checkoutApiRequest.setOrderId(UUID.randomUUID().toString());
//            } else {
//                checkoutApiRequest.setOrderId(null);
//            }
//            testDataModel.setRequestBody(mapper.writeValueAsString(checkoutApiRequest));
//        } catch (JsonProcessingException e) {
//            e.printStackTrace();
//        }
//        Response response = execute(createRequest(apiSpecification, testDataModel));
//        return testDataModel.getTestMeta().getStatusCode() == 200 ? response.as(Map.class).get("message").toString().equals(testDataModel.getTestMeta().getResponseMessage()) : testDataModel.getTestMeta().getStatusCode().equals(response.getStatusCode());
//    }
//
//
//    public boolean validateSubAtStripeBackend(List<Map<String, Object>> stripeSubData, List<Map<String, Object>> stripeSubDataPostRequest, boolean idempotent) {
//        ObjectMapper mapper = new ObjectMapper();
//        try {
//            return idempotent ? compareJson(mapper.writeValueAsString(stripeSubData), mapper.writeValueAsString(stripeSubDataPostRequest)) : validateSubAtStripeBackend(stripeSubDataPostRequest);
//        } catch (JsonProcessingException e) {
//            e.printStackTrace();
//        }
//        return false;
//    }
//
//    public boolean validateSubAtStripeBackend(List<Map<String, Object>> stripeSubDataPostRequest) {
//        return stripeSubDataPostRequest.size() == 1;
//    }
//
//
//    public Subscription getStripeSubscription(String id, String user) throws StripeException {
//        Stripe.apiKey = "sk_test_4eC39HqLyjWDarjtT1zdp7dc";
//        return Subscription.retrieve("sub_AgOem0qdyCowxn");
//    }
//
//    public boolean subAtSubscriptionService(List<Map<String, Object>> subData, List<Map<String, Object>> subDataPostRequest, boolean idempotent) {
//        ObjectMapper mapper = new ObjectMapper();
//        try {
//            return idempotent ? compareJson(mapper.writeValueAsString(subData), mapper.writeValueAsString(subDataPostRequest)) : validateSubAtStripeBackend(subDataPostRequest);
//        } catch (JsonProcessingException e) {
//            e.printStackTrace();
//        }
//        return false;
//    }
//
//    public boolean subAtSubscriptionService(List<Map<String, Object>> subDataPostRequest) {
//        return subDataPostRequest.size() == 1;
//    }
//
//    public boolean validateSubscriptionApi(TestDataModel testDataModel, ApiSpecification apiSpecification) throws JsonProcessingException {
//        Response rp = execute(createRequest(apiSpecification, testDataModel));
//        Map subReponse = rp.as(Map.class);
//        ObjectMapper mapper = new ObjectMapper();
//        String data = KafkaHelper.getInstance("subscription-test",reportHelper).poll("subscriptions-topic").stream().filter(rec-> {
//            try {
//                return mapper.readValue(rec,Map.class).get("id").equals(mapper.readValue(mapper.writeValueAsString(subReponse.get("data")),Map.class).get("id"));
//            } catch (JsonProcessingException e) {
//                e.printStackTrace();
//            }
//            return false;
//        }).findFirst().orElse("{}");
//
//        return testDataModel.getTestMeta().getStatusCode()==200 ? compareJson(data, mapper.writeValueAsString(subReponse.get("data"))):compareJson(testDataModel.getExpectedJsonResponse() ,rp.asString());
//    }
//
//}
