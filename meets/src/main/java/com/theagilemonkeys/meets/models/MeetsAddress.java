package com.theagilemonkeys.meets.models;

import com.theagilemonkeys.meets.models.base.MeetsModel;
/**
 * Android Meets SDK
 * Original work Copyright (c) 2014 [TheAgileMonkeys]
 *
 * @author Álvaro López Espinosa
 */
public interface MeetsAddress extends MeetsModel<MeetsAddress> {
    String getFirstName();
    String getLastName();
    String getCompany();
    String getStreet();
    String getCity();
    int getRegionId();
    String getRegion();
    String getPostCode();
    String getCountryId();
    String getTelephone();
    String getFax();
    boolean isDefaultBilling();
    boolean isDefaultShipping();

    MeetsAddress setFirstName(String firstname);
    MeetsAddress setLastName(String lastname);
    MeetsAddress setCompany(String company);
    MeetsAddress setStreet(String street);
    MeetsAddress setCity(String city);
    MeetsAddress setRegionId(int regionId);
    MeetsAddress setRegion(String region);
    MeetsAddress setPostCode(String postcode);
    MeetsAddress setCountryId(String countryId);
    MeetsAddress setTelephone(String telephone);
    MeetsAddress setFax(String fax);
    MeetsAddress setDefaultBilling(boolean defaultBilling);
    MeetsAddress setDefaultShipping(boolean defaultShipping);

    MeetsAddress save();
    MeetsAddress remove();
}