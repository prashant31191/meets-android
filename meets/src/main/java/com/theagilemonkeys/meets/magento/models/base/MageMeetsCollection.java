package com.theagilemonkeys.meets.magento.models.base;

import com.theagilemonkeys.meets.ApiMethod;
import com.theagilemonkeys.meets.ApiMethodModelHelper;
import com.theagilemonkeys.meets.models.base.MeetsCollection;
import com.theagilemonkeys.meets.models.base.MeetsListener;
import com.theagilemonkeys.meets.models.base.MeetsModel;

import org.jdeferred.AlwaysCallback;
import org.jdeferred.Deferred;
import org.jdeferred.DonePipe;
import org.jdeferred.FailPipe;
import org.jdeferred.Promise;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by kloster on 16/10/13.
 */

public class MageMeetsCollection<MODEL extends MeetsModel> extends ArrayList<MODEL> implements MeetsCollection<MODEL> {
    private transient ApiMethodModelHelper<MeetsCollection<MODEL>> apiMethodCtrl = new ApiMethodModelHelper<MeetsCollection<MODEL>>(this);
    private Class<? extends ApiMethod> fetchApiMethodClass;
    protected int page = 1;
    protected int pageSize = 20;
    protected boolean resetOnFetch = false;
    protected Map<String, Object> filters = new HashMap<String, Object>();

    public MageMeetsCollection(Class<? extends ApiMethod> fetchApiMethodClass) {
        this.fetchApiMethodClass = fetchApiMethodClass;
    }

    @Override
    public MeetsCollection<MODEL> fetch() {
        ApiMethodModelHelper.DelayedParams params = new ApiMethodModelHelper.DelayedParams() {
            @Override
            public Map<String, Object> buildParams() {
                filters.put("page", page);
                filters.put("limit", pageSize);
                return filters;
            }
        };

        try {
            ApiMethod apiMethod = fetchApiMethodClass.getConstructor(boolean.class)
                                                     .newInstance(true);

            pushMethod(apiMethod, params).always(updateAndTrigger);
            return this;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public MeetsCollection<MODEL> insert(MODEL model) {
        int oldIndexModel = getIndex(model.getId());

        if ( oldIndexModel < 0 ){
            add(model);
            // Trigger MeetsCollection specific listeners
            Map<MeetsListener<MeetsCollection<MODEL>>, Boolean> listeners = apiMethodCtrl.getListenersWithKeepInfo();
            for ( MeetsListener<MeetsCollection<MODEL>> listener : listeners.keySet()) {
                if (listener instanceof MeetsListener.Collection)
                    ((MeetsListener.Collection<MeetsCollection<MODEL>, MODEL>) listener).onPush(this, model);
            }
        }
        return this;
    }

    @Override
    public MODEL extract(int modelId) {
        MODEL model = null;
        int oldIndexModel = getIndex(modelId);
        if ( oldIndexModel >= 0 ) {
            model = remove(oldIndexModel);
            // Trigger MeetsCollection specific listeners
            Map<MeetsListener<MeetsCollection<MODEL>>, Boolean> listeners = apiMethodCtrl.getListenersWithKeepInfo();
            for ( MeetsListener<MeetsCollection<MODEL>> listener : listeners.keySet()) {
                if (listener instanceof MeetsListener.Collection)
                    ((MeetsListener.Collection<MeetsCollection<MODEL>, MODEL>) listener).onExtract(this, model);
            }
        }
        return model;
    }

    @Override
    public MeetsCollection<MODEL> reset() {
        // We have to extract models manually to trigger events
        for (MeetsModel meetsModel : this){
            extract(meetsModel.getId());
        }
        // Trigger MeetsCollection specific listeners
        Map<MeetsListener<MeetsCollection<MODEL>>, Boolean> listeners = apiMethodCtrl.getListenersWithKeepInfo();
        for ( MeetsListener<MeetsCollection<MODEL>> listener : listeners.keySet()) {
            if (listener instanceof MeetsListener.Collection)
                ((MeetsListener.Collection<MeetsCollection<MODEL>, MODEL>) listener).onReset(this);
        }
        return this;
    }

    @Override
    public int getSize() {
        return size();
    }

    @Override
    public MODEL get(int index) {
        return super.get(index);
    }

    public int getIndex(int modelId){
        int n = size();
        for( int i = 0; i < n; ++i)  {
            MODEL model = get(i);
            if ( model.getId() == modelId )
                return i;
        }
        return -1;
    }

    @Override
    public MeetsCollection<MODEL> nextPage() {
        ++page;
        return this;
    }

    @Override
    public MeetsCollection<MODEL> setPage(int page) {
        this.page = page;
        return this;
    }

    @Override
    public MeetsCollection<MODEL> setPageSize(int pageSize) {
        this.pageSize = pageSize;
        return this;
    }

    @Override
    public MeetsCollection<MODEL> setResetOnFetch(boolean reset) {
        resetOnFetch = reset;
        return this;
    }

    @Override
    public MeetsCollection<MODEL> setFilters(Map<String, Object> filters) {
        this.filters = filters;
        return this;
    }

    @Override
    public int getPage() {
        return page;
    }

    @Override
    public int getPageSize() {
        return pageSize;
    }

    @Override
    public MeetsCollection<MODEL> include(String... weakAttributes) {
        throw new UnsupportedOperationException("Still not implemented");
    }

    protected transient AlwaysCallback updateAndTrigger = new AlwaysCallback() {
        @Override
        public void onAlways(Promise.State state, Object resolved, Object rejected) {
            if (state == Promise.State.RESOLVED) updateFromFetchedResult(resolved);
            triggerListeners((Exception) rejected);
        }
    };

    public void updateFromFetchedResult(Object result) {
        Collection<MODEL> models;
        if (result instanceof Map){
            // Rest results comes in Map format
            models = ( (Map<String, MODEL>) result).values();
        }
        else{
            //Soap result comes in collection format
            models = (Collection<MODEL>) result;
        }

        if (resetOnFetch){
            reset();
        }

        for (MODEL model : models){
            insert(model);
        }
    }

    /////////////////////////////////////////////////////////////////////////
    //////////////////// ApiMethodModelHelper delegation /////////////////////
    /////////////////////////////////////////////////////////////////////////


    public Promise pushMethod(ApiMethod method) {
        return apiMethodCtrl.pushMethod(method);
    }

    public ApiMethodModelHelper<MeetsCollection<MODEL>> forceNextCacheToBe(boolean enable) {
        return apiMethodCtrl.forceNextCacheToBe(enable);
    }

    public Promise pushPipe(DonePipe donePipe, FailPipe failPipe) {
        return apiMethodCtrl.pushPipe(donePipe, failPipe);
    }

    public Map<MeetsListener<MeetsCollection<MODEL>>,Boolean> getListenersWithKeepInfo() {
        return apiMethodCtrl.getListenersWithKeepInfo();
    }

    public void triggerListeners(Exception e) {
        apiMethodCtrl.triggerListeners(e);
    }

    public Promise pushMethod(ApiMethod method, ApiMethodModelHelper.DelayedParams params) {
        return apiMethodCtrl.pushMethod(method, params);
    }

    public Promise pushDeferred(Deferred deferred) {
        return apiMethodCtrl.pushDeferred(deferred);
    }

    public ApiMethodModelHelper<MeetsCollection<MODEL>> setModelCache(boolean enable) {
        return apiMethodCtrl.setModelCache(enable);
    }

    public void triggerListeners() {
        apiMethodCtrl.triggerListeners();
    }

    @Override
    public MeetsCollection<MODEL> await(MeetsListener<MeetsCollection<MODEL>> listener) {
        return apiMethodCtrl.await(listener);
    }

    @Override
    public MeetsCollection<MODEL> await(MeetsListener<MeetsCollection<MODEL>> listener, boolean keepListening) {
        return apiMethodCtrl.await(listener, keepListening);
    }

    @Override
    public MeetsCollection<MODEL> noLongerWait(MeetsListener<MeetsCollection<MODEL>> listener) {
        return apiMethodCtrl.noLongerWait(listener);
    }

    @Override
    public MeetsCollection<MODEL> allNoLongerWait() {
        return apiMethodCtrl.allNoLongerWait();
    }

    @Override
    public MeetsCollection<MODEL> nextWaitForPrevious() {
        return apiMethodCtrl.nextWaitForPrevious();
    }
}
