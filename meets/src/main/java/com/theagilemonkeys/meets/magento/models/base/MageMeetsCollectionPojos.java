package com.theagilemonkeys.meets.magento.models.base;

import com.theagilemonkeys.meets.magento.models.MageMeetsAddress;
import com.theagilemonkeys.meets.magento.models.MageMeetsCartItem;
import com.theagilemonkeys.meets.magento.models.MageMeetsCartPayment;
import com.theagilemonkeys.meets.magento.models.MageMeetsCartShipping;
import com.theagilemonkeys.meets.magento.models.MageMeetsCategory;
import com.theagilemonkeys.meets.magento.models.MageMeetsCustomer;
import com.theagilemonkeys.meets.magento.models.MageMeetsProduct;
import com.theagilemonkeys.meets.magento.models.MageStockItem;

import java.util.ArrayList;
import java.util.LinkedHashMap;

/**
 * Created by kloster on 16/10/13.
 */
public class MageMeetsCollectionPojos {
    public static class ProductsMap extends LinkedHashMap<Integer, MageMeetsProduct> {}
    public static class ProductsArray extends ArrayList<MageMeetsProduct> {}
    public static class Categories extends ArrayList<MageMeetsCategory> {}
    public static class Customers extends ArrayList<MageMeetsCustomer> {}
    public static class ShippingMethods extends ArrayList<MageMeetsCartShipping> {}
    public static class PaymentMethods extends ArrayList<MageMeetsCartPayment> {}
    public static class CartItems extends ArrayList<MageMeetsCartItem> {}
    public static class StockInfos extends ArrayList<MageStockItem> {}
    public static class Addresses extends ArrayList<MageMeetsAddress> {}
}
