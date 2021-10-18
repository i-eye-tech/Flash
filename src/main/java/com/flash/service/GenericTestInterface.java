package com.flash.service;

import com.flash.mongo.model.RequestDao;

public interface GenericTestInterface {

    public void runner(RequestDao requestDao,String mongoUri);
}
