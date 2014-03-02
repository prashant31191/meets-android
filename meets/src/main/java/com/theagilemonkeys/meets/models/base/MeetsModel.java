package com.theagilemonkeys.meets.models.base;

import com.theagilemonkeys.meets.ApiMethodModelHelperInterface;

import java.io.Serializable;

/**
 * Created by kloster on 10/10/13.
 */
public interface MeetsModel<MODEL> extends ApiMethodModelHelperInterface<MODEL>, Serializable {
    MODEL fetch();
    MODEL fetch(int id);

    MODEL setId(int id);
    int getId();

    /**
     * TODO
     * @param weakAttributes
     * @return
     */
    MODEL include(String... weakAttributes);

    /**
     * Returns true if the model still does not have a valid id.
     */
    boolean isNew();

    /**
     * Copy all non-null properties from the passed model to this. Listeners are also ignored, so listeners
     * attached to this will be the same after calling this function.
     * @param model The model to copy from
     * @return This model
     */
     MODEL shallowCopyFrom(MODEL model);
}
