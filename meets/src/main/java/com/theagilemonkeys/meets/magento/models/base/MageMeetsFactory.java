package com.theagilemonkeys.meets.magento.models.base;

import com.theagilemonkeys.meets.magento.methods.Products;
import com.theagilemonkeys.meets.magento.models.MageMeetsAddress;
import com.theagilemonkeys.meets.magento.models.MageMeetsCart;
import com.theagilemonkeys.meets.magento.models.MageMeetsCartItem;
import com.theagilemonkeys.meets.magento.models.MageMeetsCartPayment;
import com.theagilemonkeys.meets.magento.models.MageMeetsCartShipping;
import com.theagilemonkeys.meets.magento.models.MageMeetsCategory;
import com.theagilemonkeys.meets.magento.models.MageMeetsCustomer;
import com.theagilemonkeys.meets.magento.models.MageMeetsProduct;
import com.theagilemonkeys.meets.magento.models.MageStockInfoList;
import com.theagilemonkeys.meets.models.MeetsAddress;
import com.theagilemonkeys.meets.models.MeetsCart;
import com.theagilemonkeys.meets.models.MeetsCategory;
import com.theagilemonkeys.meets.models.MeetsCustomer;
import com.theagilemonkeys.meets.models.MeetsProduct;
import com.theagilemonkeys.meets.models.MeetsStock;
import com.theagilemonkeys.meets.models.base.MeetsCollection;
import com.theagilemonkeys.meets.models.base.MeetsFactory;

import java.util.List;

/**
 * Created by kloster on 16/10/13.
 */
public class MageMeetsFactory extends MeetsFactory {
    @Override
    public MeetsProduct makeProduct() {
        return new MageMeetsProduct();
    }

    @Override
    public MeetsProduct makeProduct(int id) {
        return makeProduct().setId(id);
    }

    @Override
    public MeetsCollection<MeetsProduct> makeProductCollection() {
        return new MageMeetsCollection<MeetsProduct>(Products.class);
    }

    @Override
    public MeetsCategory makeCategory() {
        return new MageMeetsCategory();
    }

    @Override
    public MeetsCategory makeCategory(int id) {
        return makeCategory().setId(id);
    }

    @Override
    public MeetsCart makeCart() {
        return new MageMeetsCart();
    }

    @Override
    public MeetsCart makeCart(int id) {
        return makeCart().setId(id);
    }

    @Override
    public MeetsCart.Item makeCartItem() {
        return new MageMeetsCartItem();
    }

    @Override
    public MeetsCart.Item makeCartItem(MeetsProduct product) {
        return new MageMeetsCartItem().fillFromProduct(product);
    }

    @Override
    public MeetsCustomer makeCustomer() {
        return new MageMeetsCustomer();
    }

    @Override
    public MeetsCustomer makeCustomer(int id) {
        return makeCustomer().setId(id);
    }

    @Override
    public MeetsCollection<MeetsCustomer> makeCustomerCollection() {
        throw new UnsupportedOperationException("Still not implemented");
//        return new MageMeetsCollection<MeetsCustomer>(CustomerCustomerList.class);
    }

    @Override
    public MeetsAddress makeAddress() {
        return new MageMeetsAddress();
    }

    @Override
    public MeetsAddress makeAddress(int id) {
        return makeAddress().setId(id);
    }

    @Override
    public MeetsCart.Payment makeCartPayment() {
        return new MageMeetsCartPayment();
    }

    @Override
    public MeetsCart.Shipping makeCartShipping() {
        return new MageMeetsCartShipping();
    }

    @Override
    public MeetsStock.ItemList makeStockItemList() {
        return new MageStockInfoList();
    }

    @Override
    public MeetsStock.ItemList makeStockItemList(int id) {
        return makeStockItemList().setId(id);
    }

    @Override
    public MeetsStock.ItemList makeStockItemList(List<Integer> ids) {
        return makeStockItemList().setIds(ids);
    }
}
