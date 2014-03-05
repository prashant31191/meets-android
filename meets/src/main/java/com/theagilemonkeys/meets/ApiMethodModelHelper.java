package com.theagilemonkeys.meets;

import com.octo.android.robospice.persistence.DurationInMillis;
import com.theagilemonkeys.meets.models.base.MeetsListener;

import org.jdeferred.Deferred;
import org.jdeferred.DonePipe;
import org.jdeferred.FailPipe;
import org.jdeferred.Promise;
import org.jdeferred.impl.DeferredObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Android Meets SDK
 * Original work Copyright (c) 2014 [TheAgileMonkeys]
 *
 * @author Álvaro López Espinosa
 */
public class ApiMethodModelHelper<MODEL> implements ApiMethodModelHelperInterface<MODEL> {

    protected transient Map<MeetsListener<MODEL>, Boolean> listenersWithKeepInfo = new HashMap<MeetsListener<MODEL>, Boolean>();
    protected transient Deferred masterPromise = new DeferredObject().resolve(this);
    private boolean serially = false;
    private Boolean forceNextCacheEnable = null;
    private boolean modelCacheEnable = true;
    private MODEL model;

    public ApiMethodModelHelper(MODEL model){
        this.model = model;
    }

    public Promise pushMethod(final ApiMethod method){
        return pushMethod(method, new DelayedParams() {
            @Override public Map<String, Object> buildParams() { return null; }
            @Override public List<String> buildUrlExtraSegments() { return null; }
        });
    }

    public Promise pushMethod(final ApiMethod method, final DelayedParams params) {
        // With this clear all references to previous onDone, onFail and onAlways callbacks, avoiding possible
        // memory leaks
        if (! masterPromise.isPending())
            masterPromise = new DeferredObject().resolve(model);

        if ( ! modelCacheEnable ) {
            method.setCacheDuration(DurationInMillis.ALWAYS_EXPIRED);
        }

        if ( forceNextCacheEnable != null ) {
            method.setCacheDuration(forceNextCacheEnable ? ApiMethod.globalCacheDuration.get() : DurationInMillis.ALWAYS_EXPIRED);
            forceNextCacheEnable = null;
        }

        if ( serially ){
            serially = false;
            return pushPipe(new DonePipe() {
                                @Override
                                public Deferred pipeDone(Object result) {
                                    return method.run(params.buildParams(), params.buildUrlExtraSegments());
                                }
                            }, new FailPipe() {
                                @Override
                                public Deferred pipeFail(Object o) {
                                    return new DeferredObject().reject(o);
                                }
                            });
        }
        else{
            return pushDeferred(method.run(params.buildParams(), params.buildUrlExtraSegments()));
        }
    }

    public Promise pushDeferred(final Deferred deferred){
        masterPromise = (Deferred) masterPromise.then(new DonePipe() {
            @Override
            public Deferred pipeDone(Object result) {
                return deferred;
            }
        }, new FailPipe() {
            @Override
            public Deferred pipeFail(Object result) {
                return new DeferredObject().reject(result);
            }
        });

        return masterPromise;
    }

    public Promise pushPipe(DonePipe donePipe, FailPipe failPipe){
        masterPromise = (Deferred) masterPromise.then(donePipe,failPipe);
        return masterPromise;
    }

    @Override
    public MODEL await(MeetsListener<MODEL> listener) {
        await(listener, false);
        return model;
    }

    @Override
    public MODEL await(MeetsListener<MODEL> listener, boolean keepListening) {
        if ( ! masterPromise.isPending() ){
            Exception e = new Exception("Last operation failed");
            if ( masterPromise.isResolved() ) listener.onDone(model);
            else if (masterPromise.isRejected()) listener.onFail(model, e);
            listener.onAlways(model, e);
            // Avoid register the listener if user don't want to keep it
            if (! keepListening ) return model;
        }

        listenersWithKeepInfo.put(listener, keepListening);
        return model;
    }

    @Override
    public MODEL noLongerWait(MeetsListener<MODEL> listener) {
        listenersWithKeepInfo.remove(listener);
        return model;
    }

    @Override
    public MODEL allNoLongerWait() {
        listenersWithKeepInfo.clear();
        return model;
    }

    //////// Methods called after fetched ///////

    public void triggerListeners() {
        triggerListeners(null);
    }
    public void triggerListeners(Exception e) {
        if (masterPromise.isPending()) return;
        Iterator<Map.Entry<MeetsListener<MODEL>, Boolean>> entries = listenersWithKeepInfo.entrySet().iterator();
        while (entries.hasNext()) {
            Map.Entry<MeetsListener<MODEL>, Boolean> entry = entries.next();
            MeetsListener<MODEL> listener = entry.getKey();
            Boolean keepListening = entry.getValue();

            if (! keepListening ) entries.remove(); // Remove the last value returned by next()

            if ( e == null ) listener.onDone(model);
            else listener.onFail(model, e);
            listener.onAlways(model, e);
        }
    }

    public Map<MeetsListener<MODEL>, Boolean> getListenersWithKeepInfo() {
        return listenersWithKeepInfo;
    }

    @Override
    public MODEL nextWaitForPrevious() {
        serially = true;
        return model;
    }

    /**
     * Force to enable or disable cache during the next operations, bypassing model cache. After that,
     * the cache will return to its previous state (see {@link #setModelCache(boolean)} for details}
     * @return This model
     */
    public MODEL forceNextCacheToBe(boolean enable) {
        forceNextCacheEnable = enable;
        return model;
    }

    /**
     * Disable or enable the cache for all operations in this model. Note that when model cache is enabled,
     * the configuration for it will be taken from ApiMethod, so if cache is disabled in ApiMethod, this
     * functions does nothing.
     * By default is enabled
     * @param enable Whether to enable or disable cache
     * @return This object
     */
    public MODEL setModelCache(boolean enable) {
        modelCacheEnable = enable;
        return model;
    }

    public static abstract class DelayedParams {
        public Map<String, Object> buildParams() {
            return null;
        }
        public List<String> buildUrlExtraSegments() {
            return null;
        }
    }
}
