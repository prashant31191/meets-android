package com.theagilemonkeys.meets.magento.models;

import com.google.api.client.util.Key;
import com.theagilemonkeys.meets.ApiMethodModelHelper;
import com.theagilemonkeys.meets.Meets;
import com.theagilemonkeys.meets.magento.methods.CustomerAddressCreate;
import com.theagilemonkeys.meets.magento.methods.CustomerAddressDelete;
import com.theagilemonkeys.meets.magento.methods.CustomerAddressList;
import com.theagilemonkeys.meets.magento.methods.CustomerCustomerCreate;
import com.theagilemonkeys.meets.magento.methods.CustomerCustomerInfo;
import com.theagilemonkeys.meets.magento.methods.CustomerCustomerList;
import com.theagilemonkeys.meets.magento.methods.CustomerCustomerUpdate;
import com.theagilemonkeys.meets.magento.models.base.MageMeetsCollectionPojos;
import com.theagilemonkeys.meets.magento.models.base.MageMeetsModel;
import com.theagilemonkeys.meets.models.MeetsAddress;
import com.theagilemonkeys.meets.models.MeetsCustomer;
import com.theagilemonkeys.meets.utils.StringUtils;
import com.theagilemonkeys.meets.utils.soap.Serializable;

import org.jdeferred.Deferred;
import org.jdeferred.DoneCallback;
import org.jdeferred.DonePipe;
import org.jdeferred.Promise;
import org.jdeferred.impl.DeferredObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

/**
 * Android Meets SDK
 * Original work Copyright (c) 2014 [TheAgileMonkeys]
 *
 * @author Álvaro López Espinosa
 */
public class MageMeetsCustomer extends MageMeetsModel<MeetsCustomer> implements MeetsCustomer {
    public final static String MODE_GUEST = "guest";
    public final static String MODE_CUSTOMER = "customer";

    @Key private String mode = MODE_CUSTOMER;
    @Key private int customer_id;
    @Key private String email;
    @Key private String firstname;
    @Key private String lastname;
    @Key private String taxvat;
    @Key private int store_id;
    @Key private int website_id;
    @Key private String password;
    @Key private String password_hash;
    @Key private MageMeetsCollectionPojos.Addresses addresses;

    {
        store_id = Meets.storeId;
        website_id = Meets.websiteId;
    }

    //This is specific of magento
    public MeetsCustomer setMode(String mode){
        this.mode = mode;
        return this;
    }

    @Override
    public MeetsCustomer fetch() {
        ApiMethodModelHelper.DelayedParams params = new ApiMethodModelHelper.DelayedParams() {
            @Override
            public Map<String, Object> buildParams() {
                Map<String, Object> params = new HashMap<String, Object>();
                params.put("customerId", getId());
                return params;
            }
        };

        pushMethod(new CustomerCustomerInfo(), params).always(updateAndTrigger);
        return this;
    }

    @Override
    public MeetsCustomer setId(int id) {
        customer_id = id;
        return this;
    }

    @Override
    public int getId() {
        return customer_id;
    }

    @Override
    public String getFirstName() {
        return firstname;
    }

    @Override
    public String getLastName() {
        return lastname;
    }

    @Override
    public String getFullName() {
        return StringUtils.safeValueOf(firstname) + " " + StringUtils.safeValueOf(lastname);
    }

    @Override
    public String getEmail() {
        return email;
    }

    @Override
    public String getVatNumber() {
        return taxvat;
    }

    @Override
    public String getPasswordHash() {
        return password_hash;
    }

    @Override
    public MeetsAddress getDefaultBillingAddress() {
        if (addresses != null && addresses.size() > 0) {
            for (MeetsAddress address : addresses){
                if ( address.isDefaultBilling() ) return address;
            }
        }
        return null;
    }

    @Override
    public MeetsAddress getDefaultShippingAddress() {
        if (addresses != null && addresses.size() > 0) {
            for (MeetsAddress address : addresses){
                if ( address.isDefaultShipping() ) return address;
            }
        }
        return null;
    }

    @Override
    public List<MeetsAddress> getAddresses() {
        List<MeetsAddress> res = new ArrayList<MeetsAddress>();
        if (addresses != null) res.addAll(addresses);
        return res;
    }

    private MeetsCustomer create() {
        ApiMethodModelHelper.DelayedParams params = new ApiMethodModelHelper.DelayedParams() {
            @Override
            public Map<String, Object> buildParams() {
                Map<String, Object> params = new HashMap<String, Object>();
                params.put("customerData", MageMeetsCustomer.this);
                return params;
            }
        };

        forceNextCacheToBe(false);
        pushMethod(new CustomerCustomerCreate(), params).always(updateAndTrigger);
        return this;
    }

    @Override
    public MeetsCustomer save() {
        if (isNew()){
            return create();
        }
        ApiMethodModelHelper.DelayedParams params = new ApiMethodModelHelper.DelayedParams() {
            @Override
            public Map<String, Object> buildParams() {
                Map<String, Object> params = new HashMap<String, Object>();
                params.put("customerId", getId());
                params.put("customerData", MageMeetsCustomer.this);
                return params;
            }
        };

        forceNextCacheToBe(false);
        pushMethod(new CustomerCustomerUpdate(), params).always(onlyTrigger);
        return this;
    }

    @Override
    public MeetsCustomer fetchByEmail(final String email) {
        return internalFetchByEmail(email, null);
    }

    @Override
    public MeetsCustomer fetchByEmailAndCheckPassword(String email, String password) {
        return internalFetchByEmail(email, password);
    }

    private MeetsCustomer internalFetchByEmail(final String email, @Nullable final String password) {
        ApiMethodModelHelper.DelayedParams params = new ApiMethodModelHelper.DelayedParams() {
            @Override
            public Map<String, Object> buildParams() {
                List<Map<String, Object>> complexFilter = new Serializable.List<Map<String, Object>>();
                    //Email filter key
                    Map<String, Object> filter = new Serializable.Map<String, Object>();
                        //Email filter value
                        Map<String, Object> filterValue = new Serializable.Map<String, Object>();
                        filterValue.put("key", "eq");
                        filterValue.put("value", email);
                    filter.put("key", "email");
                    filter.put("value", filterValue);
                complexFilter.add(filter);

                Map<String, Object> filters = new Serializable.Map<String, Object>();
                filters.put("complex_filter", complexFilter);

                Map<String, Object> params = new HashMap<String, Object>();
                params.put("filters", filters);
                return params;
            }
        };

        forceNextCacheToBe(false);
        pushMethod(new CustomerCustomerList(), params);
        pushPipe(new DonePipe() {
            @Override
            public Deferred pipeDone(Object o) {
                MageMeetsCollectionPojos.Customers customers = (MageMeetsCollectionPojos.Customers) o;
                Exception e;
                if (customers.size() == 0) {
                    e = new Exception("Customer not found");
                }
                else{
                    MeetsCustomer customer = customers.get(0);
                    if (password == null || internalCheckPassword(password, customer.getPasswordHash())) {
                        updateAndTrigger.onAlways(Promise.State.RESOLVED, customer, null);
                        return new DeferredObject().resolve(MageMeetsCustomer.this);
                    }
                    e = new Exception("Incorrect password");
                }

                updateAndTrigger.onAlways(Promise.State.REJECTED, null, e);
                return new DeferredObject().reject(e);
            }
        }, null);
        return this;
    }

    @Override
    public boolean checkPassword(String password) {
        return checkPassword(password);
    }

    private boolean internalCheckPassword(String password, String passwordHash){
        String[] pass_salt = passwordHash.split(":");
        String pass = pass_salt[0];
        String salt = pass_salt[1];
        String typedPassword = StringUtils.MD5Hash(salt + password);
        return pass.equals(typedPassword);
    }

    @Override
    public MeetsCustomer fetchAddresses() {
        ApiMethodModelHelper.DelayedParams params = new ApiMethodModelHelper.DelayedParams() {
            @Override
            public Map<String, Object> buildParams() {
                Map<String,Object> params = new HashMap<String, Object>();
                params.put("customerId", getId());
                return params;
            }
        };

        pushMethod(new CustomerAddressList(), params)
                .done(new DoneCallback() {
                    @Override
                    public void onDone(Object o) {
                        addresses = (MageMeetsCollectionPojos.Addresses) o;
                    }
                })
                .always(onlyTrigger);
        return this;
    }

    @Override
    public MeetsCustomer addAddress(final MeetsAddress meetsAddress) {
        ApiMethodModelHelper.DelayedParams params = new ApiMethodModelHelper.DelayedParams() {
            @Override
            public Map<String, Object> buildParams() {
                Map<String, Object> params = new HashMap<String, Object>();
                params.put("customerId", getId());
                params.put("addressData", meetsAddress);
                return params;
            }
        };

        forceNextCacheToBe(false);
        pushMethod(new CustomerAddressCreate(), params)
                .done(new DoneCallback() {
                    @Override
                    public void onDone(Object o) {
                        MeetsAddress resultAddress = (MeetsAddress) o;
                        meetsAddress.setId(resultAddress.getId());
                        if (addresses == null) addresses = new MageMeetsCollectionPojos.Addresses();

                        // Update the default billing and shipping state in the other addresses
                        for (MeetsAddress address : addresses) {
                            if (meetsAddress.isDefaultBilling() && address.isDefaultBilling())
                                address.setDefaultBilling(false);
                            if (meetsAddress.isDefaultShipping() && address.isDefaultShipping())
                                address.setDefaultShipping(false);
                        }

                        addresses.add((MageMeetsAddress) meetsAddress);
                    }
                })
                .always(onlyTrigger);
        return this;
    }

    @Override
    public MeetsCustomer removeAddress(final int addressId) {
        //TODO: Remove the address locally before the request (as with cart items) to avoid remove addresses not present in the customer
        ApiMethodModelHelper.DelayedParams params = new ApiMethodModelHelper.DelayedParams() {
            @Override
            public Map<String, Object> buildParams() {
                Map<String, Object> params = new HashMap<String, Object>();
                params.put("addressId", addressId);
                return params;
            }
        };

        forceNextCacheToBe(false);
        pushMethod(new CustomerAddressDelete(), params)
                .done(new DoneCallback() {
                    @Override
                    public void onDone(Object o) {
                        for(int i = 0; i < addresses.size(); ++i)
                        {
                            MeetsAddress address = addresses.get(i);
                            if (address.getId() == addressId){
                                addresses.remove(i);
                                return;
                            }
                        }
                    }
                })
                .always(onlyTrigger);
        return this;
    }

    @Override
    public MeetsCustomer setFirstName(String firstName) {
        this.firstname = firstName;
        return this;
    }

    @Override
    public MeetsCustomer setLastName(String lastname) {
        this.lastname = lastname;
        return this;
    }

    @Override
    public MeetsCustomer setEmail(String email) {
        this.email = email;
        return this;
    }

    @Override
    public MeetsCustomer setVatNumber(String taxvat) {
        this.taxvat = taxvat;
        return this;
    }

    @Override
    public MeetsCustomer setPassword(String password) {
        this.password = password;
        return this;
    }
}
