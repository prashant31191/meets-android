package com.theagilemonkeys.meets;

import android.content.Context;

import com.octo.android.robospice.SpiceManager;
import com.theagilemonkeys.meets.magento.RestApiMethod;
import com.theagilemonkeys.meets.magento.SoapApiMethod;
import com.theagilemonkeys.meets.models.base.MeetsFactory;
import com.theagilemonkeys.meets.models.base.MeetsListener;

/**
 * Created by kloster on 14/02/14.
 */
public class Meets {
    private static final String restBaseUrl = "/api/rest/";
    private static final String soapBaseUrl = "/api/v2_soap/";
    private static final String soapWsdl = "/api/v2_soap/?wsdl";
    public static int storeId;
    public static int websiteId;

    static MeetsListener globalListener = new MeetsListener.Empty();

    // Package private spiceManager.
    static SpiceManager spiceManager = new SpiceManager(MeetsSpiceService.class);

    /**
     * Initializer method for Meets. You will usually call it in your {@code App.onCreate} method.
     * @param applicationContext The application context
     * @param factory The MeetsFactory implementation instance that will be used.
     *                Now only {@link com.theagilemonkeys.meets.magento.models.base.MageMeetsFactory MageMeetsFactory} is supported
     * @param hostUrl The url of your e-commerce host. For example: {@code "http://www.example.com" }
     * @param soapApiUser The SOAP API user of your host
     * @param soapApiPass The SOAP API password of your host
     */
    public static void init(Context applicationContext, MeetsFactory factory, String hostUrl, String soapApiUser, String soapApiPass) {
        spiceManager.start(applicationContext);
        MeetsFactory.setInstance(factory);
        SoapApiMethod.soapApiUser = soapApiUser;
        SoapApiMethod.soapApiPass = soapApiPass;

        RestApiMethod.baseUrl = hostUrl.replaceAll("/$", "") + restBaseUrl;
        SoapApiMethod.baseUrl = hostUrl.replaceAll("/$", "") + soapBaseUrl;
        SoapApiMethod.soapNamespace = hostUrl.replaceAll("/$", "") + soapWsdl;
    }

    public static void init(Context applicationContext, MeetsFactory factory, String hostUrl, String soapApiUser, String soapApiPassword,
                            int storeId, int websiteId) {
        init(applicationContext, factory, hostUrl, soapApiUser, soapApiPassword);
        Meets.storeId = storeId;
        Meets.websiteId = websiteId;
        // In Magento Soap API, "store" param has different names among functions
        ApiMethod.fixedParams.put("store", storeId);
        ApiMethod.fixedParams.put("storeId", storeId);
        ApiMethod.fixedParams.put("storeView", storeId);
    }

    public static void init(Context applicationContext, MeetsFactory factory, String hostUrl, String soapApiUser, String soapApiPass,
                            int storeId, int websiteId, String basicAuthUser, String basicAuthPassword) {
        init(applicationContext, factory, hostUrl, soapApiUser, soapApiPass, storeId, websiteId);
        RestApiMethod.setBasicAuth(basicAuthUser, basicAuthPassword);
        SoapApiMethod.setBasicAuth(basicAuthUser, basicAuthPassword);
    }

    /**
     * Sets a global listener that it's always called after any specific listener.
     * @param listener A instance of MeetsListener. Be careful if you pass a non static inner class instance here,
     *                 because it keeps a reference to parent class. If it is an Activity, there will be a potential
     *                 memory leak (This doesn't happen when parent class is the Application)
     */
    public static void setGlobalListener(MeetsListener listener) {
        globalListener = listener;
    }
}