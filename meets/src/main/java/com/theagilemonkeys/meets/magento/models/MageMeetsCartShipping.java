package com.theagilemonkeys.meets.magento.models;

import com.google.api.client.util.Key;
import com.theagilemonkeys.meets.models.MeetsCart;
import com.theagilemonkeys.meets.utils.soap.Serializable;

/**
 * Android Meets SDK
 * Original work Copyright (c) 2014 [TheAgileMonkeys]
 *
 * @author Álvaro López Espinosa
 */
public class MageMeetsCartShipping extends Serializable.Object implements MeetsCart.Shipping {

    @Key private String code;
    @Key private String carrier;
    @Key private String carrier_title;
//    @Key private String method;
    @Key private String method_title;
    @Key private String method_description;
    @Key private double price;


    @Override
    public String getCarrierCode() {
        return carrier;
    }

    @Override
    public String getCarrierTitle() {
        return carrier_title;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public MeetsCart.Shipping setCode(String code) {
        this.code = code;
        return this;
    }

    @Override
    public String getTitle() {
        return method_title;
    }

    @Override
    public String getDescription() {
        return method_description;
    }

    @Override
    public double getPrice() {
        return price;
    }
}
