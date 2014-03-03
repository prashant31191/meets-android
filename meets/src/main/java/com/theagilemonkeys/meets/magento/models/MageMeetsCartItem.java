package com.theagilemonkeys.meets.magento.models;

import com.google.api.client.util.Key;
import com.theagilemonkeys.meets.ApiMethodModelHelper;
import com.theagilemonkeys.meets.magento.methods.Products;
import com.theagilemonkeys.meets.magento.models.base.MageMeetsModel;
import com.theagilemonkeys.meets.models.MeetsCart;
import com.theagilemonkeys.meets.models.MeetsProduct;

import org.jdeferred.DoneCallback;

import java.util.Arrays;
import java.util.List;

/**
 * Android Meets SDK
 * Original work Copyright (c) 2014 [TheAgileMonkeys]
 *
 * @author Álvaro López Espinosa
 */
public class MageMeetsCartItem extends MageMeetsModel<MeetsCart.Item> implements MeetsCart.Item {

    @Key private String product_id;
    @Key private String sku;
    @Key private double qty = 1;
    @Key private String name;
    @Key private String description;
    @Key private double price;

    private MeetsProduct relatedProduct;

    @Override
    public MeetsCart.Item fillFromProduct(MeetsProduct product) {
        product_id = String.valueOf(product.getId());
        name = product.getName();
        description = product.getDescription();
        price = product.getPrice();
        relatedProduct = product;
        return this;
    }

    @Override
    public MeetsCart.Item setQuantity(double quantity) {
        qty = quantity;
        if (qty < 0) qty = 0;
        return this;
    }

    @Override
    public MeetsCart.Item incQuantity(double quantityIncrement) {
        qty += quantityIncrement;
        if (qty < 0) qty = 0;
        return this;
    }

    @Override
    public double getQuantity() {
        return qty;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public double getPrice() {
        return price;
    }

    @Override
    public int getProductId() {
        return Integer.parseInt(product_id);
    }

    @Override
    public String getProductSku() {
        return sku;
    }

    @Override
    public MeetsCart.Item setProductId(int productId) {
        product_id = String.valueOf(productId);
        return this;
    }

    @Override
    public MeetsProduct getRelatedProduct() {
        return relatedProduct;
    }


    @Override
    public MeetsCart.Item fetchRelatedProduct() {
        ApiMethodModelHelper.DelayedParams params = new ApiMethodModelHelper.DelayedParams() {
            @Override
            public List<String> buildUrlExtraSegments() {
                return Arrays.asList(String.valueOf(getProductId()));
            }
        };

        pushMethod(new Products(), params)
                .done(new DoneCallback() {
                    @Override
                    public void onDone(Object result) {
                        relatedProduct = (MeetsProduct) result;
                    }
                })
                .always(onlyTrigger);
        return this;
    }

    @Override
    public MeetsCart.Item fetch() {
        throw new UnsupportedOperationException("Cart items can not be fetched individually. Work with them through MeetsCart");
    }

    /**
     * Alias of {@link #setProductId(int)}
     * @param productId Product id
     * @return This object
     */
    @Override
    public MeetsCart.Item setId(int productId) {
        return setProductId(productId);
    }

    /**
     * Alias of {@link #getProductId()}
     * @return Product id
     */
    @Override
    public int getId() {
        return getProductId();
    }
}
