//package com.flash.service.managers.b2c.catalog;
//
//import com.fasterxml.jackson.core.JsonProcessingException;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
//import com.mongodb.BasicDBObject;
//import com.mongodb.DBObject;
//import com.mongodb.QueryBuilder;
//import com.flash.dto.response.catalogservice.*;
//import com.flash.service.Request;
//import com.flash.service.managers.RequestManager;
//import com.flash.service.utilities.database.MongoHelper;
//import org.apache.commons.collections4.CollectionUtils;
//
//import java.util.*;
//import java.util.stream.Collectors;
//
//public class CatalogManager extends RequestManager {
//
//    private final MongoHelper mongoHelperInstance;
//    private static final String DEFAULT_PREFERRED_LANGUAGE = "en";
//    private static final String DATABASE_NAME = "catalog_service_staging";
//
//    public CatalogManager(String requestId) {
//        super(requestId);
//        mongoHelperInstance = getMongoHelperInstance();
//    }
//
//    public String getExpectedResponse(Request r, Map<String, Object> params) throws JsonProcessingException {
//        String locale = r.getQueryParams().getOrDefault("preferredLanguage", DEFAULT_PREFERRED_LANGUAGE);
//        List<String> actions = Arrays.asList(params.get("actions").toString().split(","));
//        Map<String, Object> response = new HashMap<>();
//
//        if(actions.contains("coupons")) {
//            String q = params.entrySet().stream().filter(n -> n.getKey().startsWith("coupons_"))
//                    .map(m -> m.getKey().split("coupons_")[1] + "=" + m.getValue()).collect(Collectors.joining());
//            ActionResponse couponAction = getCouponAction(q, locale);
//            response.put(couponAction.getKey(), couponAction.getResponse());
//        }
//
//        if(actions.contains("properties")) {
//            String q = params.entrySet().stream().filter(n -> n.getKey().startsWith("properties_"))
//                    .map(m -> m.getKey().split("properties_")[1] + "=" + m.getValue()).collect(Collectors.joining());
//            ActionResponse propertyAction = getPropertyAction(q, locale);
//            response.put(propertyAction.getKey(), propertyAction.getResponse());
//        }
//
//        if(actions.contains("products")) {
//            String q = params.entrySet().stream().filter(n -> n.getKey().startsWith("products_"))
//                    .map(m -> m.getKey().split("products_")[1] + "=" + m.getValue()).collect(Collectors.joining());
//            ActionResponse productAction = getProductAction(q, locale);
//            response.put(productAction.getKey(), productAction.getResponse());
//        }
//
//        ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());
//        return mapper.writeValueAsString(response);
//    }
//
//    private BasicDBObject getQuery(String q) {
//        DBObject query;
//        String key = q.split("=")[0];
//        String value = q.split("=")[1];
//        // Supports only eq and in operator
//        if(q.contains(","))
//            query = new QueryBuilder().put(key).in(Arrays.asList(value.split(","))).get();
//        else
//            query = new QueryBuilder().put(key).is(value).get();
//        return (BasicDBObject) query;
//    }
//
//    private ActionResponse getCouponAction(String q, String locale) {
//        List<Coupon> coupons = new ArrayList<>();
//        if(!q.trim().isEmpty()) {
//            BasicDBObject query = getQuery(q);
//            coupons = mongoHelperInstance.getMongoDatabase( RequestManager.automation_service)
//                    .getCollection("coupons", Coupon.class)
//                    .find(query)
//                    .into(new ArrayList<>());
//        }
//
//        return getActionResponse("coupons", coupons);
//    }
//
//    private ActionResponse getPropertyAction(String q, String locale) {
//        Map<String, Object> propertyResponse = new HashMap<>();
//        if(!q.trim().isEmpty()) {
//            List<Property> properties = mongoHelperInstance.getMongoDatabase( RequestManager.automation_service)
//                    .getCollection("properties", Property.class)
//                    .find(getQuery(q))
//                    .into(new ArrayList<>());
//            for (Property property : properties)
//                propertyResponse.put(property.getKey(),
//                        property.getPropertyLocale().containsKey(locale) ? property.getPropertyLocale().get(locale) :
//                                property.getPropertyLocale().get(DEFAULT_PREFERRED_LANGUAGE));
//        }
//
//        return getActionResponse("properties", propertyResponse);
//    }
//
//    public ActionResponse getProductAction(String q, String locale) {
//        List<Product> products = new ArrayList<>();
//        if(!q.trim().isEmpty()) {
//            products = mongoHelperInstance.getMongoDatabase( RequestManager.automation_service)
//                    .getCollection("products", Product.class)
//                    .find(getQuery(q))
//                    .into(new ArrayList<>());
//
//            List<Product> linkedProductList = new ArrayList<>();
//            if(!locale.equalsIgnoreCase("en")) {
//                String expectedCategoryCd = locale.equalsIgnoreCase("en-au") ? "AUD" : "GBP";
//                List<String> linkedProductIds = new ArrayList<>();
//                for(Product product : products) {
//                    List<ProductLinking> productLinkings = product.getProductLinkings();
//                    for(ProductLinking productLinking : CollectionUtils.emptyIfNull(productLinkings))
//                        if(productLinking.getCategoryCd().equalsIgnoreCase(expectedCategoryCd))
//                            linkedProductIds.add(productLinking.getLinkedProductId());
//                }
//                BasicDBObject query = (BasicDBObject) new QueryBuilder().put("id").in(linkedProductIds).get();
//
//                linkedProductList = mongoHelperInstance.getMongoDatabase( RequestManager.automation_service)
//                        .getCollection("products", Product.class)
//                        .find(query)
//                        .into(new ArrayList<>());
//            }
//
//            if(linkedProductList.size() == products.size())
//                products = linkedProductList;
//
//        }
//
//        ProductMapper mapper = new ProductMapper();
//        List<ProductResponseDTO> response = products.stream().map(mapper::mapProduct)
//                .collect(Collectors.toList());
//
//        return getActionResponse("products", response);
//    }
//
//    private ActionResponse getActionResponse(String key, Object response) {
//        ActionResponse actionResponse = new ActionResponse();
//        actionResponse.setKey(key);
//        actionResponse.setResponse(response);
//        return actionResponse;
//    }
//
//}
