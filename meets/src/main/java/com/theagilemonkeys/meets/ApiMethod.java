package com.theagilemonkeys.meets;

import com.octo.android.robospice.exception.RequestCancelledException;
import com.octo.android.robospice.persistence.DurationInMillis;
import com.octo.android.robospice.persistence.exception.KeySanitationExcepion;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.persistence.keysanitation.DefaultKeySanitizer;
import com.octo.android.robospice.request.googlehttpclient.GoogleHttpClientSpiceRequest;
import com.octo.android.robospice.request.listener.RequestListener;
import com.theagilemonkeys.meets.utils.StringUtils;

import org.jdeferred.Deferred;
import org.jdeferred.impl.DeferredObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Android Meets SDK
 * Original work Copyright (c) 2014 [TheAgileMonkeys]
 *
 * @author Álvaro López Espinosa
 */
public abstract class ApiMethod<RESULT> extends GoogleHttpClientSpiceRequest<RESULT> implements RequestListener<RESULT> {

    /////// Static global configuration. These values are used for all methods by default //////

    /////// CACHE ////////
    public static final AtomicLong globalCacheDuration = new AtomicLong(DurationInMillis.ONE_SECOND);
    public static final AtomicBoolean globalAlwaysGetFromCacheFirst = new AtomicBoolean(true);

    /////// FIXED PARAMS //////
    public static final Map<String,Object> fixedParams = Collections.synchronizedMap(new HashMap<String, Object>());
    public static final List<String> fixedUrlExtraSegments = Collections.synchronizedList(new ArrayList<String>());

    /////// BASIC AUTHORIZATION ///////
    private static String basicAuthName;
    private static String basicAuthPass;

    public static synchronized void setBasicAuth(String name, String pass){
        basicAuthName = name;
        basicAuthPass = pass;
    }
    public static synchronized String getBasicAuthName() {
        return basicAuthName;
    }
    public static synchronized String getBasicAuthPass() {
        return basicAuthPass;
    }

    //////// Instance attributes and methods ///////
    protected List<String> urlExtraSegments;
    protected Map <String, Object> params;
    protected final Class modelClass;
    protected Deferred runDeferred;
    protected long cacheDuration = globalCacheDuration.get();
    protected boolean alwaysGetFromCacheFirst = globalAlwaysGetFromCacheFirst.get();

    public ApiMethod(Class<RESULT> magentoModelClass) {
        super(magentoModelClass);
        modelClass = magentoModelClass;
    }

    /**
     * Set the cache duration for this method. This method will override default cache duration
     * for this method.
     * @param milliseconds The cache duration in milliseconds. If < 0, no cache will be used. If == 0,
     *                     the cache never expires. You will prefer use DurationInMillis constants.
     * @return This method
     */
    public ApiMethod<RESULT> setCacheDuration(long milliseconds) {
        cacheDuration = milliseconds;
        return this;
    }

    /**
     * If you pass true to this method and call "run", it will try to get the data from cache and,
     * regardless of the result, the request is always performed to update cache data (and return it
     * if it was not found before).
     * Note that if data in cache is found and is expired, the response listener will be called twice:
     * once with the dirty data from cache and another with the updated data when the request finish.
     * @param val
     * @return
     */
    public ApiMethod<RESULT> setAlwaysGetFromCacheFirst(boolean val) {
        alwaysGetFromCacheFirst = val;
        return this;
    }

    public ApiMethod<RESULT> clearCache() {
        Meets.spiceManager.removeAllDataFromCache();
        return this;
    }

    public Deferred run(String... urlExtraSegments) {
        return run(null, urlExtraSegments);
    }

    public Deferred run(Map<String, Object> params, String... urlExtraSegments){
        return run(params, Arrays.asList(urlExtraSegments));
    }

    public Deferred run(Map<String, Object> params, List<String> urlExtraSegments){
        runDeferred = new DeferredObject();

        this.params = new HashMap<String, Object>(fixedParams);
        if (params != null)
            this.params.putAll(params);

        this.urlExtraSegments = new ArrayList<String>(fixedUrlExtraSegments);
        if (urlExtraSegments != null)
            this.urlExtraSegments.addAll(urlExtraSegments);

        makeRequest();

        return runDeferred;
    }

    private void makeRequest() {
        if (cacheDuration >= 0){
            if (alwaysGetFromCacheFirst)
                Meets.spiceManager.getFromCacheAndLoadFromNetworkIfExpired(this, getCacheKey(), cacheDuration, this);
            else
                Meets.spiceManager.execute(this, getCacheKey(), cacheDuration, this);
        }
        else {
            Meets.spiceManager.execute(this, this);
        }
    }

    @Override
    public void onRequestFailure(SpiceException e) {
        if(runDeferred.isPending()) {
            runDeferred.reject(e);
            Meets.globalListener.onFail(null, e);
            Meets.globalListener.onAlways(null, e);
        }
    }

    @Override
    public void onRequestSuccess(RESULT response) {
        if(runDeferred.isPending()) {
            runDeferred.resolve(response);
            Meets.globalListener.onDone(null);
            Meets.globalListener.onAlways(null, null);
        }
    }

    public String getCacheKey(){
        String key = null;
        try {
            key = (String) new DefaultKeySanitizer().sanitizeKey(generateUrl());
        } catch (KeySanitationExcepion e) {
            e.printStackTrace();
        }
        return key;
    }

    protected abstract String getBaseUrl();

    protected String generateUrl(){
        return getBaseUrl() + getMethodName() + "/" + generateUrlExtraSegments() + "?" + generateUrlParams();
    }

    protected String getMethodName() {
        return StringUtils.toLowerCaseFirst(getClass().getSimpleName());
    }
    protected String generateUrlExtraSegments(){
        String res = "";
        if ( urlExtraSegments != null ){
            for ( String segment : urlExtraSegments ){
                res += segment + "/";
            }
        }
        return res;
    }

    protected String generateUrlParams(){
        String res = "";
        if ( params != null){
            for (Map.Entry<String, ?> entry : params.entrySet()){
                String key = entry.getKey();
                Object val = entry.getValue();
                if ( val instanceof List){
                    res += generateUrlListParamForKey(key, (List) val);
                }
                else{
                    res += key + "=" + String.valueOf(val) + "&";
                }
            }
        }
        return res;
    }

    protected String generateUrlListParamForKey(String key, List list) {
        String res = "";
        int listIndex = 0;
        for( Object elem : list ){
            String indexedKey = key + "[" + listIndex++ + "]";
            if ( elem instanceof Map){
                for( Map.Entry entry : ((Map<Object,Object>) elem).entrySet()){
                    res += indexedKey + "[" + String.valueOf(entry.getKey()) + "]="
                                      + String.valueOf(entry.getValue()) + "&";
                }
            }
            else{
                res += indexedKey + "=" + String.valueOf(elem) + "&";
            }
        }
        return res;
    }
}
