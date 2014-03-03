package com.theagilemonkeys.meets.models.base;

import com.theagilemonkeys.meets.models.MeetsAddress;
import com.theagilemonkeys.meets.models.MeetsCart;
import com.theagilemonkeys.meets.models.MeetsCategory;
import com.theagilemonkeys.meets.models.MeetsCustomer;
import com.theagilemonkeys.meets.models.MeetsProduct;
import com.theagilemonkeys.meets.models.MeetsStock;

import java.util.List;

/**
 * Android Meets SDK
 * Original work Copyright (c) 2014 [TheAgileMonkeys]
 *
 * @author Álvaro López Espinosa
 */
public abstract class MeetsFactory {
    private static MeetsFactory instance;

    public static void setInstance(MeetsFactory meetsFactoryInstance){
        instance = meetsFactoryInstance;
    }

    public static MeetsFactory get(){
        return instance;
    }

    // "instance" must provide an implementation of thefollowing methods
    public abstract MeetsProduct makeProduct();
    public abstract MeetsProduct makeProduct(int id);
    public abstract MeetsCollection<MeetsProduct> makeProductCollection();
    public abstract MeetsCategory makeCategory();
    public abstract MeetsCategory makeCategory(int id);
    public abstract MeetsCustomer makeCustomer();
    public abstract MeetsCustomer makeCustomer(int id);
    /**
     * TODO
     * @return
     */
    public abstract MeetsCollection<MeetsCustomer> makeCustomerCollection();
    public abstract MeetsAddress makeAddress();
    public abstract MeetsAddress makeAddress(int id);
    public abstract MeetsCart makeCart();
    public abstract MeetsCart makeCart(int id);
    public abstract MeetsCart.Item makeCartItem();
    public abstract MeetsCart.Item makeCartItem(MeetsProduct product);
    public abstract MeetsCart.Payment makeCartPayment();
    public abstract MeetsCart.Shipping makeCartShipping();
    public abstract MeetsStock.ItemList makeStockItemList();
    public abstract MeetsStock.ItemList makeStockItemList(int id);
    public abstract MeetsStock.ItemList makeStockItemList(List<Integer> ids);
}
