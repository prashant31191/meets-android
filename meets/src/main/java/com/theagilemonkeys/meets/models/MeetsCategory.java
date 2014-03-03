package com.theagilemonkeys.meets.models;

import com.theagilemonkeys.meets.models.base.MeetsModel;

import java.util.List;

/**
 * Android Meets SDK
 * Original work Copyright (c) 2014 [TheAgileMonkeys]
 *
 * @author Álvaro López Espinosa
 */
public interface MeetsCategory extends MeetsModel<MeetsCategory> {
    int getParentId();
    String getName();
    boolean isActive();
    int getPosition();
    int getLevel();
    List<MeetsCategory> getChildren();

    MeetsCategory fetchChildren();
    MeetsCategory fetchWithDescendants();
}
