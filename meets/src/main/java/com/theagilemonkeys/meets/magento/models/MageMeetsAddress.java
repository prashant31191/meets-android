package com.theagilemonkeys.meets.magento.models;

import com.google.api.client.util.Key;
import com.theagilemonkeys.meets.ApiMethodModelHelper;
import com.theagilemonkeys.meets.magento.methods.CustomerAddressDelete;
import com.theagilemonkeys.meets.magento.methods.CustomerAddressInfo;
import com.theagilemonkeys.meets.magento.methods.CustomerAddressUpdate;
import com.theagilemonkeys.meets.magento.models.base.MageMeetsModel;
import com.theagilemonkeys.meets.models.MeetsAddress;

import java.util.HashMap;
import java.util.Map;

import static com.theagilemonkeys.meets.utils.StringUtils.safeValueOf;

/**
 * Created by kloster on 3/10/13.
 */
public class MageMeetsAddress extends MageMeetsModel<MeetsAddress> implements MeetsAddress {
    final static public String MODE_BILLING = "billing";
    final static public String MODE_SHIPPING = "shipping";

    @Key private Integer customer_address_id;
    @Key private String address_id; // The same as customer_address_id.
    @Key private String mode;
    @Key private String firstname;
    @Key private String middlename;
    @Key private String lastname;
    @Key private String prefix;
    @Key private String suffix;
    @Key private String email;
    @Key private String company;
    @Key private Object street; //Something really weird happens when use String as type field. The server does not recognizes  it
    @Key private String city;
    @Key private int region_id;
    @Key private String region;
    @Key private String postcode;
    @Key private String country_id;
    @Key private String telephone;
    @Key private String fax;
    @Key private boolean is_default_billing;
    @Key private boolean is_default_shipping;

    //This is specific of magento
    public MeetsAddress setMode(String mode) {
        this.mode = mode;
        return this;
    }

    @Override
    public MeetsAddress fetch() {
        ApiMethodModelHelper.DelayedParams params = new ApiMethodModelHelper.DelayedParams() {
            @Override
            public Map<String, Object> buildParams() {
                Map<String, Object> params = new HashMap<String, Object>();
                params.put("addressId", getId());
                return params;
            }
        };


        pushMethod(new CustomerAddressInfo(), params).always(updateAndTrigger);
        return this;
    }

    @Override
    public MeetsAddress setId(int id) {
        customer_address_id = id;
        address_id = String.valueOf(id);
        return this;
    }

    @Override
    public int getId() {
        return customer_address_id > 0 ? customer_address_id : Integer.parseInt(address_id);
    }

    @Override
    public MeetsAddress setFirstName(String firstname) {
        this.firstname = firstname;
        return this;
    }

    @Override
    public MeetsAddress setLastName(String lastname) {
        this.lastname = lastname;
        return this;
    }

    @Override
    public MeetsAddress setCompany(String company) {
        this.company = company;
        return this;
    }

    @Override
    public MeetsAddress setStreet(String street) {
        this.street = street;
        return this;
    }

    @Override
    public MeetsAddress setCity(String city) {
        this.city = city;
        return this;
    }

    @Override
    public MeetsAddress setRegionId(int regionId) {
        this.region_id = regionId;
        return this;
    }

    @Override
    public MeetsAddress setRegion(String region) {
        this.region = region;
        return this;
    }

    @Override
    public MeetsAddress setPostCode(String postcode) {
        this.postcode = postcode;
        return this;
    }

    @Override
    public MeetsAddress setCountryId(String countryId) {
        country_id = countryId;
        return this;
    }

    @Override
    public MeetsAddress setTelephone(String telephone) {
        this.telephone = telephone;
        return this;
    }

    @Override
    public MeetsAddress setFax(String fax) {
        this.fax = fax;
        return this;
    }

    @Override
    public MeetsAddress setDefaultBilling(boolean defaultBilling) {
//        is_default_billing = defaultBilling ? 1 : 0;
        is_default_billing = defaultBilling;
        return this;
    }

    @Override
    public MeetsAddress setDefaultShipping(boolean defaultShipping) {
//        is_default_shipping = defaultShipping ? 1 : 0;
        is_default_shipping = defaultShipping;
        return this;
    }

    @Override
    public MeetsAddress save() {
        ApiMethodModelHelper.DelayedParams params = new ApiMethodModelHelper.DelayedParams() {
            @Override
            public Map<String, Object> buildParams() {
                Map<String, Object> params = new HashMap<String, Object>();
                params.put("addressId", getId());
                params.put("addressData", MageMeetsAddress.this);
                return params;
            }
        };

        forceNextCacheToBe(false);
        pushMethod(new CustomerAddressUpdate(), params).always(onlyTrigger);
        return this;
    }

    @Override
    public MeetsAddress remove() {
        ApiMethodModelHelper.DelayedParams params = new ApiMethodModelHelper.DelayedParams() {
            @Override
            public Map<String, Object> buildParams() {
                Map<String, Object> params = new HashMap<String, Object>();
                params.put("addressId", getId());
                return params;
            }
        };

        forceNextCacheToBe(false);
        pushMethod(new CustomerAddressDelete(), params).always(onlyTrigger);
        return this;
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
    public String getCompany() {
        return company;
    }

    @Override
    public String getStreet() {
        return (String) street;
    }

    @Override
    public String getCity() {
        return city;
    }

    @Override
    public int getRegionId() {
        return region_id;
    }

    @Override
    public String getRegion() {
        return region;
    }

    @Override
    public String getPostCode() {
        return postcode;
    }

    @Override
    public String getCountryId() {
        return country_id;
    }

    @Override
    public String getTelephone() {
        return telephone;
    }

    @Override
    public String getFax() {
        return fax;
    }

    @Override
    public boolean isDefaultBilling() {
        return is_default_billing;
    }

    @Override
    public boolean isDefaultShipping() {
        return is_default_shipping;
    }

    @Override
    public String toString() {
        String res = safeValueOf(getFirstName()) + ' ' + safeValueOf(getLastName()) + '\n' +
                     safeValueOf(getStreet()) + '\n' +
                     safeValueOf(getCity()) + ", " + safeValueOf(getRegion()) + ", " + safeValueOf(getPostCode()) + '\n' +
                     safeValueOf(getCountryId()) + '\n' +
                     safeValueOf(getTelephone());
        return res;
    }
}
