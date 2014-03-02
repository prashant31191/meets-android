package com.theagilemonkeys.meets.magento.methods;

import com.theagilemonkeys.meets.magento.RestApiMethod;
import com.theagilemonkeys.meets.magento.models.MageMeetsProduct;
import com.theagilemonkeys.meets.magento.models.base.MageMeetsCollectionPojos;

/**
 * Created by kloster on 30/09/13.
 */
public class Products extends RestApiMethod {
    public Products() {
        super(MageMeetsProduct.class);
    }
    public Products(boolean forCollection) {
        super(MageMeetsCollectionPojos.ProductsMap.class);
    }
}

