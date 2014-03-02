package com.theagilemonkeys.meets.models;

import com.theagilemonkeys.meets.models.base.MeetsModel;

import java.util.List;

/**
 * Created by kloster on 10/10/13.
 */
public interface MeetsCustomer extends MeetsModel<MeetsCustomer> {
    String getEmail();
    String getFirstName();
    String getLastName();
    String getFullName();
    String getVatNumber();
    String getPasswordHash();
    List<MeetsAddress> getAddresses();
    MeetsAddress getDefaultBillingAddress();
    MeetsAddress getDefaultShippingAddress();

    MeetsCustomer setEmail(String email);
    MeetsCustomer setFirstName(String firstName);
    MeetsCustomer setLastName(String lastname);
    MeetsCustomer setVatNumber(String vatNumber);
    MeetsCustomer setPassword(String password);

    MeetsCustomer fetchByEmail(String email);
    MeetsCustomer fetchByEmailAndCheckPassword(String email, String password);
    boolean checkPassword(String password);
    MeetsCustomer save();
    MeetsCustomer fetchAddresses();
    MeetsCustomer addAddress(MeetsAddress meetsAddress);
    MeetsCustomer removeAddress(int addressId);
}


