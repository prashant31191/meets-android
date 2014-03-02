package com.theagilemonkeys.meets.magento.methods;

import com.theagilemonkeys.meets.magento.SoapApiMethod;
import com.theagilemonkeys.meets.magento.models.base.MageMeetsCollectionPojos;

/**
 * Created by kloster on 30/09/13.
 */
public class CustomerAddressList extends SoapApiMethod {
    public CustomerAddressList() {
        super(MageMeetsCollectionPojos.Addresses.class);
    }
}

