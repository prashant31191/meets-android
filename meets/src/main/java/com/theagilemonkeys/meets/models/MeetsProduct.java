package com.theagilemonkeys.meets.models;

import com.theagilemonkeys.meets.models.base.MeetsModel;

import java.util.List;
import java.util.Map;

/**
 * Android Meets SDK
 * Original work Copyright (c) 2014 [TheAgileMonkeys]
 *
 * @author Álvaro López Espinosa
 */
public interface MeetsProduct extends MeetsModel<MeetsProduct> {
    String getType();
    String getSku();
    String getName();
    String getDescription();
    double getPrice();
    String getImageUrl();
    Map<String, String> getImageVersions();
    Map<String, String> getAdditionalAttributes();
    List<MeetsProduct> getAssociatedProducts();

    MeetsProduct fetchImageVersions();
    MeetsProduct fetchWithAdditionalAttributes(String... additionalAttributes);
    MeetsProduct fetchWithAdditionalAttributes(List<String> additionalAttributes);
    MeetsProduct fetchAssociatedProducts();

}
