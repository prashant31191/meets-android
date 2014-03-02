package com.theagilemonkeys.meets.magento.models;

import com.google.api.client.util.Key;
import com.theagilemonkeys.meets.ApiMethodModelHelper;
import com.theagilemonkeys.meets.magento.methods.ShoppingCartCreate;
import com.theagilemonkeys.meets.magento.methods.ShoppingCartCustomerAddresses;
import com.theagilemonkeys.meets.magento.methods.ShoppingCartCustomerSet;
import com.theagilemonkeys.meets.magento.methods.ShoppingCartInfo;
import com.theagilemonkeys.meets.magento.methods.ShoppingCartOrder;
import com.theagilemonkeys.meets.magento.methods.ShoppingCartPaymentList;
import com.theagilemonkeys.meets.magento.methods.ShoppingCartPaymentMethod;
import com.theagilemonkeys.meets.magento.methods.ShoppingCartProductAdd;
import com.theagilemonkeys.meets.magento.methods.ShoppingCartProductRemove;
import com.theagilemonkeys.meets.magento.methods.ShoppingCartShippingList;
import com.theagilemonkeys.meets.magento.methods.ShoppingCartShippingMethod;
import com.theagilemonkeys.meets.magento.models.base.MageMeetsCollectionPojos;
import com.theagilemonkeys.meets.magento.models.base.MageMeetsModel;
import com.theagilemonkeys.meets.models.MeetsAddress;
import com.theagilemonkeys.meets.models.MeetsCart;
import com.theagilemonkeys.meets.models.MeetsCustomer;
import com.theagilemonkeys.meets.models.MeetsProduct;
import com.theagilemonkeys.meets.models.base.MeetsFactory;
import com.theagilemonkeys.meets.utils.soap.Serializable;
import com.theagilemonkeys.meets.utils.soap.SoapParser;

import org.jdeferred.DoneCallback;
import org.jdeferred.FailCallback;
import org.jdeferred.Promise;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by kloster on 3/10/13.
 */
public class MageMeetsCart extends MageMeetsModel<MeetsCart> implements MeetsCart {

    @Key private int quote_id;
    @Key private double items_count = 0;
    @Key String checkout_method;
    @Key String customer_id;
    @Key private double items_qty = 0;
    @Key private double subtotal = 0;
    @Key private double grand_total = 0;
    @Key
    @SoapParser.ListType(MageMeetsCollectionPojos.CartItems.class)
    private List<Item> items = new ArrayList<Item>();

    private MageMeetsCollectionPojos.ShippingMethods shippingMethods;
    private MageMeetsCollectionPojos.PaymentMethods paymentMethods;
    private String lastOrderId;

    {
        //Disable cache for the entire model operations
        setModelCache(false);
    }


    @Override
    public MeetsCart fetch() {
        ApiMethodModelHelper.DelayedParams params = new ApiMethodModelHelper.DelayedParams() {
            @Override
            public Map<String, Object> buildParams() {
                Map<String, Object> params = new HashMap<String, Object>();
                params.put("quoteId", quote_id);
                return params;
            }
        };

        pushMethod(new ShoppingCartInfo(), params).always(updateAndTrigger);
        return this;
    }

    @Override
    public MeetsCart setId(int id) {
        quote_id = id;
        return this;
    }

    @Override
    public int getId() {
        return quote_id;
    }

    @Override
    public MeetsCart create() {
        pushMethod(new ShoppingCartCreate()).always(updateAndTrigger);
        nextWaitForPrevious();
        return this;
    }

    @Override
    public MeetsCart attachCustomer(final MeetsCustomer customer) {
        ((MageMeetsCustomer) customer).setMode(MageMeetsCustomer.MODE_CUSTOMER);
        return internalAttachCustomer(customer);
    }

    @Override
    public MeetsCart attachCustomerAsGuest(MeetsCustomer customer) {
        ((MageMeetsCustomer) customer).setMode(MageMeetsCustomer.MODE_GUEST);
        return internalAttachCustomer(customer);
    }

    private MeetsCart internalAttachCustomer(final MeetsCustomer customer) {
        ApiMethodModelHelper.DelayedParams params = new ApiMethodModelHelper.DelayedParams() {
            @Override
            public Map<String, Object> buildParams() {
                Map<String, Object> params = new HashMap<String, Object>();
                params.put("quoteId", quote_id);
                params.put("customer", customer);
                return params;
            }
        };

        pushMethod(new ShoppingCartCustomerSet(), params).always(onlyTrigger);
        return this;
    }

    @Override
    public MeetsCart attachAddresses(final MeetsAddress billingAddress, final MeetsAddress shippingAddress) {
        ((MageMeetsAddress) billingAddress).setMode(MageMeetsAddress.MODE_BILLING);
        ((MageMeetsAddress) shippingAddress).setMode(MageMeetsAddress.MODE_SHIPPING);

        ApiMethodModelHelper.DelayedParams params = new ApiMethodModelHelper.DelayedParams() {
            @Override
            public Map<String, Object> buildParams() {
                Serializable.List<MageMeetsAddress> addresses = new Serializable.List<MageMeetsAddress>();
                addresses.add((MageMeetsAddress) billingAddress);
                addresses.add((MageMeetsAddress) shippingAddress);

                Map<String, Object> params = new HashMap<String, Object>();
                params.put("quoteId", quote_id);
                params.put("customer", addresses);
                return params;
            }
        };

        pushMethod(new ShoppingCartCustomerAddresses(), params).always(onlyTrigger);
        return this;
    }

    @Override
    public MeetsCart attachShippingMethod(final Shipping shipping) {
        ApiMethodModelHelper.DelayedParams params = new ApiMethodModelHelper.DelayedParams() {
            @Override
            public Map<String, Object> buildParams() {
                Map<String, Object> params = new HashMap<String, Object>();
                params.put("quoteId", quote_id);
                params.put("method", shipping.getCode());
                return params;
            }
        };

        pushMethod(new ShoppingCartShippingMethod(), params).always(onlyTrigger);
        return this;
    }

    @Override
    public MeetsCart attachPaymentMethod(final Payment paymentMethod) {
        ApiMethodModelHelper.DelayedParams params = new ApiMethodModelHelper.DelayedParams() {
            @Override
            public Map<String, Object> buildParams() {
                Map<String, Object> params = new HashMap<String, Object>();
                params.put("quoteId", quote_id);
                params.put("method",paymentMethod);
                return params;
            }
        };

        pushMethod(new ShoppingCartPaymentMethod(), params).always(onlyTrigger);
        return this;
    }

    @Override
    public MeetsCart order() {
        ApiMethodModelHelper.DelayedParams params = new ApiMethodModelHelper.DelayedParams() {
            @Override
            public Map<String, Object> buildParams() {
                Map<String, Object> params = new HashMap<String, Object>();
                params.put("quoteId", quote_id);
                return params;
            }
        };

        pushMethod(new ShoppingCartOrder(), params)
                .done(new DoneCallback() {
                    @Override
                    public void onDone(Object result) {
                        lastOrderId = (String) result;
                    }
                })
                .always(onlyTrigger);
        return this;
    }

    @Override
    public MeetsCart addItem(final MeetsProduct product, final double quantity) {
        return addItems(Arrays.asList(MeetsFactory.get().makeCartItem(product).setQuantity(quantity)));
    }

    @Override
    public MeetsCart addItem(Item item) {
        return addItems(Arrays.asList(item));
    }

    @Override
    public MeetsCart addItems(List<MeetsProduct> products, List<Double> quantities) {
        List<Item> items = new ArrayList<Item>();
        int productsLength = products.size();
        int quantitiesLength = quantities.size();
        for(int i = 0; i < productsLength; ++i) {
            MeetsProduct product = products.get(i);
            double quantity = quantities.get(i % quantitiesLength);
            items.add(MeetsFactory.get().makeCartItem(product).setQuantity(quantity));
        }
        return addItems(items);
    }

    @Override
    public MeetsCart addItems(final List<Item> items) {
        localAddItems(items);

        ApiMethodModelHelper.DelayedParams params = new ApiMethodModelHelper.DelayedParams() {
            @Override
            public Map<String, Object> buildParams() {
                // Create a list with the product we want to add to cart
                Serializable.List<Item> cartItemsToSend = new Serializable.List<Item>();
                cartItemsToSend.addAll(items);
                // Create the params and call the method
                Map<String, Object> params = new HashMap<String, Object>();
                params.put("quoteId", quote_id);
                params.put("products", cartItemsToSend);
                return params;
            }
        };

        pushMethod(new ShoppingCartProductAdd(), params)
                .fail(new FailCallback() {
                    @Override
                    public void onFail(Object result) {
                        localRemoveItems(items);
                    }
                })
                .always(onlyTrigger);
        return null;
    }

    private void localAddItems(List<Item> items) {
        for(Item item : items) localAddItem(item);
    }

    private void localAddItem(Item item) {
        // Update the local data of cart products
        Item localCartItem = null;
        double qty = item.getQuantity();

        for(Item localItem : items){
            if( localItem.getProductId() == item.getProductId() ){
                localItem.incQuantity(qty);
                localCartItem = localItem;
                break;
            }
        }
        if ( localCartItem == null){
            localCartItem = item;
            items.add(localCartItem);
            ++items_count;
        }

        grand_total += localCartItem.getPrice() * qty;
        subtotal += localCartItem.getPrice() * qty;
        items_qty +=  qty;
    }

    @Override
    public MeetsCart removeItem(int productId, double quantity) {
        return removeItems(Arrays.asList(productId), Arrays.asList(quantity));
    }

    @Override
    public MeetsCart removeItem(int productId) {
        return removeItems(Arrays.asList(productId), Collections.<Double>emptyList());
    }

    @Override
    public MeetsCart removeItems(List<Integer> productIds) {
        return removeItems(productIds, Collections.<Double>emptyList());
    }

    @Override
    public MeetsCart removeItems(List<Integer> productIds, List<Double> quantities) {
        List<Item> items = new ArrayList<Item>();
        int productIdsLength = productIds.size();
        int quantitiesLength = quantities.size();
        for(int i = 0; i < productIdsLength; ++i) {
            int productId = productIds.get(i);
            double quantity = Integer.MAX_VALUE;
            if (quantitiesLength > 0)
                quantity = quantities.get(i % quantitiesLength);

            items.add(MeetsFactory.get().makeCartItem()
                    .setProductId(productId)
                    .setQuantity(quantity));
        }
        return internalRemoveItems(items);
    }


    private MeetsCart internalRemoveItems(List<Item> items) {
        final List<Item> removedItems = localRemoveItems(items);

        if( ! removedItems.isEmpty() ){
            ApiMethodModelHelper.DelayedParams params = new ApiMethodModelHelper.DelayedParams() {
                @Override
                public Map<String, Object> buildParams() {
                    // Create a list with the product we want to add to cart
                    Serializable.List<Item> cartItemsToSend = new Serializable.List<Item>();
                    cartItemsToSend.addAll(removedItems);
                    // Create the params and call the method
                    Map<String, Object> params = new HashMap<String, Object>();
                    params.put("quoteId", quote_id);
                    params.put("products", cartItemsToSend);
                    return params;
                }
            };

            pushMethod(new ShoppingCartProductRemove(), params)
                    .fail(new FailCallback() {
                        @Override
                        public void onFail(Object result) {
                            localAddItems(removedItems);
                        }
                    })
                    .always(onlyTrigger);
        }
        else {
            onlyTrigger.onAlways(Promise.State.RESOLVED, this, null);
        }
        return this;
    }

    private List<Item> localRemoveItems(List<Item> items) {
        List<Item> removedItems = new ArrayList<Item>();
        for(Item item : items) {
            Item removedItem = localRemoveItem(item);
            if (removedItem != null) removedItems.add(item);
        }
        return removedItems;
    }

    private Item localRemoveItem(Item itemToRemove) {
        Item localCartItem = null;

        for(Item item : items){
            if( item.getProductId() == itemToRemove.getProductId() ){
                localCartItem = item;
                break;
            }
        }
        if(localCartItem == null) return null;

        double realQty = Math.min(localCartItem.getQuantity(), itemToRemove.getQuantity());
        localCartItem.incQuantity(-realQty);
        if (localCartItem.getQuantity() <= 0){
            items.remove(localCartItem);
            --items_count;
        }

        grand_total -= localCartItem.getPrice() * realQty;
        subtotal -= localCartItem.getPrice() * realQty;
        items_qty -= realQty;

        return localCartItem;
    }

    @Override
    public List<Item> getItems() {
        return items;
    }

    @Override
    public double getItemsTotalQuantity() {
        return items_qty;
    }

    @Override
    public double getSubtotal() {
        return subtotal;
    }

    @Override
    public double getTotal() {
        return grand_total;
    }

    @Override
    public String getOrderId() {
        return lastOrderId;
    }

    @Override
    public List<Shipping> getShippingMethods() {
        return new ArrayList<Shipping>(shippingMethods);
    }

    @Override
    public List<Payment> getPaymentMethods() {
        return new ArrayList<Payment>(paymentMethods);
    }

    @Override
    public MeetsCart fetchShippingMethods() {
        ApiMethodModelHelper.DelayedParams params = new ApiMethodModelHelper.DelayedParams() {
            @Override
            public Map<String, Object> buildParams() {
                Map<String, Object> params = new HashMap<String, Object>();
                params.put("quoteId", quote_id);
                return params;
            }
        };

        forceNextCacheToBe(true);
        pushMethod(new ShoppingCartShippingList(), params)
                .done(new DoneCallback() {
                    @Override
                    public void onDone(Object result) {
                        shippingMethods = (MageMeetsCollectionPojos.ShippingMethods) result;
                    }
                })
                .always(onlyTrigger);
        return this;
    }

    @Override
    public MeetsCart fetchPaymentMethods() {
        ApiMethodModelHelper.DelayedParams params = new ApiMethodModelHelper.DelayedParams() {
            @Override
            public Map<String, Object> buildParams() {
                Map<String, Object> params = new HashMap<String, Object>();
                params.put("quoteId", quote_id);
                return params;
            }
        };

        forceNextCacheToBe(true);
        pushMethod(new ShoppingCartPaymentList(), params)
                .done(new DoneCallback() {
                    @Override
                    public void onDone(Object result) {
                        paymentMethods = (MageMeetsCollectionPojos.PaymentMethods) result;
                    }
                })
                .always(onlyTrigger);
        return this;
    }
}
