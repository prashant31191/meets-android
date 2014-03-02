package com.theagilemonkeys.meets.magento.methods;

import com.theagilemonkeys.meets.magento.SoapApiMethod;
import com.theagilemonkeys.meets.magento.models.MageMeetsCart;

/**
 * Created by kloster on 30/09/13.
 */
public class ShoppingCartCreate extends SoapApiMethod {
    public ShoppingCartCreate() {
        super(MageMeetsCart.class);
    }

    @Override
    protected void parseResponse(Object response, Object model) throws Exception {
        ((MageMeetsCart) model).setId((Integer) response);
    }
}

