package com.theagilemonkeys.meets.models;

import com.theagilemonkeys.meets.models.base.MeetsModel;

import java.util.List;

/**
 * Created by kloster on 10/10/13.
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
