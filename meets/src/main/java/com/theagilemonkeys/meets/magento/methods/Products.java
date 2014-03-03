package com.theagilemonkeys.meets.magento.methods;

import com.theagilemonkeys.meets.magento.RestApiMethod;
import com.theagilemonkeys.meets.magento.models.MageMeetsProduct;
import com.theagilemonkeys.meets.magento.models.base.MageMeetsCollectionPojos;

/**
 * Android Meets SDK
 * Original work Copyright (c) 2014 [TheAgileMonkeys]
 *
 * @author Álvaro López Espinosa
 */
public class Products extends RestApiMethod {
    public Products() {
        super(MageMeetsProduct.class);
    }
    public Products(boolean forCollection) {
        super(MageMeetsCollectionPojos.ProductsMap.class);
    }
}

