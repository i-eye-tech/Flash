package com.flash.mongo.repositories;

//@Repository
public interface TestDataRepository<T> {

    public T findByTestTypeAndCollectionName(String testType,Class<T> entityClass, String collectionName);

}
