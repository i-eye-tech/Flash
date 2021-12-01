package com.flash.constants;

import com.flash.service.*;

public enum ValidationType {

    COMPARATOR,DBVALIDATOR,CATALOGTEST,CHECKOUT,SUBSCRIPTION_ENGINE,GENERICVALIDATOR;

    public Class getClassEntity(){
        switch (this){
            case COMPARATOR: return GenericTest.class;
            case DBVALIDATOR: return DbValidationTest.class;
            case GENERICVALIDATOR: return GenericValidationTest.class;
        }
        return null;
    }
}
