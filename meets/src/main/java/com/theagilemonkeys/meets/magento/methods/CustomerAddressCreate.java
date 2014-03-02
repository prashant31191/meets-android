package com.theagilemonkeys.meets.magento.methods;

import com.theagilemonkeys.meets.magento.SoapApiMethod;
import com.theagilemonkeys.meets.magento.models.MageMeetsAddress;

/**
 * Created by kloster on 30/09/13.
 */
public class CustomerAddressCreate extends SoapApiMethod {
    public CustomerAddressCreate() {
        super(MageMeetsAddress.class);
    }

    @Override
    protected void parseResponse(Object response, Object model) throws Exception {
        ((MageMeetsAddress) model).setId((Integer) response);
    }
}

