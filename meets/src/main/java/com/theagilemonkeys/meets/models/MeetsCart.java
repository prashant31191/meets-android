package com.theagilemonkeys.meets.models;

import com.theagilemonkeys.meets.models.base.MeetsModel;

import java.io.Serializable;
import java.util.List;

/**
 * Android Meets SDK
 * Original work Copyright (c) 2014 [TheAgileMonkeys]
 *
 * @author Álvaro López Espinosa
 */
public interface MeetsCart extends MeetsModel<MeetsCart> {
    List<Item> getItems();
    List<Shipping> getShippingMethods();
    List<Payment> getPaymentMethods();
    double getItemsTotalQuantity();
    double getSubtotal();
    double getTotal();
    String getOrderId();

    MeetsCart create();

    MeetsCart addItem(MeetsProduct product, double quantity);
    MeetsCart addItem(Item item);
    MeetsCart addItems(List<MeetsProduct> products, List<Double> quantities);
    MeetsCart addItems(List<Item> items);

    MeetsCart removeItem(int productId, double quantity);
    /**
     * Remove all product units from the cart
     * @param productId
     * @return
     */
    MeetsCart removeItem(int productId);
    MeetsCart removeItems(List<Integer> productIds, List<Double> quantities);
    MeetsCart removeItems(List<Integer> productIds);

    MeetsCart attachCustomer(MeetsCustomer customer);
    MeetsCart attachCustomerAsGuest(MeetsCustomer customer);
    MeetsCart attachAddresses(MeetsAddress billingAddress, MeetsAddress shippingAddress);
    MeetsCart attachShippingMethod(Shipping shipping);
    MeetsCart attachPaymentMethod(Payment payment);
    MeetsCart order();

    MeetsCart fetchShippingMethods();
    MeetsCart fetchPaymentMethods();

    interface Item extends MeetsModel<Item> {
        Item fillFromProduct(MeetsProduct product);
        Item setQuantity(double quantity);
        Item incQuantity(double quantityIncrement);
        double getQuantity();
        String getName();
        String getDescription();
        double getPrice();
        int getProductId();
        String getProductSku();
        MeetsProduct getRelatedProduct();

        Item setProductId(int productId);

        Item fetchRelatedProduct();
    }

    interface Payment extends Serializable {
        String getTitle();
        String getCode();
        Payment setCode(String methodCode);
        Payment setPoNumber(String poNumber);
        Payment setCcCid(String ccCid);
        Payment setCcOwner(String ccOwner);
        Payment setCcNumber(String ccNumber);
        Payment setCcType(String ccType);
        Payment setCcExpYear(String ccExpYear);
        Payment setCcExpMonth(String ccExpMonth);
        Payment setPaymillToken(String token);
    }

    interface Shipping extends Serializable {
        String getCarrierCode();
        String getCarrierTitle();
        String getCode();
        Shipping setCode(String code);
        String getTitle();
        String getDescription();
        double getPrice();
    }
}
