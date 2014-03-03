package com.theagilemonkeys.meets.magento.models;

import com.google.api.client.util.Key;
import com.theagilemonkeys.meets.models.MeetsStock;
import com.theagilemonkeys.meets.utils.soap.Serializable;

/**
 * Android Meets SDK
 * Original work Copyright (c) 2014 [TheAgileMonkeys]
 *
 * @author Álvaro López Espinosa
 */
public class MageStockItem extends Serializable.Object implements MeetsStock.Item {


    @Key private String product_id;
    @Key private String sku;
    @Key private String qty;
    @Key private String is_in_stock;

    @Override
    public int getProductId() {
        return Integer.parseInt(product_id);
    }

    @Override
    public String getProductSku() {
        return sku;
    }

    @Override
    public boolean isInStock() {
        return "1".equals(is_in_stock);
    }

    @Override
    public double getQuantity() {
        return Double.parseDouble(qty);
    }
}
