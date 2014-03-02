package com.theagilemonkeys.meets.magento.methods;

import com.theagilemonkeys.meets.magento.SoapApiMethod;
import com.theagilemonkeys.meets.magento.models.MageMeetsCustomer;

/**
 * Created by kloster on 30/09/13.
 */
public class CustomerCustomerCreate extends SoapApiMethod {
    public CustomerCustomerCreate() {
        super(MageMeetsCustomer.class);
    }

    @Override
    protected void parseResponse(Object response, Object model) throws Exception {
        ((MageMeetsCustomer) model).setId((Integer) response);
    }
}

