package com.theagilemonkeys.meets.magento.models;

import com.google.api.client.util.Key;
import com.theagilemonkeys.meets.ApiMethodModelHelper;
import com.theagilemonkeys.meets.magento.methods.CatalogCategoryInfo;
import com.theagilemonkeys.meets.magento.methods.CatalogCategoryLevel;
import com.theagilemonkeys.meets.magento.methods.CatalogCategoryTree;
import com.theagilemonkeys.meets.magento.models.base.MageMeetsCollectionPojos;
import com.theagilemonkeys.meets.magento.models.base.MageMeetsModel;
import com.theagilemonkeys.meets.models.MeetsCategory;
import com.theagilemonkeys.meets.utils.StringUtils;

import org.jdeferred.DoneCallback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Android Meets SDK
 * Original work Copyright (c) 2014 [TheAgileMonkeys]
 *
 * @author Álvaro López Espinosa
 */
public class MageMeetsCategory extends MageMeetsModel<MeetsCategory> implements MeetsCategory {
    @Key
    private int category_id;
    @Key
    private String name;
    @Key
    private int position;
    @Key
    private int level;
    @Key
    private int parent_id;
    @Key
    private int is_active;
    @Key
    private MageMeetsCollectionPojos.Categories children = new MageMeetsCollectionPojos.Categories();

    @Override
    public MeetsCategory fetch() {
        ApiMethodModelHelper.DelayedParams params = new ApiMethodModelHelper.DelayedParams() {
            @Override
            public Map<String, Object> buildParams() {
                Map<String, Object> params = new HashMap<String, Object>();
                params.put("categoryId", getId());
                return params;
            }
        };
        pushMethod(new CatalogCategoryInfo(), params).always(updateAndTrigger);
        return this;
    }

    @Override
    public MeetsCategory fetchChildren() {
        ApiMethodModelHelper.DelayedParams params = new ApiMethodModelHelper.DelayedParams() {
            @Override
            public Map<String, Object> buildParams() {
                Map<String, Object> params = new HashMap<String, Object>();
                params.put("parentCategory", category_id);
                return params;
            }
        };
        pushMethod(new CatalogCategoryLevel(), params)
                .done(new DoneCallback() {
                    @Override
                    public void onDone(Object result) {
                        children = (MageMeetsCollectionPojos.Categories) result;
                    }
                })
                .always(onlyTrigger);
        return this;
    }

    @Override
    public MeetsCategory fetchWithDescendants() {
        ApiMethodModelHelper.DelayedParams params = new ApiMethodModelHelper.DelayedParams() {
            @Override
            public Map<String, Object> buildParams() {
                Map<String, Object> params = new HashMap<String, Object>();
                params.put("parentId", category_id);
                return params;
            }
        };
        pushMethod(new CatalogCategoryTree(), params).always(updateAndTrigger);
        return this;
    }

    @Override
    public MeetsCategory setId(int id) {
        category_id = id;
        return this;
    }

    @Override
    public int getId() {
        return category_id;
    }

    @Override
    public int getParentId() {
        return parent_id;
    }

    @Override
    public String getName() {
        return StringUtils.toUpperCaseFirst(name);
    }

    @Override
    public boolean isActive() {
        return is_active > 0;
    }

    @Override
    public int getPosition() {
        return position;
    }

    @Override
    public int getLevel() {
        return level;
    }

    @Override
    public List<MeetsCategory> getChildren() {
        return new ArrayList<MeetsCategory>(children);
    }

//    @Override
//    protected void updateFromFetchedResult(Object fetchedResult) {
//        if ( onlyChildren )
//            children = (MageMeetsCollectionPojos.Categories) fetchedResult;
//        else
//            super.updateFromFetchedResult(fetchedResult);
//    }
}
